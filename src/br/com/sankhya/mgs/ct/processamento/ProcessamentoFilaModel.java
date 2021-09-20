package br.com.sankhya.mgs.ct.processamento;


import br.com.lugh.performance.PerformanceMonitor;
import br.com.sankhya.bh.utils.FilaUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.Processar;
import br.com.sankhya.modelcore.util.MGECoreParameter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProcessamentoFilaModel {
    private static Map<String, ProcessamentoFilaModel> instancias = new HashMap<>();
    LinkedBlockingQueue<Runnable> q = new LinkedBlockingQueue<>(1000);
    BlockingThreadPoolExecutor tp;
    private String nomeFila;
    private String nomeConsulta;
    private JdbcWrapper jdbcWrapper;
    public void stop(){
        tp.shutdownNow();
    }
    public static void stopAll(){
        for (Map.Entry<String, ProcessamentoFilaModel> model : instancias.entrySet()) {
            model.getValue().stop();
        }
    }
    
    private ProcessamentoFilaModel(String fila,JdbcWrapper jdbc) {
        this.jdbcWrapper = jdbc;
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
        return instancias.computeIfAbsent(fila, s -> new ProcessamentoFilaModel(fila,jdbc));
    }
    
    public void executar() throws Exception {
        run();
    }
    
    public void run() throws Exception {
        ProcessamentoFilaFactory processamentoFilaFactory = new ProcessamentoFilaFactory();
        
        final List<FilaPojo> fila = new ArrayList<>();
        PerformanceMonitor.INSTANCE.measureJava("Carrega Fila "+nomeFila, () -> {
            NativeSqlDecorator consultaFila = null;
            try {
                System.out.println("Executando o run ProcessamentoFilaModel" + nomeFila);
                BigDecimal quantidadeExecucaoFila = (BigDecimal) MGECoreParameter.getParameter("MGSQTDEXECFILA");
                if (quantidadeExecucaoFila == null) {
                    quantidadeExecucaoFila = new BigDecimal(10);
                }
    
                consultaFila = new NativeSqlDecorator(this, nomeConsulta,this.jdbcWrapper);
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
        
        try {
            PerformanceMonitor.INSTANCE.measureJava("adicionar Processamento "+nomeFila, () -> {
                FilaUtils filaUtils = new FilaUtils(jdbcWrapper);
                for (FilaPojo filaCod : fila) {
    
                    filaUtils.atualizarStatusFila(filaCod.getNUFILAPROC(), "A");
                    
                    
                    Processar processamento = processamentoFilaFactory.getProcessamento(filaCod.NOME);
                    
                    
                    ProcessamentoFilaParaleloModel processamentoFilaParaleloModel = new ProcessamentoFilaParaleloModel();
                    processamentoFilaParaleloModel.setProcessamento(processamento);
                    processamentoFilaParaleloModel.setNumeroUnicoFilaProcessamento(filaCod.getNUFILAPROC());
                    processamentoFilaParaleloModel.setNomeFila(nomeFila);
                    processamentoFilaParaleloModel.setTipoFila(filaCod.NOME);
                    tp.execute(processamentoFilaParaleloModel);
                    
                }
            });
        } catch (Exception e) {
            throw new Exception("Erro ao percorrer consulta busca fila processamento: " + e);
        }
    }
    
    
}

