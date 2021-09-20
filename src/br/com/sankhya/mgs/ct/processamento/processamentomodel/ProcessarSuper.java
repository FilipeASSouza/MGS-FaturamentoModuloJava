package br.com.sankhya.mgs.ct.processamento.processamentomodel;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.dao.FilaDAO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProcessarSuper implements Processar {
    protected FilaDAO.RegistroFila registroFila;
    protected BigDecimal numeroUnicoFilaProcessamento;
    protected BigDecimal numeroUnicoIntegracao;
    protected JapeSession.SessionHandle hnd = null;
    protected JdbcWrapper jdbc = null;
    protected String mensagem;
    final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
    
    protected ProcessarSuper() {
    
    }
    
    @Override
    public boolean executar() throws Exception {
        hnd = JapeSession.open();
        jdbc = dwfFacade.getJdbcWrapper();
        jdbc.openSession();
        this.registroFila = new FilaDAO(jdbc).getRegistroFila(numeroUnicoFilaProcessamento);
        return false;
    }
    
    public void finalizar() {
        JapeSession.close(hnd);
        JdbcWrapper.closeSession(jdbc);
    }
    
    @Override
    public String getMensagem() {
        return mensagem;
    }
    
    @Override
    public void setNumeroUnicoFilaProcessamento(BigDecimal numeroUnicoFilaProcessamento) {
        this.numeroUnicoFilaProcessamento = numeroUnicoFilaProcessamento;
    }
    
    protected Map<String, String> getParametrosExecutacao() {
        Map<String, String> mapParametros = new HashMap<String, String>();
        String[] parametrosLista = registroFila.getCHAVE().split(";");
        
        for (String parametro : parametrosLista) {
            String[] chaveValor = parametro.split("=");
            mapParametros.put(chaveValor[0], chaveValor[1]);
        }
        return mapParametros;
        
    }
    
    protected String getLogin() throws Exception {
        DynamicVO vo = JapeFactory.dao("Usuario").findByPK(registroFila.getCODUSU());
        return vo.asString("NOMEUSU");
    }
    
    protected void geraIntegrcaoDetalheCusto() {
    
    }
    
}
