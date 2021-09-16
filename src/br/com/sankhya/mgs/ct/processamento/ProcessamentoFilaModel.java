package br.com.sankhya.mgs.ct.processamento;


import br.com.lugh.performance.PerformanceMonitor;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.ProcessarSuper;
import br.com.sankhya.modelcore.util.MGECoreParameter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProcessamentoFilaModel {
    private static ProcessamentoFilaModel instancia;
    LinkedBlockingQueue<Runnable> q = new LinkedBlockingQueue<>(5);
    BlockingThreadPoolExecutor tp = new BlockingThreadPoolExecutor(10, 50, 1, TimeUnit.HOURS, q);
    
    private ProcessamentoFilaModel() {
    
    }
    
    public static synchronized ProcessamentoFilaModel getInstance() {
        if (instancia == null)
            instancia = new ProcessamentoFilaModel();
        return instancia;
    }
    
    public void executar() {
        run();
    }
    
    public void run() {
        ProcessamentoFilaFactory processamentoFilaFactory = new ProcessamentoFilaFactory();
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            
            final List<FilaPojo> fila = new ArrayList<>();
            PerformanceMonitor.INSTANCE.measureJava("Carrega Fila", () -> {
                NativeSqlDecorator consultaFila = null;
                try {
                    BigDecimal quantidadeExecucaoFila = (BigDecimal) MGECoreParameter.getParameter("MGSQTDEXECFILA");
                    if (quantidadeExecucaoFila == null) {
                        quantidadeExecucaoFila = new BigDecimal(10);
                    }
                    
                    consultaFila = new NativeSqlDecorator(this, "buscaFilaProcessamento.sql");
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
                PerformanceMonitor.INSTANCE.measureJava("adicionar Processamento", () -> {
                    for (FilaPojo filaCod : fila) {
                        
                        BigDecimal numeroUnicoFilaProcessamento = null;
                        numeroUnicoFilaProcessamento = filaCod.getNUFILAPROC();
                        
                        ProcessarSuper processamento = processamentoFilaFactory.getProcessamento(filaCod.NOME);
                        processamento.setNumeroUnicoFilaProcessamento(numeroUnicoFilaProcessamento);
                        
                        tp.execute(processamento);
                        
                    }
                });
            } catch (Exception e) {
                throw new Exception("Erro ao percorrer consulta busca fila processamento: " + e);
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (hnd != null)
                JapeSession.close(hnd);
        }
    }
    
    
}

