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
import org.apache.poi.ss.formula.functions.Na;

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

        JapeWrapper usuarioDAO = JapeFactory.dao("Usuario");
        vo = (DynamicVO) persistenceEvent.getVo();
        DynamicVO voOld = (DynamicVO) persistenceEvent.getOldVO();
        DynamicVO usuarioVO = usuarioDAO.findByPK(AuthenticationInfo.getCurrent().getUserID());

        if( vo.asTimestamp("DHBAIXA") != null
                && vo.asBigDecimal("RECDESP").equals(BigDecimal.ONE)
                && !vo.asTimestamp("DHBAIXA").equals(voOld.asTimestamp("DHBAIXA")) ){


            if( vo.asBigDecimal("NUMNOTA") != null ){

                NativeSqlDecorator verificaFatura = new NativeSqlDecorator("SELECT COUNT(*) AS NROFATURA FROM MGSTCTFATURA WHERE NUFATURA = :NUFATURA");
                verificaFatura.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA"));

                if( verificaFatura.proximo() ){

                    if( !verificaFatura.getValorBigDecimal("NROFATURA").equals(BigDecimal.ZERO) ){

                        NativeSqlDecorator verificaPagamento = new NativeSqlDecorator("SELECT NVL( ( SELECT MAX(NUPAGTOFATUR) FROM MGSTCTPAGAMENTO WHERE NUFATURA = :NUFATURA ) , 0 ) AS NROPAG FROM DUAL ");
                        verificaPagamento.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA"));

                        if( verificaPagamento.proximo() ){

                            if( verificaPagamento.getValorBigDecimal("NROPAG").equals(BigDecimal.ZERO) ){

                                NativeSqlDecorator inserirPagamento = new NativeSqlDecorator(this,"InserirPagamento.sql");

                                inserirPagamento.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA") );
                                inserirPagamento.setParametro("DTPAGTOFATUR", vo.asTimestamp("DHBAIXA") );
                                inserirPagamento.setParametro("NUPAGTOFATUR", BigDecimal.ONE );
                                inserirPagamento.setParametro("VLRPAGTOFATUR", vo.asBigDecimal("VLRBAIXA") );
                                inserirPagamento.setParametro("ATUALIZADOPOR", usuarioVO.asString("NOMEUSU") );
                                inserirPagamento.setParametro("DTATUALIZACAO", TimeUtils.getNow() );
                                inserirPagamento.setParametro("HISTORICO", vo.asString("HISTORICO") );
                                inserirPagamento.setParametro("NUFIN", vo.asBigDecimal("NUFIN"));
                                inserirPagamento.atualizar();

                            }else{

                                NativeSqlDecorator inserirPagamento = new NativeSqlDecorator(this,"InserirPagamento.sql");

                                inserirPagamento.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA") );
                                inserirPagamento.setParametro("DTPAGTOFATUR", vo.asTimestamp("DHBAIXA") );
                                inserirPagamento.setParametro("NUPAGTOFATUR", verificaPagamento.getValorBigDecimal("NROPAG").add(BigDecimal.ONE) );
                                inserirPagamento.setParametro("VLRPAGTOFATUR", vo.asBigDecimal("VLRBAIXA") );
                                inserirPagamento.setParametro("ATUALIZADOPOR",usuarioVO.asString("NOMEUSU") );
                                inserirPagamento.setParametro("DTATUALIZACAO", TimeUtils.getNow() );
                                inserirPagamento.setParametro("HISTORICO", vo.asString("HISTORICO") );
                                inserirPagamento.setParametro("NUFIN", vo.asBigDecimal("NUFIN"));
                                inserirPagamento.atualizar();

                            }
                        }
                    }
                }
            }
        }
    }

    public void deletarPagamentoFatura(PersistenceEvent persistenceEvent) throws Exception{
        JapeWrapper usuarioDAO = JapeFactory.dao("Usuario");
        vo = (DynamicVO) persistenceEvent.getVo();
        DynamicVO usuarioVO = usuarioDAO.findByPK(AuthenticationInfo.getCurrent().getUserID());

        if( vo.asBigDecimal("RECDESP").equals(BigDecimal.ONE)
                && vo.asTimestamp("DHBAIXA") == null ){


            if( vo.asBigDecimal("NUMNOTA") != null ){

                NativeSqlDecorator verificaFatura = new NativeSqlDecorator("SELECT COUNT(*) AS NROFATURA FROM MGSTCTFATURA WHERE NUFATURA = :NUFATURA");
                verificaFatura.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA"));

                if( verificaFatura.proximo() ){

                    if( !verificaFatura.getValorBigDecimal("NROFATURA").equals(BigDecimal.ZERO) ){

                        NativeSqlDecorator verificaPagamento = new NativeSqlDecorator("SELECT NVL( ( SELECT MAX(NUPAGTOFATUR) FROM MGSTCTPAGAMENTO WHERE NUFATURA = :NUFATURA ) , 0 ) AS NROPAG FROM DUAL ");
                        verificaPagamento.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA"));

                        if( verificaPagamento.proximo() ){

                            if( !verificaPagamento.getValorBigDecimal("NROPAG").equals(BigDecimal.ZERO) ){
                                NativeSqlDecorator deletarPagamento = new NativeSqlDecorator(this,"DeletarPagamento.sql");

                                deletarPagamento.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA") );
                                deletarPagamento.setParametro("NUFIN", vo.asBigDecimal("NUFIN"));
                                deletarPagamento.atualizar();
                            }
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
