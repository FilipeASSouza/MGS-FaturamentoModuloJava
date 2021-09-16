package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;

/**
 * Entidade: Financeiro
 * Tabela: TGFFIN
 * Chave: NUFIN
 */
public class MovimentacaoFinanceiraModel {
    private JapeWrapper dao = JapeFactory.dao("Financeiro");
    private DynamicVO vo;
    private JdbcWrapper jdbcWrapper;
    NativeSqlDecorator verificaFatura;
    NativeSqlDecorator verificaPagamento;
    NativeSqlDecorator inserirPagamento;
    NativeSqlDecorator deletarPagamento;
    
    public MovimentacaoFinanceiraModel(JdbcWrapper jdbc) throws Exception {
        this.jdbcWrapper = jdbc;
        inicialzaVariaveis();
    }
    
    public MovimentacaoFinanceiraModel(BigDecimal numeroUnico, JdbcWrapper jdbc) throws Exception {//Chave: NUFIN
        this.vo = dao.findByPK(numeroUnico);
        this.jdbcWrapper = jdbc;
        inicialzaVariaveis();
    }
    
    public MovimentacaoFinanceiraModel(DynamicVO dynamicVO, JdbcWrapper jdbc) throws Exception {
        this.vo = dynamicVO;
        this.jdbcWrapper = jdbc;
        inicialzaVariaveis();
    }
    
    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
        inicialzaVariaveis();
    }
    
    private void inicialzaVariaveis() throws Exception {
        verificaFatura = new NativeSqlDecorator("SELECT COUNT(*) AS NROFATURA FROM MGSTCTFATURA WHERE NUFATURA = :NUFATURA", this.jdbcWrapper);
        verificaPagamento = new NativeSqlDecorator("SELECT NVL( ( SELECT MAX(NUPAGTOFATUR) FROM MGSTCTPAGAMENTO WHERE NUFATURA = :NUFATURA ) , 0 ) AS NROPAG FROM DUAL ", this.jdbcWrapper);
        inserirPagamento = new NativeSqlDecorator(this, "InserirPagamento.sql", this.jdbcWrapper);
        deletarPagamento = new NativeSqlDecorator(this, "DeletarPagamento.sql",this.jdbcWrapper);
    }
    
    private void validaDadosInsert() throws Exception {
    
    }
    
    public void validaDadosUpdate(PersistenceEvent persistenceEvent) throws Exception {
        
        JapeWrapper usuarioDAO = JapeFactory.dao("Usuario");
        vo = (DynamicVO) persistenceEvent.getVo();
        DynamicVO voOld = (DynamicVO) persistenceEvent.getOldVO();
        DynamicVO usuarioVO = usuarioDAO.findByPK(AuthenticationInfo.getCurrent().getUserID());
        
        if (vo.asTimestamp("DHBAIXA") != null
                && vo.asBigDecimal("RECDESP").equals(BigDecimal.ONE)
                && !vo.asTimestamp("DHBAIXA").equals(voOld.asTimestamp("DHBAIXA"))) {
            
            
            if (vo.asBigDecimal("NUMNOTA") != null) {
                
                verificaFatura.cleanParameters();
                verificaFatura.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA"));
                
                if (verificaFatura.proximo()) {
                    
                    if (!verificaFatura.getValorBigDecimal("NROFATURA").equals(BigDecimal.ZERO)) {
                        
                        verificaPagamento.cleanParameters();
                        verificaPagamento.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA"));
                        
                        if (verificaPagamento.proximo()) {
                            
                            if (verificaPagamento.getValorBigDecimal("NROPAG").equals(BigDecimal.ZERO)) {
                                
                                
                                inserirPagamento.cleanParameters();
                                inserirPagamento.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA"));
                                inserirPagamento.setParametro("DTPAGTOFATUR", vo.asTimestamp("DHBAIXA"));
                                inserirPagamento.setParametro("NUPAGTOFATUR", BigDecimal.ONE);
                                inserirPagamento.setParametro("VLRPAGTOFATUR", vo.asBigDecimal("VLRBAIXA"));
                                inserirPagamento.setParametro("ATUALIZADOPOR", usuarioVO.asString("NOMEUSU"));
                                inserirPagamento.setParametro("DTATUALIZACAO", TimeUtils.getNow());
                                inserirPagamento.setParametro("HISTORICO", vo.asString("HISTORICO"));
                                inserirPagamento.setParametro("NUFIN", vo.asBigDecimal("NUFIN"));
                                inserirPagamento.atualizar();
                                
                            } else {
                                
                                inserirPagamento.cleanParameters();
                                inserirPagamento.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA"));
                                inserirPagamento.setParametro("DTPAGTOFATUR", vo.asTimestamp("DHBAIXA"));
                                inserirPagamento.setParametro("NUPAGTOFATUR", verificaPagamento.getValorBigDecimal("NROPAG").add(BigDecimal.ONE));
                                inserirPagamento.setParametro("VLRPAGTOFATUR", vo.asBigDecimal("VLRBAIXA"));
                                inserirPagamento.setParametro("ATUALIZADOPOR", usuarioVO.asString("NOMEUSU"));
                                inserirPagamento.setParametro("DTATUALIZACAO", TimeUtils.getNow());
                                inserirPagamento.setParametro("HISTORICO", vo.asString("HISTORICO"));
                                inserirPagamento.setParametro("NUFIN", vo.asBigDecimal("NUFIN"));
                                inserirPagamento.atualizar();
                                
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void deletarPagamentoFatura(PersistenceEvent persistenceEvent) throws Exception {
        vo = (DynamicVO) persistenceEvent.getVo();
        
        if (vo.asBigDecimal("RECDESP").equals(BigDecimal.ONE)
                && vo.asTimestamp("DHBAIXA") == null) {
            
            
            if (vo.asBigDecimal("NUMNOTA") != null) {
                
                verificaFatura.cleanParameters();
                verificaFatura.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA"));
                
                if (verificaFatura.proximo()) {
                    
                    if (!verificaFatura.getValorBigDecimal("NROFATURA").equals(BigDecimal.ZERO)) {
                        verificaPagamento.cleanParameters();
                        verificaPagamento.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA"));
                        
                        if (verificaPagamento.proximo()) {
                            
                            if (!verificaPagamento.getValorBigDecimal("NROPAG").equals(BigDecimal.ZERO)) {
                                
                                deletarPagamento.cleanParameters();
                                deletarPagamento.setParametro("NUFATURA", vo.asBigDecimal("NUMNOTA"));
                                deletarPagamento.setParametro("NUFIN", vo.asBigDecimal("NUFIN"));
                                deletarPagamento.atualizar();
                            }
                        }
                    }
                }
            }
        }
    }
    
}
