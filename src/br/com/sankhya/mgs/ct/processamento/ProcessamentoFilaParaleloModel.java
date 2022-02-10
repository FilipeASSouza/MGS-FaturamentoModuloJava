package br.com.sankhya.mgs.ct.processamento;

import br.com.lugh.performance.ExtensaoLogger;
import br.com.lugh.performance.PerformanceMonitor;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.dao.FilaDAO;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.Processar;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;

public class ProcessamentoFilaParaleloModel implements Runnable {
    private Processar processamento = null;
    private BigDecimal numeroUnicoFilaProcessamento = null;
    private String nomeFila;
    private String tipoFila;
    
    public void setProcessamento(Processar processamento) {
        this.processamento = processamento;
    }
    
    public void setNumeroUnicoFilaProcessamento(BigDecimal numeroUnicoFilaProcessamento) {
        this.numeroUnicoFilaProcessamento = numeroUnicoFilaProcessamento;
    }
    
    
    @Override
    public void run() {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            final JapeSession.SessionHandle hndFinal = hnd;
            JdbcWrapper jdbcWrapper = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            FilaDAO filaDAO = new FilaDAO(jdbcWrapper);
            PerformanceMonitor.INSTANCE.measureJava(nomeFila + ":" + tipoFila, () -> {
                ExtensaoLogger logger = ExtensaoLogger.getLogger();
                try {
                    
                    
                    hndFinal.execWithTX(() -> {
                        processamento.setNumeroUnicoFilaProcessamento(numeroUnicoFilaProcessamento);
                        
                        boolean executado = processamento.execute();
                        
                        if (executado) {
                            filaDAO.atualizaFilaProcessado(numeroUnicoFilaProcessamento,
                                    "OK. " + processamento.getMensagem());
                        } else {
                            filaDAO.atualizaFilaErro(
                                    numeroUnicoFilaProcessamento,
                                    "Erro ao executar processamento: " + processamento.getMensagem());
                        }
                        
                    });
                    
                    
                } catch (Exception e) {
                    logger.severe("Erro ao executar planilha"+e.getMessage(),e);
                    try {
                        hndFinal.execWithTX(() -> {
                            filaDAO.atualizaFilaErro(
                                    numeroUnicoFilaProcessamento,
                                    "Erro ao executar processamento: " + e);
                        });
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JapeSession.close(hnd);
        }
    }
    
    public BigDecimal getNumeroUnicoFilaProcessamento() {
        return numeroUnicoFilaProcessamento;
    }
    
    public String getNomeFila() {
        return nomeFila;
    }
    
    public void setNomeFila(String nomeFila) {
        this.nomeFila = nomeFila;
    }
    
    public String getTipoFila() {
        return tipoFila;
    }
    
    public void setTipoFila(String tipoFila) {
        this.tipoFila = tipoFila;
    }
}
