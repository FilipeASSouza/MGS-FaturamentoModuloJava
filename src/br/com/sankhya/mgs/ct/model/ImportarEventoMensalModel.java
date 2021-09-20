package br.com.sankhya.mgs.ct.model;

import br.com.lugh.performance.ExtensaoLogger;
import br.com.sankhya.bh.utils.LerArquivoDeDadosDecorator;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.SWRepositoryUtils;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static br.com.sankhya.bh.utils.ErroUtils.disparaErro;

public class ImportarEventoMensalModel {
    private BigDecimal numeroUnico;
    private BigDecimal numeroUnicoModalidade;
    private BigDecimal numeroUnicoModalidadeContrato;
    private BigDecimal numeroUnicoTipoServico;
    private BigDecimal numeroContrato;
    private String motivoCarga;
    private BigDecimal codCusto;
    private BigDecimal codTipoFatura;
    private Map<Integer, Integer> qtdErrosByFlag = new HashMap<>();
    private String mensagem = "";
    private Date dataLancamentoCusto;
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Importacao_Evento_Mensal");
    private JapeWrapper daoAnx = JapeFactory.dao("AnexoSistema");
    private JapeWrapper daoUser = JapeFactory.dao("Usuario");
    private DynamicVO vo;
    private JdbcWrapper jdbcWrapper;
    private ExtensaoLogger logger = ExtensaoLogger.getLogger();
    NativeSqlDecorator consultaFaturaSQL;
    NativeSqlDecorator consultaUnidadeFaturSQL;
    NativeSqlDecorator consultaCustoFaturaSQL;
    
    public ImportarEventoMensalModel(JdbcWrapper jdbc) {
        this.jdbcWrapper = jdbc;
        consultaFaturaSQL = new NativeSqlDecorator("select distinct codunidadefatur from mgstctlctcusto where numcontrato = :numcontrato and codunidadefatur = :codunidadefatur and dtlanccusto = :dtlanccusto and codtipofatura = :codtipofatura", this.jdbcWrapper);
        consultaUnidadeFaturSQL = new NativeSqlDecorator("select distinct codsite from mgstctcontrcent where numcontrato = :numcontrato and codsite = :codsite", this.jdbcWrapper);
        consultaCustoFaturaSQL = new NativeSqlDecorator("select codcusto, codtipofatura from mgstctevtcus where codevento = :codevento and ROWNUM < 2", this.jdbcWrapper);
    }
    
    public void setNumeroUnico(BigDecimal numeroUnico) {
        this.numeroUnico = numeroUnico;
    }
    
    
    public void setNumeroUnicoModalidade(BigDecimal numeroUnicoModalidade) {
        this.numeroUnicoModalidade = numeroUnicoModalidade;
    }
    
    public void setNumeroUnicoTipoServico(BigDecimal numeroUnicoTipoServico) {
        this.numeroUnicoTipoServico = numeroUnicoTipoServico;
    }
    
    public void setNumeroContrato(BigDecimal numeroContrato) {
        this.numeroContrato = numeroContrato;
    }
    
    public void setMotivoCarga(String motivoCarga) {
        this.motivoCarga = motivoCarga;
    }
    
    public void importa() throws Exception {
        vo = dao.findByPK(numeroUnico);
        if ("P".equals(vo.asString("STATUS"))) {
            disparaErro("Arquivo ja processado!");
        }
        
        DynamicVO modalidadeContratoVO = JapeFactory
                .dao("MGSCT_Modalidade_Contrato")
                .findOne("NUTIPOSERVICO = ? AND NUMCONTRATO = ? AND CODTPN = ?",
                        numeroUnicoTipoServico,
                        numeroContrato,
                        numeroUnicoModalidade);
        
        if (modalidadeContratoVO == null) {
            disparaErro("Modalidade por contrato não localizado!");
        }
        
        numeroUnicoModalidadeContrato = modalidadeContratoVO.asBigDecimal("NUMODALIDADE");
        
        try {
            for (String arquivo : getListaDeArquivos("MGSCT_Importacao_Evento_Mensal", this.numeroUnico)) {
                processaPlanilha(arquivo);
            }
            logger.info(qtdErrosByFlag.toString());
            for (Map.Entry<Integer, Integer> erros : qtdErrosByFlag.entrySet()) {
                switch (erros.getKey()) {
                    case 7:
                        mensagem += " ,Não houve vinculação do evento com o código custo e fatura! (" + erros.getValue() + ")";
                        break;
                    case 2: // CODUNIDADEFATUR
                        mensagem += " ,Não existe unidade de faturamento neste contrato! (" + erros.getValue() + ")";
                        break;
                    case 99:
                        mensagem += " ,Já existe planilha para essa unidade nesse periodo!(" + erros.getValue() + ")";
                        break;
                    case 1:
                        mensagem += "";
                        break;
                    default:
                        mensagem += " ,Existem erros no arquivo, favor verificar! (" + erros.getValue() + ")";
                        break;
                }
            }
            
            
            FluidUpdateVO fcvo = dao.prepareToUpdate(vo);
            fcvo.set("STATUS", "P");
            fcvo.set("LOG", "OK, Motivo: " + motivoCarga + mensagem);
            fcvo.update();
        } catch (Exception e) {
            logger.severe("Erro ao processar arquivo: ", e);
            FluidUpdateVO fcvo = dao.prepareToUpdate(vo);
            fcvo.set("STATUS", "E");
            fcvo.set("LOG", "Erro ao processar arquivo: " + e);
            fcvo.update();
        } finally {
            if (consultaCustoFaturaSQL != null)
                consultaCustoFaturaSQL.close();
            if (consultaUnidadeFaturSQL != null)
                consultaUnidadeFaturSQL.close();
            if (consultaFaturaSQL != null)
                consultaFaturaSQL.close();
        }
    }
    
    private void processaPlanilha(String arquivo) throws Exception {
        
        
        JapeWrapper detalhamentoCustoDAO = JapeFactory.dao("MGSCT_Detalhamento_Custo");//MGSCT_Detalhamento_Custo > MGSTCTEVTMENSAL
        LerArquivoDeDadosDecorator planilha = new LerArquivoDeDadosDecorator(arquivo, "xlsx");
        
        
        planilha.setColuna("CODUNIDADEFATUR", 0);
        planilha.setColuna("CODVAGA", 1);
        planilha.setColuna("CODSERVMATERIAL", 2);
        planilha.setColuna("CODTIPOPOSTO", 3);
        planilha.setColuna("CODCARGO", 4);
        planilha.setColuna("CODPRONTUARIO", 5);
        planilha.setColuna("NOME", 6);
        planilha.setColuna("CODEVENTO", 7);
        planilha.setColuna("DTINIEVENTO", 8);
        planilha.setColuna("DTFIMEVENTO", 9);
        planilha.setColuna("DSCEVENTO", 10);
        planilha.setColuna("INFEVENTO", 11);
        planilha.setColuna("VLRUNIEVENTO", 12);
        planilha.setColuna("QTDEVENTO", 13);
        planilha.setColuna("VLRTOTEVENTO", 14);
        planilha.setColuna("COMPEVENTO", 15);
        planilha.setColuna("COMPLANC", 16);
        planilha.setColuna("COMPFATU", 17);
        planilha.setColuna("PERCITF", 18);
        planilha.setColuna("PERCTXADM", 19);
        planilha.setColuna("VLRTXADM", 20);
        planilha.setColuna("DTLCCUSTO", 21);
        planilha.setColuna("CODCUSTO", 22);
        planilha.setColuna("CODTIPOFATURA", 23);
        
        
        while (planilha.proximo()) {
            
            //ERRO CURSOR
            
            if (validaconsultafatura(planilha)) continue;
            
            if (validaconsultaunidade(planilha)) continue;
            
            dataLancamentoCusto = planilha.getValorTimestamp("DTLCCUSTO");
            
            if (validaconsultacusto(planilha)) continue;
            
            BigDecimal valorUnitario = planilha.getValorBigDecimal2("VLRUNIEVENTO");
            
            FluidCreateVO detalhamentoCustoFCVO = detalhamentoCustoDAO.create();
            
            detalhamentoCustoFCVO.set("NUMCONTRATO", numeroContrato);//pega do sistema
            detalhamentoCustoFCVO.set("NUMODALIDADE", numeroUnicoModalidadeContrato); //pega do sistema
            detalhamentoCustoFCVO.set("CODSITLANC", BigDecimal.ZERO);
            detalhamentoCustoFCVO.set("TIPLANCEVENTO", "M");
            detalhamentoCustoFCVO.set("CODCARGA", numeroUnico);
            detalhamentoCustoFCVO.set("MTVCARGA", motivoCarga);//pedir para o usuário digitar o motivo
            detalhamentoCustoFCVO.set("DHINS", TimeUtils.getNow());//data de quem inseriu
            detalhamentoCustoFCVO.set("USUINS", daoUser.findByPK(AuthenticationInfo.getCurrent().getUserID()).asString("NOMEUSU"));//usuário que inseriu
            detalhamentoCustoFCVO.set("CODUNIDADEFATUR", planilha.getValorBigDecimal("CODUNIDADEFATUR"));
            detalhamentoCustoFCVO.set("CODVAGA", planilha.getValorString("CODVAGA"));
            detalhamentoCustoFCVO.set("CODSERVMATERIAL", planilha.getValorBigDecimal("CODSERVMATERIAL"));
            detalhamentoCustoFCVO.set("CODTIPOPOSTO", planilha.getValorBigDecimal("CODTIPOPOSTO"));
            detalhamentoCustoFCVO.set("CODCARGO", planilha.getValorBigDecimal("CODCARGO"));
            detalhamentoCustoFCVO.set("CODPRONTUARIO", planilha.getValorBigDecimal("CODPRONTUARIO"));
            detalhamentoCustoFCVO.set("NOME", planilha.getValorString("NOME"));
            detalhamentoCustoFCVO.set("CODEVENTO", planilha.getValorBigDecimal("CODEVENTO"));
            detalhamentoCustoFCVO.set("DTINIEVENTO", planilha.getValorTimestamp("DTINIEVENTO"));
            detalhamentoCustoFCVO.set("DTFIMEVENTO", planilha.getValorTimestamp("DTFIMEVENTO"));
            detalhamentoCustoFCVO.set("DSCEVENTO", planilha.getValorString("DSCEVENTO"));
            detalhamentoCustoFCVO.set("INFEVENTO", planilha.getValorString("INFEVENTO"));
            detalhamentoCustoFCVO.set("VLRUNIEVENTO", valorUnitario);
            detalhamentoCustoFCVO.set("QTDEVENTO", arredondaValor(planilha.getValorBigDecimal("QTDEVENTO")));
            detalhamentoCustoFCVO.set("VLRTOTEVENTO", arredondaValor(planilha.getValorBigDecimal("VLRTOTEVENTO")));
            detalhamentoCustoFCVO.set("COMPEVENTO", planilha.getValorBigDecimal("COMPEVENTO"));
            detalhamentoCustoFCVO.set("COMPLANC", planilha.getValorBigDecimal("COMPLANC"));
            detalhamentoCustoFCVO.set("COMPFATU", planilha.getValorBigDecimal("COMPFATU"));
            detalhamentoCustoFCVO.set("PERCITF", arredondaValor(planilha.getValorBigDecimal("PERCITF")));
            detalhamentoCustoFCVO.set("PERCTXADM", arredondaValor(planilha.getValorBigDecimal("PERCTXADM")));
            detalhamentoCustoFCVO.set("VLRTXADM", arredondaValor(planilha.getValorBigDecimal("VLRTXADM")));
            detalhamentoCustoFCVO.set("DTLCCUSTO", planilha.getValorTimestamp("DTLCCUSTO"));
            detalhamentoCustoFCVO.set("CODCUSTO", codCusto);
            detalhamentoCustoFCVO.set("CODTIPOFATURA", codTipoFatura);
            
            detalhamentoCustoFCVO.save();
        }
    }
    
    private boolean validaconsultacusto(LerArquivoDeDadosDecorator planilha) throws Exception {
        String dataLancamentoCusto2 = TimeUtils.formataDDMMYYYY(planilha.getValorTimestamp("DTLCCUSTO"));
        consultaFaturaSQL.cleanParameters();
        consultaFaturaSQL.setParametro("numcontrato", vo.asBigDecimal("NUMCONTRATO"));
        consultaFaturaSQL.setParametro("codunidadefatur", planilha.getValorBigDecimal("CODUNIDADEFATUR"));
        consultaFaturaSQL.setParametro("dtlanccusto", dataLancamentoCusto2);
        consultaFaturaSQL.setParametro("codtipofatura", codTipoFatura);
        
        if (consultaFaturaSQL.proximo()) {
            logger.warning("Já existe planilha" + planilha.getValorBigDecimal("CODUNIDADEFATUR"));
            novoErro(99);
            return true;
        }
        return false;
    }
    
    private void novoErro(Integer flagerror) {
        Integer integer = qtdErrosByFlag.getOrDefault(flagerror, 0);
        integer++;
        qtdErrosByFlag.put(flagerror, integer);
    }
    
    private boolean validaconsultaunidade(LerArquivoDeDadosDecorator planilha) throws Exception {
        if (planilha.getValorBigDecimal("CODUNIDADEFATUR") != null) {
            
            consultaUnidadeFaturSQL.cleanParameters();
            consultaUnidadeFaturSQL.setParametro("numcontrato", vo.asBigDecimal("NUMCONTRATO"));
            consultaUnidadeFaturSQL.setParametro("codsite", planilha.getValorBigDecimal("CODUNIDADEFATUR"));
            if (!consultaUnidadeFaturSQL.proximo()) {
                logger.warning("Não encontrado consultaUnidadeFaturSQL: " + planilha.getValorBigDecimal("CODUNIDADEFATUR"));
                novoErro(2);
                return true;
            }
        }
        return false;
    }
    
    private boolean validaconsultafatura(LerArquivoDeDadosDecorator planilha) throws Exception {
        consultaCustoFaturaSQL.cleanParameters();
        consultaCustoFaturaSQL.setParametro("codevento", planilha.getValorBigDecimal("CODEVENTO"));
        if (consultaCustoFaturaSQL.proximo()) {
            codCusto = consultaCustoFaturaSQL.getValorBigDecimal("CODCUSTO");
            codTipoFatura = consultaCustoFaturaSQL.getValorBigDecimal("CODTIPOFATURA");
        } else {
            logger.warning("Não encontrado consultaCustoFaturaSQL" + planilha.getValorBigDecimal("CODEVENTO"));
            novoErro(7);
            return true;
        }
        return false;
    }
    
    private BigDecimal arredondaValor(BigDecimal valor) {
        if (valor == null)
            return null;
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }
    
    private Collection<String> getListaDeArquivos(String instancia, BigDecimal numeroUnico) {
        
        //pega a lista de arquivos anexados
        Collection<DynamicVO> listaAnexoSistema = new ArrayList();
        Collection<String> listaArquivos = new ArrayList();
        try {
            listaAnexoSistema = daoAnx
                    .find("NOMEINSTANCIA = ? AND PKREGISTRO = ?", instancia, numeroUnico.toString() + "_" + instancia);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        
        String diretorioBase = "";
        try {
            diretorioBase = SWRepositoryUtils.getBaseFolder().getAbsolutePath();
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
        String diretorioArquivo = diretorioBase + "/Sistema/Anexos/" + instancia + "/";
        
        
        for (DynamicVO anexoSitema : listaAnexoSistema) {
            String arquivo = diretorioArquivo + anexoSitema.asString("CHAVEARQUIVO");
            listaArquivos.add(arquivo);
        }
        
        return listaArquivos;
    }
}
