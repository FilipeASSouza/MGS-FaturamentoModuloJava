package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Entidade: MGSCT_Vagas_Previsao_Contrato
 * Tabela: MGSTCTCONTRATOVAGA
 * Chave: NUCONTRVAGA
 */

public class VagasPrevisaoContratoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Vagas_Previsao_Contrato");
    private DynamicVO vo;
    private BigDecimal quantidadeVagasDisponiveis;
    private Boolean subtrairVagaPrevisaoContrato = false;

    public VagasPrevisaoContratoModel() {
    }

    public void setVo(DynamicVO vo) {
        this.vo = vo;
    }

    public VagasPrevisaoContratoModel(DynamicVO dynamicVO) {
        this.vo = dynamicVO;
        inicialzaVariaveis();
    }

    private void inicialzaVariaveis() {

    }

    public DynamicVO criar(BigDecimal numeroUnicoPrevisaoContrato, String codigoVaga, Timestamp dataInicio) throws Exception {
        FluidCreateVO fluidCreateVO = dao.create();
        fluidCreateVO.set("NUCONTRPREV",numeroUnicoPrevisaoContrato);
        fluidCreateVO.set("CODVAGA",codigoVaga);
        fluidCreateVO.set("DTINICIO", dataInicio);
        DynamicVO save = fluidCreateVO.save();
        return save;
    }

    public void validaDelete() throws Exception {
        ErroUtils.disparaErro("Vagas não podem ser excluidas!");
    }

    public BigDecimal quantidadeVagasAtivas(BigDecimal numeroUnicoPrevisaoContrato, String codigoVaga) throws Exception {
        //Collection<DynamicVO> dynamicVOS = dao.find("NUCONTRPREV = ? AND SUBSTR(CODVAGA,1,3) = ? AND DTFIM IS NULL", numeroUnicoPrevisaoContrato, codigoVaga);

        int size  = 0;
        NativeSqlDecorator verificandoVagasLivresPrevisaoSQL = new NativeSqlDecorator("SELECT COUNT( NUCONTRVAGA ) QTD FROM MGSTCTCONTRATOVAGA WHERE NUCONTRPREV = :NUCONTRPREV AND DTFIM IS NULL AND SUBSTR( CODVAGA, 1, 3 ) = :CODVAGA ");
        verificandoVagasLivresPrevisaoSQL.setParametro("NUCONTRPREV", numeroUnicoPrevisaoContrato );
        verificandoVagasLivresPrevisaoSQL.setParametro("CODVAGA", codigoVaga );

        if( verificandoVagasLivresPrevisaoSQL.proximo() ){
            size = verificandoVagasLivresPrevisaoSQL.getValorInt("QTD");
        }

        return new BigDecimal(size);
    }

    public void validaDadosInsert() throws Exception{

        /*BigDecimal usuario = AuthenticationInfo.getCurrent().getUserID();

        if(usuario != BigDecimal.ZERO){
            ErroUtils.disparaErro("As vagas não podem ser inseridas pelo <b>Usuário</b>" +
                    "<br>Somente a rotina irá <b>cadastrar</b> as Vagas!");
        }*/
    }

    public void validaUpdate() throws Exception{

        DynamicVO vagaPrev = JapeFactory.dao("MGSCT_Vagas_Previsao_Contrato").findByPK(vo.asBigDecimal("NUCONTRVAGA"));
        if(vagaPrev.asTimestamp("DTFIM") != null){
            ErroUtils.disparaErro("<b>Data Final</b> da previsão da vaga não pode ser alterada!");
        }

        if( vo.asString("PREVUNID").equalsIgnoreCase(String.valueOf("S")) ){
            ErroUtils.disparaErro("Datas da previsão da vaga não pode ser alterada esta vinculada a uma unidade!");
        }

        //subtrairVagaPrevisaoContrato = true;
    }

    //descontinuado - Sugestão do Juliano para que se a data estiver diferente de nulo alerta
    public void validaDadosUpdate(DynamicVO oldvo) throws Exception {
       Boolean dataFimNovoPreenchido = vo.asTimestamp("DTFIM") != null;
       Boolean dataFimAntigoPreenchido = oldvo.asTimestamp("DTFIM") != null;

        if (dataFimNovoPreenchido){
            if (vo.asTimestamp("DTFIM").compareTo(vo.asTimestamp("DTINICIO")) < 0){
                ErroUtils.disparaErro("Data final não pode ser menor que a data inicial!");
            }
        }

       if (dataFimAntigoPreenchido && !dataFimNovoPreenchido){
           ErroUtils.disparaErro("Vaga não pode ser reativada!");
       }

        if (!dataFimAntigoPreenchido && dataFimNovoPreenchido) {
            Timestamp dtfim = vo.asTimestamp("DTFIM");
            if (dtfim.compareTo(TimeUtils.getNow())>0){
                ErroUtils.disparaErro("Data de finalização da vaga não pode ser maior que hoje!");
            }

            if (vagaAtivaPrevisaoUnidade()){
                ErroUtils.disparaErro("Vaga ainda se encontra ativa na previsao da unidade, deve ser desabilitado primeiro!");
            }

        }
    }

    public boolean vagaAtivaPrevisaoUnidade() throws Exception {
        JapeWrapper vagasPrevisaoUnidadeDAO = JapeFactory.dao("MGSCT_Vagas_Previsao_Unidade");
        DynamicVO vagasPrevisaoUnidadeVO = vagasPrevisaoUnidadeDAO.findOne("CODVAGA = ? AND DTFIM IS NULL", vo.asString("CODVAGA"));
        if (vagasPrevisaoUnidadeVO != null){
            return true;
        }

        return false;
    }

    public void alteraDadosDerivados() throws Exception {
        if(subtrairVagaPrevisaoContrato){
            PrevisoesContratoModel previsoesContratoModel = new PrevisoesContratoModel(vo.asBigDecimal("NUCONTRPREV"));
            previsoesContratoModel.diminuirUmQuantidadeContrata();
            subtrairVagaPrevisaoContrato = false;
        }
    }

    public ArrayList<DynamicVO> getVagasLivres(BigDecimal numeroUnicoPrevisaoContrato) throws Exception {
        NativeSqlDecorator verificandoVagasLivresSQL = new NativeSqlDecorator("SELECT\n" +
                "NUCONTRVAGA\n" +
                "FROM ( SELECT \n" +
                "NUCONTRVAGA\n" +
                ", (\n" +
                "  CASE\n" +
                "    WHEN EXISTS\n" +
                "      (SELECT CODVAGA\n" +
                "      FROM MGSTCTUNIDPREVVAGA\n" +
                "      WHERE CODVAGA      = MGSTCTCONTRATOVAGA.CODVAGA\n" +
                "      AND (TRUNC(DTFIM) >= TRUNC(SYSDATE)\n" +
                "      OR DTFIM          IS NULL)\n" +
                "      )\n" +
                "    THEN 'S'\n" +
                "    ELSE 'N'\n" +
                "  END) PREVUNID\n" +
                "FROM MGSTCTCONTRATOVAGA\n" +
                "WHERE\n" +
                "NUCONTRPREV = :NUCONTRPREV\n" +
                "AND DTFIM IS NULL ) F\n" +
                "WHERE\n" +
                "F.PREVUNID = 'N'");
        verificandoVagasLivresSQL.setParametro("NUCONTRPREV", numeroUnicoPrevisaoContrato);
        ArrayList<DynamicVO> vagaLivresVOs = new ArrayList();
        while(verificandoVagasLivresSQL.proximo()){
            BigDecimal numeroUnicoVaga = verificandoVagasLivresSQL.getValorBigDecimal("NUCONTRVAGA");
            JapeWrapper mgsct_vagas_previsao_contrato = JapeFactory.dao("MGSCT_Vagas_Previsao_Contrato");
            DynamicVO vagaLivre = mgsct_vagas_previsao_contrato.findByPK(numeroUnicoVaga);
            vagaLivresVOs.add(vagaLivre);
        }
        return  vagaLivresVOs;
    }

    public void validaCamposUpdate(HashMap<String, Object[]> campos) throws Exception {
        String mensagemErro = "";

        if( campos.containsKey("DTFIM") ){
            subtrairVagaPrevisaoContrato = true;
        }

        //todo melhorar a descricao do campo pegando do dicionario de dados
        if( campos.containsKey("CODVAGA") ){mensagemErro += "<b>Vaga</b> não pode ser alterada!";}

        if (mensagemErro != "") {
            ErroUtils.disparaErro(mensagemErro);
        }
    }
}
