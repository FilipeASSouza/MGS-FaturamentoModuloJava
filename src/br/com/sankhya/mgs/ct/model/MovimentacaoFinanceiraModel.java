package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Entidade: Financeiro
 * Tabela: TGFFIN
 * Chave: NUFIN
 */
public class MovimentacaoFinanceiraModel {
    private JapeWrapper dao = JapeFactory.dao("Financeiro");
    private DynamicVO vo;
    public MovimentacaoFinanceiraModel()  {
    }

    public MovimentacaoFinanceiraModel(BigDecimal numeroUnico) throws Exception {//Chave: NUFIN
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public MovimentacaoFinanceiraModel(DynamicVO dynamicVO) throws Exception {
        this.vo = dynamicVO;
        inicialzaVariaveis();
    }

    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
        inicialzaVariaveis();
    }

    private void inicialzaVariaveis()throws Exception {

    }

    private void validaDadosInsert() throws Exception {

    }

    public void validaDadosUpdate(PersistenceEvent persistenceEvent) throws Exception {

        JapeWrapper cabecalhoNotaDAO = JapeFactory.dao("CabecalhoNota");
        JapeWrapper usuarioDAO = JapeFactory.dao("Usuario");
        vo = (DynamicVO) persistenceEvent.getVo();
        DynamicVO voOld = (DynamicVO) persistenceEvent.getOldVO();
        DynamicVO usuarioVO = usuarioDAO.findByPK(AuthenticationInfo.getCurrent().getUserID());

        if( vo.asTimestamp("DHTIPOPERBAIXA") != null
                && !vo.asTimestamp("DHTIPOPERBAIXA").equals(voOld.asTimestamp("DHTIPOPERBAIXA")) ){

            if( vo.asBigDecimal("NUNOTA") != null ){

                DynamicVO cabecalhoNotaVo = cabecalhoNotaDAO.findByPK(vo.asBigDecimal("NUNOTA"));

                if( cabecalhoNotaVo.asBigDecimal("AD_FATURA") != null ){

                    NativeSqlDecorator verificaFatura = new NativeSqlDecorator("SELECT 1 FROM MGSTCTFATURA WHERE NUFATURA = :NUFATURA");
                    verificaFatura.setParametro("NUFATURA", cabecalhoNotaVo.asBigDecimal("AD_FATURA"));
                    verificaFatura.executar();

                    JapeWrapper pagamentoFaturaDAO = JapeFactory.dao("MGCCT_Pagamento_Faturas");

                    while( verificaFatura.proximo() ){

                        FluidCreateVO pagamentoFaturaFCVO = pagamentoFaturaDAO.create();

                        NativeSqlDecorator verificaPagamento = new NativeSqlDecorator("SELECT MAX( NUPAGTOFATUR ) AS NROPAG FROM MGSTCTPAGAMENTO WHERE NUFATURA = :NUFATURA ");
                        verificaPagamento.setParametro("NUFATURA", cabecalhoNotaVo.asBigDecimal("AD_FATURA"));
                        verificaPagamento.executar();

                        if( verificaPagamento.proximo() ){
                            while( verificaPagamento.proximo() ){

                                pagamentoFaturaFCVO.set("NUFATURA", cabecalhoNotaVo.asBigDecimal("AD_FATURA"));
                                pagamentoFaturaFCVO.set("DTPAGTOFATUR", vo.asTimestamp("DHBAIXA") );
                                pagamentoFaturaFCVO.set("NUPAGTOFATUR", verificaPagamento.getValorBigDecimal("NROPAG").add(BigDecimal.ONE) );
                                pagamentoFaturaFCVO.set("VLRPAGTOFATUR", vo.asBigDecimal("VLRBAIXA") );
                                pagamentoFaturaFCVO.set("ATUALIZADOPOR", usuarioVO.asString("NOMEUSUCPLT") );
                                pagamentoFaturaFCVO.set("DTATUALIZACAO", TimeUtils.getNow() );
                                pagamentoFaturaFCVO.set("HISTORICO", vo.asString("HISTORICO") );
                                pagamentoFaturaFCVO.set("NUBOLETOFATUR", vo.asString("NOSSONUM") );
                                pagamentoFaturaFCVO.save();

                            }
                        }else{

                            pagamentoFaturaFCVO.set("NUFATURA", cabecalhoNotaVo.asBigDecimal("AD_FATURA"));
                            pagamentoFaturaFCVO.set("DTPAGTOFATUR", vo.asTimestamp("DHBAIXA") );
                            pagamentoFaturaFCVO.set("NUPAGTOFATUR", BigDecimal.ONE );
                            pagamentoFaturaFCVO.set("VLRPAGTOFATUR", vo.asBigDecimal("VLRBAIXA") );
                            pagamentoFaturaFCVO.set("ATUALIZADOPOR",usuarioVO.asString("NOMEUSUCPLT") );
                            pagamentoFaturaFCVO.set("DTATUALIZACAO", TimeUtils.getNow() );
                            pagamentoFaturaFCVO.set("HISTORICO", vo.asString("HISTORICO") );
                            pagamentoFaturaFCVO.set("NUBOLETOFATUR", vo.asString("NOSSONUM") );
                            pagamentoFaturaFCVO.save();

                        }
                    }
                }
            }
        }
    }

    private void validaDadosUpdate(DynamicVO oldvo) throws Exception {

    }

    private void preecheCamposCalculados() throws Exception {

    }

    private void criaRegistrosDerivados() throws Exception {

    }

    private void validaDelete() throws Exception {
        
    }

    /*  consulta para pegar campo que nao pode ser alterado com uma descricao correta
select 'if (campos.containsKey("'||NOMECAMPO||'")) {mensagemErro += "Campo '||DESCRCAMPO||' não pode ser modificado. ";}' from tddcam where nometab = 'TABELA'  and nomecampo NOT IN ('CAMPO1','CAMPO2') order by ordem 
    */

    private void validaCamposUpdate(HashMap<String, Object[]> campos) throws Exception {
        String mensagemErro = "";


        if (campos.containsKey("#CAMPO#")) {
            mensagemErro += "Campo Evento não pode ser modificado. ";
        }

        if (mensagemErro != "") {
            ErroUtils.disparaErro(mensagemErro);
        }
    }
}
