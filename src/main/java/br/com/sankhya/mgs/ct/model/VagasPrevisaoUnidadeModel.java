package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

/**
 * Entidade: MGSCT_Vagas_Previsao_Unidade
 * Tabela: MGSTCTUNIDPREVVAGA
 * Chave: NUUNIDPREVVAGA
 */
public class VagasPrevisaoUnidadeModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Vagas_Previsao_Unidade");
    private DynamicVO vo;


    /**
     * Entidade: MGSCT_Previsoes_Unidade
     * Tabela: MGSTCTUNIDADEPREV
     * Chave: NUUNIDPREV
     */
    private DynamicVO mestrevo;

    private Boolean subtrairVagaPrevisaoUnidade = false;
    public VagasPrevisaoUnidadeModel()  {
    }

    public VagasPrevisaoUnidadeModel(BigDecimal numeroUnico) throws Exception {//Chave: NUUNIDPREVVAGA
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public VagasPrevisaoUnidadeModel(DynamicVO dynamicVO) throws Exception {
        this.vo = dynamicVO;
        inicialzaVariaveis();
    }

    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
        inicialzaVariaveis();
    }

    private void inicialzaVariaveis()throws Exception {
        mestrevo = JapeFactory.dao("MGSCT_Previsoes_Unidade").findByPK(vo.asBigDecimal("NUUNIDPREV"));
    }

    private void validaDadosInsert() throws Exception {

    }

    public void validaDadosUpdate(DynamicVO oldvo) throws Exception {
        Boolean dataFimNovoPreenchido = vo.asTimestamp("DTFIM") != null;
        Boolean dataFimAntigoPreenchido = oldvo.asTimestamp("DTFIM") != null;
        String vagaAlocada = null;

        if (dataFimNovoPreenchido){
            if (vo.asTimestamp("DTFIM").compareTo(vo.asTimestamp("DTINICIO")) < 0){
                ErroUtils.disparaErro("Data final n�o pode ser menor que a data incial!");
            }
        }

        NativeSqlDecorator verificarAlocacaoSQL = new NativeSqlDecorator("select codvaga from mgstctalocacaops where codvaga = :codvaga and (TRUNC(DTFIM) >= TRUNC(SYSDATE)  OR DTFIM  IS NULL)");
        verificarAlocacaoSQL.setParametro("codvaga", vo.asString("CODVAGA"));

        if(verificarAlocacaoSQL.proximo()){
            vagaAlocada = verificarAlocacaoSQL.getValorString("codvaga");
        }

        if( vagaAlocada != null ){
            ErroUtils.disparaErro("Vaga com aloca��o, fineza verificar!");
        }

        if (dataFimAntigoPreenchido && !dataFimNovoPreenchido){
            ErroUtils.disparaErro("Vaga n�o pode ser reativada!");
        }

        if (!dataFimAntigoPreenchido && dataFimNovoPreenchido) {
            Timestamp dtfim = vo.asTimestamp("DTFIM");
            if (dtfim.compareTo(TimeUtils.getNow())>0){
                ErroUtils.disparaErro("Data de finaliza��o da vaga n�o pode ser maior que hoje!");
            }

            //todo implementar
            /*if ( [metodo que retorna se a vaga esta com funcionario alocado] ){
                ErroUtils.disparaErro("Vaga ainda se encontra ativa na previsao da undiade, deve ser desabilitado primeiro!");
            }*/

            subtrairVagaPrevisaoUnidade = true;
        }
    }

    public void preencheCamposCalculados() throws Exception {
        vo.setProperty("NUCONTRCENT", mestrevo.asBigDecimalOrZero("NUCONTRCENT"));
    }

    private void criaRegistrosDerivados() throws Exception {

    }

    public void validaDelete() throws Exception {
        ErroUtils.disparaErro("Vaga n�o pode ser deletada!");
    }

    public DynamicVO criar(BigDecimal numeroUnicoPrevisaoUnidade, String codigoVaga, Timestamp dataInicio) throws Exception {
        FluidCreateVO fluidCreateVO = dao.create();
        fluidCreateVO.set("NUUNIDPREV", numeroUnicoPrevisaoUnidade);
        fluidCreateVO.set("CODVAGA",codigoVaga);
        fluidCreateVO.set("DTINICIO", dataInicio);
        DynamicVO save = fluidCreateVO.save();
        return save;
    }

    public void alteraDadosDerivados() throws Exception {
        if(subtrairVagaPrevisaoUnidade){
            PrevisoesUnidadeModel previsoesUnidadeModel = new PrevisoesUnidadeModel(vo.asBigDecimal("NUUNIDPREV"));
            previsoesUnidadeModel.diminuirUmQuantidadeContrata();
            subtrairVagaPrevisaoUnidade = false;
        }
    }

    public BigDecimal quantidadeVagasAtivas(BigDecimal numeroUnicoPrevisaoContrato) throws Exception {
        JapeSession.getCurrentSession().getTopMostHandle().setFindersMaxRows(1000);
        Collection<DynamicVO> dynamicVOS = dao.find("NUUNIDPREV = ? AND DTFIM IS NULL", numeroUnicoPrevisaoContrato);
        int size = dynamicVOS.size();
        return new BigDecimal(size);
    }
}
