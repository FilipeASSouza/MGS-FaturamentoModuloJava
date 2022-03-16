package br.com.sankhya.mgs.ct.model.edicaodetalhamento;

public class EdicaoCompetenciaModel extends EdicaoDetalhamentoModelSuper{
    public void executar() throws Exception {
        scriptsql = "updateCompetencia.sql";
        super.inicializarExecutar();
        nativeSqlDecorator.setParametro("V_FATU",parametros.get("COMPFATU"));//yyyymm
        nativeSqlDecorator.setParametro("V_DTLCCUSTO",parametros.get("DTLCCUSTO"));//dd/mm/yyyy
        super.finalizaExecutar();
    }
}
