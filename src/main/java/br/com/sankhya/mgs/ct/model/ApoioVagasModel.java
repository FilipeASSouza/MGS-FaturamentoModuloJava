package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import com.sankhya.util.StringUtils;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Entidade: MGSCT_Apoio_Vagas
 * Tabela: MGSTCTVAGAS
 * Chave: CODVAGA
 */
public class ApoioVagasModel {
    private JapeWrapper dao;
    private DynamicVO vo;

    public ApoioVagasModel() {
        inicialzaVariaveis();
    }


    public ApoioVagasModel(DynamicVO dynamicVO) {
        inicialzaVariaveis();
        this.vo = dynamicVO;
    }

    private void inicialzaVariaveis() {
        dao = JapeFactory.dao("MGSCT_Apoio_Vagas");
    }

    /**
     * @param quantidade
     * @param siglaTipo
     * @return ArrayList<DynamicVO>
     *     retorna um lista de vragas criadas
     * @throws Exception
     */
    public ArrayList<DynamicVO> criaVagas(BigDecimal quantidade, String siglaTipo) throws Exception {
        BigDecimal ultimoNumeroSequencial = getUltimoNumeroSequencial(siglaTipo);
        ArrayList<DynamicVO> listaVagasCriadas = new ArrayList();
        for (int i = 0; i < quantidade.intValue(); i++) {
            ultimoNumeroSequencial = ultimoNumeroSequencial.add(BigDecimal.ONE);
            String codVaga = siglaTipo+ StringUtils.formatNumeric("0000000", ultimoNumeroSequencial);

            FluidCreateVO fluidCreateVO = dao.create();
            fluidCreateVO.set("CODVAGA", codVaga);
            fluidCreateVO.set("SIGLA", siglaTipo);
            fluidCreateVO.set("NUMSEQ", ultimoNumeroSequencial);
            fluidCreateVO.set("DTCRIAO", TimeUtils.getNow());
            fluidCreateVO.set("DISPONIVEL", "S");
            DynamicVO save = fluidCreateVO.save();
            listaVagasCriadas.add(save);
        }
        return listaVagasCriadas;
    }

    private BigDecimal getUltimoNumeroSequencial(String siglaTipo) throws Exception {
        NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator("SELECT MAX(NUMSEQ) AS NUMSEQ FROM MGSTCTVAGAS WHERE SIGLA = :SIGLA");
        nativeSqlDecorator.setParametro("SIGLA", siglaTipo);
        nativeSqlDecorator.proximo();
        BigDecimal numseq = nativeSqlDecorator.getValorBigDecimal("NUMSEQ");
        if (numseq == null) {
            numseq = BigDecimal.ZERO;
        }
        return numseq;
    }
}
