package br.com.sankhya.mgs.ct.model.edicaodetalhamento;

import br.com.sankhya.jape.dao.JdbcWrapper;

public class EdicaoCompetenciaModel extends EdicaoDetalhamentoModelSuper{
    public EdicaoCompetenciaModel(JdbcWrapper jdbcWrapper) throws Exception {
        super("updateCompetencia.sql",jdbcWrapper);
    }
    
    public void executar() throws Exception {
        super.inicializarExecutar();
        nativeSqlDecorator.setParametro("V_FATU",parametros.get("COMPFATU"));//yyyymm
        nativeSqlDecorator.setParametro("V_DTLCCUSTO",parametros.get("DTLCCUSTO"));//dd/mm/yyyy
        super.finalizaExecutar();
    }
}
