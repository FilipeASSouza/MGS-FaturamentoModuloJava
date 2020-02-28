package br.com.sankhya.mgs.ct.processamento.processamentomodel;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.dao.FilaDAO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProcessarSuper implements Processar {
    protected FilaDAO.RegistroFila registroFila;
    protected BigDecimal numeroUnicoFilaProcessamento;
    protected BigDecimal numeroUnicoIntegracao;

    protected ProcessarSuper() {

    }

    @Override
    public boolean executar() throws Exception {
        try {
            this.registroFila = new FilaDAO().getRegistroFila(numeroUnicoFilaProcessamento);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getMensagem() {
        return null;
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
