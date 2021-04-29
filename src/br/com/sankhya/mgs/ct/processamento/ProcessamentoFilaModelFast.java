package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.Processar;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ProcessamentoFilaModelFast implements Runnable{
    private static ProcessamentoFilaModelFast instancia;
    private static Thread thread = null;
    private static Thread threadAnterior = null;

    private JapeWrapper filadao = JapeFactory.dao("MGSCT_Fila_Processamento");

    private ProcessamentoFilaModelFast() {
        executar();
    }

    public static synchronized ProcessamentoFilaModelFast getInstance() {
        if (instancia == null)
            instancia = new ProcessamentoFilaModelFast();
        return instancia;
    }

    public void executar() {
        if (thread == null) {
            if (threadAnterior != null) {
                threadAnterior.stop();
                threadAnterior = null;
            }
            thread = new Thread(this);
            thread.setName("ContratoCorporativoFilaProcessamentoFast");
            thread.start();
        }
    }

    public void run() {

        try {
            JapeSession.SessionHandle hnd = null;
            JdbcWrapper jdbc = null;

            BigDecimal quantidadeExecucaoParalela = (BigDecimal) MGECoreParameter.getParameter("MGSQTDEXECPARALE");
            if (quantidadeExecucaoParalela == null) {
                quantidadeExecucaoParalela = new BigDecimal(1);
            }

            NativeSqlDecorator consultaFila = null;
            try {

                consultaFila = new NativeSqlDecorator(this, "buscaFilaProcessamentoFast.sql");
                consultaFila.setParametro("QTDEXECFILA", BigDecimal.ONE);

            } catch (Exception e) {
                throw new Exception("Erro ao executar consulta busca fila processamento: " + e);
            }


            BigDecimal numeroUnicoTipoProcessamento = null;
            BigDecimal numeroUnicoFilaProcessamento = null;

            hnd = JapeSession.open();
            final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            jdbc = dwfFacade.getJdbcWrapper();
            jdbc.openSession();


            try {
                while (consultaFila.proximo()) {
                    numeroUnicoTipoProcessamento = consultaFila.getValorBigDecimal("NUTIPOPROC");
                    numeroUnicoFilaProcessamento = consultaFila.getValorBigDecimal("NUFILAPROC");

                    ProcessamentoFilaFactory processamentoFilaFactory = new ProcessamentoFilaFactory();
                    Processar processamento = processamentoFilaFactory.getProcessamento(numeroUnicoTipoProcessamento);


                    ProcessamentoFilaParaleloModel processamentoFilaParaleloModel = new ProcessamentoFilaParaleloModel();
                    processamentoFilaParaleloModel.setProcessamento(processamento);
                    processamentoFilaParaleloModel.setNumeroUnicoFilaProcessamento(numeroUnicoFilaProcessamento);
                    Thread threadProcessamento = new Thread(processamentoFilaParaleloModel);
                    threadProcessamento.setName("ContratoCorporativoProcessamentoFast");
                    threadProcessamento.start();

                    while (quantidadeExecucaoParalela.compareTo(new BigDecimal(ProcessamentoFilaParaleloModel.getQuantidadeThreads())) <= 0){
                        Thread.sleep(5000);
                    }
                }

            } catch (Exception e) {
                throw new Exception("Erro ao percorrer consulta busca fila processamento: " + e);
            }
            JapeSession.close(hnd);
            JdbcWrapper.closeSession(jdbc);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            threadAnterior = thread;
            thread = null;
        }
    }
}
