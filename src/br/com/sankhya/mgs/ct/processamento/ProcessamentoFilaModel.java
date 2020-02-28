package br.com.sankhya.mgs.ct.processamento;


import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.mgs.ct.dao.FilaDAO;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.Processar;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;

public class ProcessamentoFilaModel implements Runnable {
    private static ProcessamentoFilaModel instancia;
    private static Thread thread = null;

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
            thread = new Thread(this);
            thread.setName("ContratoCorporativoFilaProcessamento");
            thread.start();
        }
    }

    public void run() {
        ProcessamentoFilaFactory processamentoFilaFactory = new ProcessamentoFilaFactory();
        try {
            JapeSession.SessionHandle hnd = null;
            JdbcWrapper jdbc = null;
            boolean possuiRegistros = true;
            while (possuiRegistros) {
                NativeSqlDecorator consultaFila = null;
                try {
                    consultaFila = new NativeSqlDecorator(this, "buscaFilaProcessamento.sql");
                } catch (Exception e) {
                    throw new Exception("Erro ao executar consulta busca fila processamento: " + e);
                }

                possuiRegistros = false;
                BigDecimal numeroUnicoTipoProcessamento = null;
                BigDecimal numeroUnicoFilaProcessamento = null;

                hnd = JapeSession.open();
                final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
                jdbc = dwfFacade.getJdbcWrapper();
                jdbc.openSession();

                FilaDAO filaDAO = new FilaDAO();
                try {
                    while (consultaFila.proximo()) {
                        possuiRegistros = true;
                        try {
                            numeroUnicoTipoProcessamento = consultaFila.getValorBigDecimal("NUTIPOPROC");
                            numeroUnicoFilaProcessamento = consultaFila.getValorBigDecimal("NUFILAPROC");

                            Processar processamento = processamentoFilaFactory.getProcessamento(numeroUnicoTipoProcessamento);
                            processamento.setNumeroUnicoFilaProcessamento(numeroUnicoFilaProcessamento);


                            boolean executado = processamento.executar();
                            if (executado){
                                filaDAO.atualizaFilaProcessado(numeroUnicoFilaProcessamento);
                            } else {
                                filaDAO.atualizaFilaErro(
                                        numeroUnicoFilaProcessamento,
                                        "Erro ao executar processamento: " + processamento.getMensagem());
                            }


                        } catch (Exception e) {
                            filaDAO.atualizaFilaErro(
                                    numeroUnicoFilaProcessamento,
                                    "Erro ao executar processamento: " + e);
                        }

                    }
                } catch (Exception e) {
                    throw new Exception("Erro ao percorrer consulta busca fila processamento: " + e);
                } finally {

                }

                if (!possuiRegistros) {
                    thread = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}

