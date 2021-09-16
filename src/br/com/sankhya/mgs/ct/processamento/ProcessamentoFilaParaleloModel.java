package br.com.sankhya.mgs.ct.processamento;

import br.com.lugh.performance.PerformanceMonitor;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.mgs.ct.dao.FilaDAO;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.Processar;

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
        
        FilaDAO filaDAO = new FilaDAO();
        try {
            hnd = JapeSession.open();
            final JapeSession.SessionHandle hndFinal = hnd;
            PerformanceMonitor.INSTANCE.measureJava(nomeFila + ":" + tipoFila, () -> {
                hndFinal.execWithTX(() -> {
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
                    
                });
            });
            
            
        } catch (Exception e) {
            try {
                hnd.execWithTX(() -> {
                    filaDAO.atualizaFilaErro(
                            numeroUnicoFilaProcessamento,
                            "Erro ao executar processamento: " + e);
                });
            } catch (Exception exception) {
                exception.printStackTrace();
            }
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
