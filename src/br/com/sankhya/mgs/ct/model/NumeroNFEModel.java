package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel.GeraFilaFaturaAnexo;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Entidade: MGSCT_Numero_NFE
 * Tabela: MGSTCTNFENOTAS
 * Chave: NUNFENOTAS
 */
public class NumeroNFEModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Numero_NFE");
    private DynamicVO vo;

    public NumeroNFEModel() {
    }

   public void geraAnexoFatura(BigDecimal numeroFaturaInicial,BigDecimal mumeroFaturaFinal) throws Exception {
       Collection<DynamicVO> dynamicVOS = dao.find("NUFATURA >= ? AND NUFATURA <= ?", numeroFaturaInicial, mumeroFaturaFinal);
       JapeWrapper anexoFaturaDAO = JapeFactory.dao("MGSCT_Fatura_Anexo");
       for (DynamicVO vo : dynamicVOS){
           this.vo = vo;
           Collection<DynamicVO> anexoFaturaVOS = anexoFaturaDAO.find("NUFATURA = ?", vo.asBigDecimal("NUFATURA"));

           if(anexoFaturaVOS.size() == 0){
               GeraFilaFaturaAnexo geraFilaFaturaAnexo = new GeraFilaFaturaAnexo();
               geraFilaFaturaAnexo.setParametroExecucao("numeroUnicoFatura",vo.asBigDecimal("NUFATURA"));
               geraFilaFaturaAnexo.executar();
           }
       }
   }
}
