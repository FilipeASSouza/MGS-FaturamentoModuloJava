package br.com.sankhya.mgs.ct.processamento.processamentomodel;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
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

public class RtnContrInsLancCustoUPGestorAnexo extends ProcessarSuper implements Processar {
    public RtnContrInsLancCustoUPGestorAnexo() {
        super();
    }

    @Override
    public boolean executar() throws Exception {
        Boolean executado = false;//todo refatorar pra super
        int numeroPaginasGerado;
        try {
            super.executar();

            final Map<String, String> parametrosExecutacao = this.getParametrosExecutacao();//todo refatorar colocando na super

            //relVO = (DynamicVO) this.dwfEntityFacade.findEntityByPrimaryKeyAsVO("Relatorio", new Object[]{17});

            JapeWrapper relatorioDAO = JapeFactory.dao("Relatorio");

            BigDecimal codigoRelatorioPlanilhaCusto = JapeFactory.dao("ParametroSistema").findOne("CHAVE = 'AD_MGSCTREPLCUS' AND CODUSU = 0").asBigDecimal("INTEIRO");

            DynamicVO relVO = relatorioDAO.findByPK(codigoRelatorioPlanilhaCusto);

            Report modeloImpressao = null;

            modeloImpressao = ReportManager.getInstance().getReport(relVO.asBigDecimal("NURFE"), EntityFacadeFactory.getDWFFacade());

            JasperPrint jasperPrint = null;

            HashMap<String, Object> parametrosRelatorio = new HashMap<String, Object>();

            String pastaDeModelosParaImpressão = JapeFactory.dao("ParametroSistema").findOne("CHAVE = 'SERVDIRMOD' AND CODUSU = 0").asString("TEXTO");

            parametrosRelatorio.put("P_DTLANCUSTOINI", TimeUtils.toTimestamp(parametrosExecutacao.get("DTLANCCUSTO"),"yyyyMMdd"));
            parametrosRelatorio.put("P_DTLANCUSTOFIN", TimeUtils.toTimestamp(parametrosExecutacao.get("DTLANCCUSTO"),"yyyyMMdd"));
            parametrosRelatorio.put("P_NUMCONTRATO", new BigDecimal(parametrosExecutacao.get("NUMCONTRATO")));
            parametrosRelatorio.put("P_CODUNIDADEFATUR", new BigDecimal(parametrosExecutacao.get("CODUNIDADEFATUR")));
            parametrosRelatorio.put("P_CODTIPOFATURA", new BigDecimal(parametrosExecutacao.get("CODTIPOFATURA")));
            parametrosRelatorio.put("P_FISCGEST", "G");
            parametrosRelatorio.put("PDIR_MODELO", pastaDeModelosParaImpressão);

            jasperPrint = modeloImpressao.buildJasperPrint(parametrosRelatorio, jdbc.getConnection());

            numeroPaginasGerado = jasperPrint.getPages().size();

            mensagem = "Numero de paginas gerado: " + numeroPaginasGerado;

            byte[] arquivoBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            if (numeroPaginasGerado > 0) {

                JapeWrapper dao = JapeFactory.dao("MGSCT_Relatorio_Anexo");
                final FluidCreateVO fluidCreateVO = dao.create();

                fluidCreateVO.set("NUMCONTRATO", new BigDecimal(parametrosExecutacao.get("NUMCONTRATO")));
                fluidCreateVO.set("CODTIPOFATURA", new BigDecimal(parametrosExecutacao.get("CODTIPOFATURA")));
                fluidCreateVO.set("CODUNIDADEFATUR", new BigDecimal(parametrosExecutacao.get("CODUNIDADEFATUR")));
                fluidCreateVO.set("DTLANCCUSTO", TimeUtils.toTimestamp(parametrosExecutacao.get("DTLANCCUSTO"), "yyyyMMdd"));
                fluidCreateVO.set("TIPGESTOR", "G");
                fluidCreateVO.set("DTRLTANEXO", TimeUtils.getNow());
                fluidCreateVO.set("DHINS", TimeUtils.getNow());
                fluidCreateVO.set("USUINS", getLogin());
                fluidCreateVO.set("ANEXO", arquivoBytes);


                hnd = JapeSession.open();
                final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
                jdbc = dwfFacade.getJdbcWrapper();
                jdbc.openSession();


                hnd.execWithTX(new JapeSession.TXBlock() {
                    public void doWithTx() throws Exception {
                        DynamicVO save = fluidCreateVO.save();
                        NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator(this, "RtnContrInsLancCustoUPAnexoUpdateLancCusto.sql");
                        nativeSqlDecorator.setParametro("NURLTANEXO", save.asBigDecimal("NURLTANEXO"));
                        nativeSqlDecorator.setParametro("NUMCONTRATO", new BigDecimal(parametrosExecutacao.get("NUMCONTRATO")));
                        nativeSqlDecorator.setParametro("CODTIPOFATURA", new BigDecimal(parametrosExecutacao.get("CODTIPOFATURA")));
                        nativeSqlDecorator.setParametro("CODUNIDADEFATUR", new BigDecimal(parametrosExecutacao.get("CODUNIDADEFATUR")));
                        nativeSqlDecorator.setParametro("DTLANCCUSTO", TimeUtils.toTimestamp(parametrosExecutacao.get("DTLANCCUSTO"), "yyyyMMdd"));
                        nativeSqlDecorator.setParametro("TIPGESTOR", "G");

                        nativeSqlDecorator.atualizar();
                    }
                });
            }
            executado = true;
        } catch (Exception e) {
            throw new Exception("Erro ao executar rotina Java RtnContrInsLancCustoUPAnexo: " + e);
        } finally {
            super.finalizar();
        }
        return executado;
    }
}
