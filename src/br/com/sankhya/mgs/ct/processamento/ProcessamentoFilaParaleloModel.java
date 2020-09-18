package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.dao.FilaDAO;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.Processar;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;

public class ProcessamentoFilaParaleloModel implements Runnable {
    private Processar processamento = null;
    private BigDecimal numeroUnicoFilaProcessamento = null;
    private static int quantidadeThreads = 0;


    public void setProcessamento(Processar processamento) {
        this.processamento = processamento;
    }

    public void setNumeroUnicoFilaProcessamento(BigDecimal numeroUnicoFilaProcessamento) {
        this.numeroUnicoFilaProcessamento = numeroUnicoFilaProcessamento;
    }

    public static void setQuantidadeThreads(int quantidadeThreads) {
        ProcessamentoFilaParaleloModel.quantidadeThreads = quantidadeThreads;
    }

    public static int getQuantidadeThreads() {
        return quantidadeThreads;
    }

    @Override
    public void run() {
        JapeSession.SessionHandle hnd = null;
        JdbcWrapper jdbc = null;

        quantidadeThreads++;
        FilaDAO filaDAO = new FilaDAO();
        try {
            hnd = JapeSession.open();
            final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            jdbc = dwfFacade.getJdbcWrapper();
            jdbc.openSession();


            processamento.setNumeroUnicoFilaProcessamento(numeroUnicoFilaProcessamento);

            boolean executado = processamento.executar();

            if (executado) {
                filaDAO.atualizaFilaProcessado(numeroUnicoFilaProcessamento,
                        "OK. " + processamento.getMensagem());
            } else {
                filaDAO.atualizaFilaErro(
                        numeroUnicoFilaProcessamento,
                        "Erro ao executar processamento: " + processamento.getMensagem());
            }

        } catch (Exception e) {
            try {
                filaDAO.atualizaFilaErro(
                        numeroUnicoFilaProcessamento,
                        "Erro ao executar processamento: " + e);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } finally {
            quantidadeThreads--;
            try {
                JapeSession.close(hnd);
                JdbcWrapper.closeSession(jdbc);
            }catch (Exception e){
            }
        }
    }
}
