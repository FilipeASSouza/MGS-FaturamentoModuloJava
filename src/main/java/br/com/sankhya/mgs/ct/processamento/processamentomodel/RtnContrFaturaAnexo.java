package br.com.sankhya.mgs.ct.processamento.processamentomodel;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.Report;
import br.com.sankhya.modelcore.util.ReportManager;
import com.sankhya.util.TimeUtils;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class RtnContrFaturaAnexo extends ProcessarSuper implements Processar {
    public RtnContrFaturaAnexo() {
        super();
    }

    @Override
    public boolean executar() throws Exception {
        Boolean executado = false;//todo refatorar pra super
        int numeroPaginasGerado;
        try {
            super.executar();

            final Map<String, String> parametrosExecutacao = this.getParametrosExecutacao();//todo refatorar colocando na super

            byte[] arquivoBytes = null;
            try {
                JapeWrapper relatorioDAO = JapeFactory.dao("Relatorio");

                BigDecimal codigoRelatorioPlanilhaCusto = JapeFactory.dao("ParametroSistema").findOne("CHAVE = 'AD_MGSCTRELFAT' AND CODUSU = 0").asBigDecimal("INTEIRO");

                DynamicVO relVO = relatorioDAO.findByPK(codigoRelatorioPlanilhaCusto);

                Report modeloImpressao = null;

                modeloImpressao = ReportManager.getInstance().getReport(relVO.asBigDecimal("NURFE"), EntityFacadeFactory.getDWFFacade());

                JasperPrint jasperPrint = null;

                HashMap<String, Object> parametrosRelatorio = new HashMap<String, Object>();

                String pastaDeModelosParaImpressão = JapeFactory.dao("ParametroSistema").findOne("CHAVE = 'SERVDIRMOD' AND CODUSU = 0").asString("TEXTO");

                parametrosRelatorio.put("P_NUFATURA", new BigDecimal(parametrosExecutacao.get("NUFATURA")));
                parametrosRelatorio.put("PDIR_MODELO", pastaDeModelosParaImpressão);

                jasperPrint = modeloImpressao.buildJasperPrint(parametrosRelatorio, jdbc.getConnection());

                numeroPaginasGerado = jasperPrint.getPages().size();

                mensagem = "Numero de paginas gerado: " + numeroPaginasGerado;

                arquivoBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            } catch (Exception e) {
                throw new Exception("Erro ao proecessar relatorio: " + e);
            }
            if (numeroPaginasGerado > 0) {

                JapeWrapper dao = JapeFactory.dao("MGSCT_Fatura_Anexo");//MGSTCTFTRANEXO

                NativeSqlDecorator verificarAnexo = new NativeSqlDecorator("SELECT NUFATURA FROM MGSTCTFTRANEXO WHERE NUFATURA = :NUFATURA");
                verificarAnexo.setParametro("NUFATURA", parametrosExecutacao.get("NUFATURA"));
                if(verificarAnexo.proximo()){
                    ErroUtils.disparaErro("Fatura já enviada para o site!");
                }

                final FluidCreateVO relatorioAnexoFCVO = dao.create();

                relatorioAnexoFCVO.set("NUFATURA", new BigDecimal(parametrosExecutacao.get("NUFATURA")));

                relatorioAnexoFCVO.set("DHINS", TimeUtils.getNow());
                relatorioAnexoFCVO.set("USUINS", getLogin());
                relatorioAnexoFCVO.set("ANEXO", arquivoBytes);

                hnd = JapeSession.open();
                final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
                jdbc = dwfFacade.getJdbcWrapper();
                jdbc.openSession();

                hnd.execWithTX(new JapeSession.TXBlock() {
                    public void doWithTx() throws Exception {
                        DynamicVO save = relatorioAnexoFCVO.save();
                    }
                });
            }
            executado = true;
        } catch (Exception e) {
            throw new Exception("Erro ao executar rotina Java RtnContrFaturaAnexo: " + e);
        } finally {
            JapeSession.close(hnd);
            JdbcWrapper.closeSession(jdbc);
            super.finalizar();
        }
        return executado;
    }
}
