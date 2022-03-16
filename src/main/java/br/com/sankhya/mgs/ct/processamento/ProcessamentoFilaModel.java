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

public class ProcessamentoFilaModel implements Runnable {
    private static ProcessamentoFilaModel instancia;
    private static Thread thread = null;
    private static Thread threadAnterior = null;

    private JapeWrapper filadao = JapeFactory.dao("MGSCT_Fila_Processamento");

    private ProcessamentoFilaModel() {
        executar();
    }

    public static synchronized ProcessamentoFilaModel getInstance() {
        if (instancia == null)
            instancia = new ProcessamentoFilaModel();
        return instancia;
    }

    public void executar() {
        if (thread == null) {
            if (threadAnterior != null) {
                threadAnterior.stop();
                threadAnterior = null;
            }
            thread = new Thread(this);
            thread.setName("ContratoCorporativoFilaProcessamento");
            thread.start();
        }
    }

    public void run() {

        try {
            JapeSession.SessionHandle hnd = null;
            JdbcWrapper jdbc = null;

            BigDecimal quantidadeExecucaoParalela = (BigDecimal) MGECoreParameter.getParameter("MGSQTDEXECPARALE");
            if (quantidadeExecucaoParalela == null) {
                quantidadeExecucaoParalela = new BigDecimal(10);
            }

            System.out.println("Executando o run ProcessamentoFilaModel");

            NativeSqlDecorator consultaFila = null;
            try {
                BigDecimal quantidadeExecucaoFila = (BigDecimal) MGECoreParameter.getParameter("MGSQTDEXECFILA");
                if (quantidadeExecucaoFila == null) {
                    quantidadeExecucaoFila = new BigDecimal(10);
                }

                consultaFila = new NativeSqlDecorator(this, "buscaFilaProcessamento.sql");
                consultaFila.setParametro("QTDEXECFILA", quantidadeExecucaoFila);

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

                    //P = processado, I = incluido, E = Erro, A = andamento

                    NativeSqlDecorator atualizandoProcessamentoSQL = new NativeSqlDecorator("UPDATE MGSTCTFILAPROC SET STATUS = :STATUS WHERE NUFILAPROC = :NUFILAPROC");
                    atualizandoProcessamentoSQL.setParametro("STATUS", String.valueOf("A"));
                    atualizandoProcessamentoSQL.setParametro("NUFILAPROC", numeroUnicoFilaProcessamento );
                    atualizandoProcessamentoSQL.atualizar();

                    ProcessamentoFilaFactory processamentoFilaFactory = new ProcessamentoFilaFactory();
                    Processar processamento = processamentoFilaFactory.getProcessamento(numeroUnicoTipoProcessamento);


                    ProcessamentoFilaParaleloModel processamentoFilaParaleloModel = new ProcessamentoFilaParaleloModel();
                    processamentoFilaParaleloModel.setProcessamento(processamento);
                    processamentoFilaParaleloModel.setNumeroUnicoFilaProcessamento(numeroUnicoFilaProcessamento);
                    Thread threadProcessamento = new Thread(processamentoFilaParaleloModel);
                    threadProcessamento.setName("ContratoCorporativoProcessamento");
                    threadProcessamento.start();

                    while (quantidadeExecucaoParalela.compareTo(new BigDecimal(ProcessamentoFilaParaleloModel.getQuantidadeThreads())) <= 0){
                        System.out.println("Execuções paralelas ProcessamentoFilaModel ln 97");
                        Thread.sleep(10);
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

