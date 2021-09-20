package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class Utils {
    @NotNull
    public static DynamicVO validaPreco(NativeSqlDecorator nativeSqlDDecorator, JapeWrapper mgsct_valores_produtosDAO) throws Exception {
        BigDecimal numeroUnicoValoresProdutos = BigDecimal.ZERO;
        if (nativeSqlDDecorator.proximo()) {
            numeroUnicoValoresProdutos = nativeSqlDDecorator.getValorBigDecimal("NUCONTRMATSRV");
            if (numeroUnicoValoresProdutos == null) {
                numeroUnicoValoresProdutos = BigDecimal.ZERO;
            }
        }
        
        if (BigDecimal.ZERO.equals(numeroUnicoValoresProdutos)) {
            ErroUtils.disparaErro("Pre\u00e7o n\u00e3o localizado, favor verificar dados lancados!");
        }
        
        DynamicVO mgsct_valores_produtosVO = mgsct_valores_produtosDAO.findByPK(numeroUnicoValoresProdutos);
        if (mgsct_valores_produtosVO == null) {
            ErroUtils.disparaErro("Pre\u00e7o n\u00e3o localizado, favor verificar dados lancados!");
        }
        return mgsct_valores_produtosVO;
    }
    
}
