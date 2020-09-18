package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.LerArquivoDeDadosDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Entidade: MGSCT_Importacao_Valores
 * Tabela: MGSTCTIMPVLR
 * Chave: NUIMPVLR
 */

public class ImportarValoresModel {
    private BigDecimal numerUnico;
    private String tipoArquio;
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Importacao_Valores");
    private DynamicVO vo;

    public void setNumerUnico(BigDecimal numerUnico) {
        this.numerUnico = numerUnico;
    }

    public void setTipoArquivo(String tipoArquio) {
        this.tipoArquio = tipoArquio;
    }

    public void importa() throws Exception {
        vo = dao.findByPK(numerUnico);
        if ("P".equals(vo.asString("STATUS"))){
            br.com.sankhya.bh.utils.ErroUtils.disparaErro("Arquivo ja processado!");
        }

        try {
            for (String arquivo : getListaDeArquivos("MGSCT_Importacao_Valores", this.numerUnico)) {
                processaPlanilha(arquivo);
            }
            FluidUpdateVO fcvo = dao.prepareToUpdate(vo);
            fcvo.set("STATUS","P");
            fcvo.set("LOG","OK");
            fcvo.update();
        }catch (Exception e){
            FluidUpdateVO fcvo = dao.prepareToUpdate(vo);
            fcvo.set("STATUS","E");
            fcvo.set("LOG","Erro ao processar arquivo: "+e);
            fcvo.update();
        }
    }

    private void processaPlanilha(String arquivo) throws Exception {
        switch (tipoArquio) {
            case "SR"://tabela MGSTCTCONTRVLRSERV MGSCT_Valores_Produtos
                processaPlanilhaServico(arquivo);
                break;
            case "PS"://tabela MGSTCTCONTRVLRPS MGSCT_Valores_Eventos
                processaPlanilhaPosto(arquivo);
                break;
        }
    }

    private void processaPlanilhaServico(String arquivo) throws Exception {
        JapeWrapper valoresProdutosDAO = JapeFactory.dao("MGSCT_Valores_Produtos");//MGSTCTCONTRVLRSERV
        LerArquivoDeDadosDecorator planilha = new LerArquivoDeDadosDecorator(arquivo, "xlsx");

        planilha.setColuna("NUMCONTRATO", 0);
        planilha.setColuna("CODTPN", 1);
        planilha.setColuna("CODSERVMATERIAL", 2);
        planilha.setColuna("CODEVENTO", 3);
        planilha.setColuna("NROOCORRENCIA", 4);
        planilha.setColuna("DTINICIO", 5);
        planilha.setColuna("DTFIM", 6);
        planilha.setColuna("ALIQISS", 7);
        planilha.setColuna("VLRTOTAL", 8);
        planilha.setColuna("DTREFERCCT", 9);
        planilha.setColuna("ALIQADM", 10);

        while(planilha.proximo()) {
            int valoresProdutosNumeroRegstrosIguais = valoresProdutosDAO.find("NUMCONTRATO = ? AND CODSERVMATERIAL = ? AND CODEVENTO = ? AND ALIQISS = ? AND  DTINICIO = ? AND DTFIM = ? AND NROOCORRENCIA = ?",
                    planilha.getValorBigDecimal("NUMCONTRATO"),
                    planilha.getValorBigDecimal("CODSERVMATERIAL"),
                    planilha.getValorBigDecimal("CODEVENTO"),
                    planilha.getValorBigDecimal("ALIQISS"),
                    planilha.getValorTimestamp("DTINICIO"),
                    planilha.getValorTimestamp("DTFIM"),
                    planilha.getValorBigDecimal("NROOCORRENCIA")
            ).size();

            if (valoresProdutosNumeroRegstrosIguais > 0){
                br.com.sankhya.bh.utils.ErroUtils.disparaErro("Ja existem dados equivalente ao dao planilha. Planilha nao será importada!");
            }

            FluidCreateVO valoresProdutosFCVO = valoresProdutosDAO.create();
            valoresProdutosFCVO.set("NUMCONTRATO",planilha.getValorBigDecimal("NUMCONTRATO"));
            valoresProdutosFCVO.set("CODTPN",planilha.getValorBigDecimal("CODTPN"));
            valoresProdutosFCVO.set("CODSERVMATERIAL",planilha.getValorBigDecimal("CODSERVMATERIAL"));
            valoresProdutosFCVO.set("CODEVENTO",planilha.getValorBigDecimal("CODEVENTO"));
            valoresProdutosFCVO.set("NROOCORRENCIA",planilha.getValorBigDecimal("NROOCORRENCIA"));
            valoresProdutosFCVO.set("DTINICIO",planilha.getValorTimestamp("DTINICIO"));
            valoresProdutosFCVO.set("DTFIM",planilha.getValorTimestamp("DTFIM"));
            valoresProdutosFCVO.set("ALIQISS",planilha.getValorBigDecimal("ALIQISS"));
            valoresProdutosFCVO.set("VLRTOTAL",planilha.getValorBigDecimal("VLRTOTAL"));
            valoresProdutosFCVO.set("DTREFERCCT",planilha.getValorTimestamp("DTREFERCCT"));
            valoresProdutosFCVO.set("ALIQADM",planilha.getValorBigDecimal("ALIQADM"));
            valoresProdutosFCVO.save();
        }
    }

    private void processaPlanilhaPosto(String arquivo) throws Exception {
        JapeWrapper valoresEventosDAO = JapeFactory.dao("MGSCT_Valores_Eventos");//MGSTCTCONTRVLRPS
        LerArquivoDeDadosDecorator planilha = new LerArquivoDeDadosDecorator(arquivo, "xlsx");

        planilha.setColuna("NUMCONTRATO", 0);
        planilha.setColuna("CODTPN", 1);
        planilha.setColuna("CODTIPOPOSTO", 2);
        planilha.setColuna("CODEVENTO", 3);
        planilha.setColuna("NROOCORRENCIA", 4);
        planilha.setColuna("DTINICIO", 5);
        planilha.setColuna("DTFIM", 6);
        planilha.setColuna("ALIQISS", 7);
        planilha.setColuna("VLRTOTAL", 8);
        planilha.setColuna("ALIQADM", 9);


        while(planilha.proximo()) {
            int valoresEventosNumeroRegstrosIguais = valoresEventosDAO.find("NUMCONTRATO =? AND CODTIPOPOSTO = ? AND CODTPN = ? AND CODEVENTO = ? AND ALIQISS = ? AND DTINICIO = ? AND DTFIM = ? AND NROOCORRENCIA = ?",
                    planilha.getValorBigDecimal("NUMCONTRATO"),
                    planilha.getValorBigDecimal("CODTIPOPOSTO"),
                    planilha.getValorBigDecimal("CODTPN"),
                    planilha.getValorBigDecimal("CODEVENTO"),
                    planilha.getValorBigDecimal("ALIQISS"),
                    planilha.getValorTimestamp("DTINICIO"),
                    planilha.getValorTimestamp("DTFIM"),
                    planilha.getValorBigDecimal("NROOCORRENCIA")
            ).size();

            if (valoresEventosNumeroRegstrosIguais > 0){
                br.com.sankhya.bh.utils.ErroUtils.disparaErro("Ja existem dados equivalente ao dao planilha. Planilha nao será importada!");
            }

            FluidCreateVO valoresEventosFCVO = valoresEventosDAO.create();
            valoresEventosFCVO.set("NUMCONTRATO",planilha.getValorBigDecimal("NUMCONTRATO"));
            valoresEventosFCVO.set("CODTPN",planilha.getValorBigDecimal("CODTPN"));
            valoresEventosFCVO.set("CODTIPOPOSTO",planilha.getValorBigDecimal("CODTIPOPOSTO"));
            valoresEventosFCVO.set("CODEVENTO",planilha.getValorBigDecimal("CODEVENTO"));
            valoresEventosFCVO.set("NROOCORRENCIA",planilha.getValorBigDecimal("NROOCORRENCIA"));
            valoresEventosFCVO.set("DTINICIO",planilha.getValorTimestamp("DTINICIO"));
            valoresEventosFCVO.set("DTFIM",planilha.getValorTimestamp("DTFIM"));
            valoresEventosFCVO.set("ALIQISS",planilha.getValorBigDecimal("ALIQISS"));
            valoresEventosFCVO.set("VLRTOTAL",planilha.getValorBigDecimal("VLRTOTAL"));
            valoresEventosFCVO.set("ALIQADM",planilha.getValorBigDecimal("ALIQADM"));
            valoresEventosFCVO.save();
        }
    }

    private Collection<String> getListaDeArquivos(String instancia, BigDecimal numeroUnico) {

        //pega a lista de arquivos anexados
        Collection<DynamicVO> listaAnexoSistema = new ArrayList();
        Collection<String> listaArquivos = new ArrayList();
        try {
            listaAnexoSistema = JapeFactory.dao("AnexoSistema")
                    .find("NOMEINSTANCIA = ? AND PKREGISTRO = ?", instancia, numeroUnico.toString() + "_" + instancia);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        String diretorioBase = "";
        try {
            diretorioBase = JapeFactory.dao("ParametroSistema").findOne("CHAVE = 'FREPBASEFOLDER'").asString("TEXTO");
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        String diretorioArquivo = diretorioBase + "/Sistema/Anexos/" + instancia + "/";


        for (DynamicVO anexoSitema : listaAnexoSistema) {

            ArrayList listaLinhasArquivo = new ArrayList();

            String arquivo = diretorioArquivo + anexoSitema.asString("CHAVEARQUIVO");
            listaArquivos.add(arquivo);


        }

        return listaArquivos;
    }
}