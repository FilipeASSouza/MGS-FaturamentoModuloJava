package br.com.sankhya.mgs.ct.processamento.controleintegracao;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;

public class IntegracaoDetalhaCustoModel {
    static private JapeWrapper integracaoDetalhaCustoDAO = JapeFactory.dao("MGSCT_Integr_Detalha_Custo");
    private BigDecimal numeroUnicoIntegracao;

    public BigDecimal getNumeroUnicoIntegracao() {
        return numeroUnicoIntegracao;
    }

    public IntegracaoDetalhaCustoModel() {

    }

    public IntegracaoDetalhaCustoPOJO getPojo() {
        IntegracaoDetalhaCustoPOJO integracaoDetalhaCustoPOJO = new IntegracaoDetalhaCustoPOJO();
        return integracaoDetalhaCustoPOJO;
    }

    public boolean salvarIntegracao(IntegracaoDetalhaCustoPOJO i) throws Exception {
        DynamicVO dynamicVO = integracaoDetalhaCustoDAO.findOne("NUMCONTRATO = ? AND  CODUNIDADEFATUR = ? AND INTPERIODO = ? AND INTCOMPETENCIA = ? AND CODORIGEM = ? AND DHDESFAZER IS NULL",
                i.getNumeroContrato(),
                i.getCodigpUnidadeFaturamento(),
                i.getCodigoPeriodo(),
                i.getCodigoCompetencia(),
                i.getCodigoOrigem()
        );

        if (dynamicVO != null) {
            this.numeroUnicoIntegracao = dynamicVO.asBigDecimal("NUINTEGRADC");
            return false;
        } else {
            FluidCreateVO fluidCreateVO = integracaoDetalhaCustoDAO.create();
            fluidCreateVO.set("NUMCONTRATO", i.getNumeroContrato());
            fluidCreateVO.set("CODUNIDADEFATUR", i.getCodigpUnidadeFaturamento());
            fluidCreateVO.set("INTPERIODO", i.getCodigoPeriodo());
            fluidCreateVO.set("INTCOMPETENCIA", i.getCodigoCompetencia());
            fluidCreateVO.set("CODORIGEM", i.getCodigoOrigem());
            fluidCreateVO.set("DHINS", TimeUtils.getNow());
            fluidCreateVO.set("USUINS", i.getCodigoUsuarioInsercao());
            DynamicVO save = fluidCreateVO.save();
            this.numeroUnicoIntegracao = save.asBigDecimal("NUINTEGRADC");
            return true;
        }
    }

    static public void atualizaComplemento(BigDecimal numeroUnicoIntegracao, String complemento) throws Exception {
        FluidUpdateVO fluidUpdateVO = integracaoDetalhaCustoDAO.prepareToUpdateByPK(numeroUnicoIntegracao);
        fluidUpdateVO.set("COMPLEMENTO",complemento);
        fluidUpdateVO.update();
    }

    public class IntegracaoDetalhaCustoPOJO {
        private BigDecimal numeroContrato;
        private BigDecimal codigpUnidadeFaturamento;
        private BigDecimal codigoPeriodo;
        private BigDecimal codigoCompetencia;
        private BigDecimal codigoOrigem;
        private BigDecimal codigoUsuarioInsercao;

        public BigDecimal getNumeroContrato() {
            return numeroContrato;
        }

        public void setNumeroContrato(BigDecimal numeroContrato) {
            this.numeroContrato = numeroContrato;
        }

        public BigDecimal getCodigpUnidadeFaturamento() {
            return codigpUnidadeFaturamento;
        }

        public void setCodigpUnidadeFaturamento(BigDecimal codigpUnidadeFaturamento) {
            this.codigpUnidadeFaturamento = codigpUnidadeFaturamento;
        }

        public BigDecimal getCodigoPeriodo() {
            return codigoPeriodo;
        }

        public void setCodigoPeriodo(BigDecimal codigoPeriodo) {
            this.codigoPeriodo = codigoPeriodo;
        }

        public BigDecimal getCodigoCompetencia() {
            return codigoCompetencia;
        }

        public void setCodigoCompetencia(BigDecimal codigoCompetencia) {
            this.codigoCompetencia = codigoCompetencia;
        }

        public BigDecimal getCodigoOrigem() {
            return codigoOrigem;
        }

        public void setCodigoOrigem(BigDecimal codigoOrigem) {
            this.codigoOrigem = codigoOrigem;
        }

        public BigDecimal getCodigoUsuarioInsercao() {
            return codigoUsuarioInsercao;
        }

        public void setCodigoUsuarioInsercao(BigDecimal codigoUsuarioInsercao) {
            this.codigoUsuarioInsercao = codigoUsuarioInsercao;
        }
    }
}
