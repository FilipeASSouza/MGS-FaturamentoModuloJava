package br.com.sankhya.mgs.ct.processamento;


import br.com.lugh.performance.PerformanceMonitor;
import br.com.sankhya.bh.utils.FilaUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.Processar;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;
import com.sankhya.util.FinalWrapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProcessamentoFilaModel {
    private static Map<String, ProcessamentoFilaModel> instancias = new HashMap<>();
    LinkedBlockingQueue<Runnable> q = new LinkedBlockingQueue<>(1000);
    BlockingThreadPoolExecutor tp;
    private String nomeFila;
    private String nomeConsulta;
    private String nomeParametro;
    private JdbcWrapper jdbcWrapper;
    final Set<FilaPojo> fila = new HashSet<>();
    
    public void stop() {
        tp.shutdownNow();
    }
    
    public static void stopAll() {
        for (Map.Entry<String, ProcessamentoFilaModel> model : instancias.entrySet()) {
            model.getValue().stop();
        }
    }
    
    private ProcessamentoFilaModel(String fila, JdbcWrapper jdbc) {
        this.jdbcWrapper = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
        nomeFila = fila;
        switch (fila) {
            case "normal":
                nomeConsulta = "buscaFilaProcessamento.sql";
                break;
            case "fatura":
                nomeConsulta = "buscaFilaProcessamentoFatura.sql";
                break;
            case "gestor":
                nomeConsulta = "buscaFilaProcessamentoGestor.sql";
                break;
            case "fiscal":
                nomeConsulta = "buscaFilaProcessamentoFiscal.sql";
                break;
        }
        switch (fila) {
            case "normal":
            case "fatura":
            case "gestor":
                nomeParametro = "MGSQTDEXECFILA";
                break;
            case "fiscal":
                nomeParametro = "QTDEXECFILAFISC";
                break;
        }
        BigDecimal quantidadeExecucaoParalela;
        try {
            quantidadeExecucaoParalela = (BigDecimal) MGECoreParameter.getParameter("MGSQTDEXECPARALE");
            if (quantidadeExecucaoParalela == null) {
                quantidadeExecucaoParalela = new BigDecimal(10);
            }
        } catch (Exception e) {
            quantidadeExecucaoParalela = new BigDecimal(10);
        }
        
        tp = new BlockingThreadPoolExecutor(10, quantidadeExecucaoParalela.intValue(), 1, TimeUnit.HOURS, q);
    }
    
    public static synchronized ProcessamentoFilaModel getInstance(String fila, JdbcWrapper jdbc) throws Exception {
        return instancias.computeIfAbsent(fila, s -> new ProcessamentoFilaModel(fila, jdbc));
    }
    
    public void executar() throws Exception {
        run();
    }
    
    public void run() throws Exception {
        this.jdbcWrapper = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
        ProcessamentoFilaFactory processamentoFilaFactory = new ProcessamentoFilaFactory();
        
        
        PerformanceMonitor.INSTANCE.measureJava("Carrega Fila " + nomeFila, () -> {
            NativeSqlDecorator consultaFila = null;
            try {
                System.out.println("Executando o run ProcessamentoFilaModel" + nomeFila);
                BigDecimal quantidadeExecucaoFila = (BigDecimal) MGECoreParameter.getParameter(nomeParametro);
                if (quantidadeExecucaoFila == null) {
                    quantidadeExecucaoFila = new BigDecimal(10);
                }
                
                consultaFila = new NativeSqlDecorator(this, nomeConsulta, this.jdbcWrapper);
                consultaFila.setParametro("QTDEXECFILA", quantidadeExecucaoFila);
                while (consultaFila.proximo()) {
                    fila.add(new FilaPojo(consultaFila.getValorBigDecimal("NUFILAPROC"), consultaFila.getValorBigDecimal("NUTIPOPROC"), consultaFila.getValorString("CHAVE"), consultaFila.getValorString("NOME")));
                }
            } catch (Exception e) {
                throw new Exception("Erro ao executar consulta busca fila processamento: " + e);
            } finally {
                if (consultaFila != null)
                    consultaFila.close();
            }
        });
        atualizarFilaeAguardarcommit();
        try {
            PerformanceMonitor.INSTANCE.measureJava("adicionar Processamento " + nomeFila, () -> {
                FilaUtils filaUtils = new FilaUtils(jdbcWrapper);
                for (FilaPojo filaCod : fila) {
                    
                    Processar processamento = processamentoFilaFactory.getProcessamento(filaCod.NOME);
                    
                    
                    ProcessamentoFilaParaleloModel processamentoFilaParaleloModel = new ProcessamentoFilaParaleloModel();
                    processamentoFilaParaleloModel.setProcessamento(processamento);
                    processamentoFilaParaleloModel.setNumeroUnicoFilaProcessamento(filaCod.getNUFILAPROC());
                    processamentoFilaParaleloModel.setNomeFila(nomeFila);
                    processamentoFilaParaleloModel.setTipoFila(filaCod.NOME);
                    q.put(processamentoFilaParaleloModel);
                    tp.execute(processamentoFilaParaleloModel);
                    
                }
            });
            if (tp.getQueue().isEmpty()) {
                System.out.println("Finalizado");
            }
        } catch (Exception e) {
            throw new Exception("Erro ao percorrer consulta busca fila processamento: " + e);
        }
    }
    
    private void atualizarFilaeAguardarcommit() throws Exception {
        FinalWrapper<Exception> exc = new FinalWrapper<>();
        Thread t = new Thread(() -> {
            try {
                PerformanceMonitor.INSTANCE.measureJava("adicionar Processamento " + nomeFila, () -> {
                    JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
                    FilaUtils filaUtils = new FilaUtils(jdbc);
                    for (FilaPojo filaCod : fila) {
                        filaUtils.atualizarStatusFila(filaCod.getNUFILAPROC(), "A");
                    }
                });
                if (tp.getQueue().isEmpty()) {
                    System.out.println("Finalizado");
                }
            } catch (Exception e) {
                exc.setWrapperReference(new Exception("Erro ao percorrer consulta busca fila processamento: " + e));
            }
        });
        t.start();
        t.join();
        if (exc.getWrapperReference() != null)
            throw exc.getWrapperReference();
    }
    
    
}

