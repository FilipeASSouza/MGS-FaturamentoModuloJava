package br.com.sankhya.mgs.ct.acao.retornofaturamento;

public class DeletaAcaoSuper {
    protected int linhasProcessadas = 0;
    protected int linhasComSucesso = 0;
    protected int linhasSemSucesso = 0;

    protected void init(){
        linhasProcessadas = 0;
        linhasComSucesso = 0;
        linhasSemSucesso = 0;
    }

    protected String getMensagem(){
        return "Registros processados: "+linhasProcessadas+", com sucesso: "+linhasComSucesso+", sem sucesso: "+linhasSemSucesso;
    }
}
