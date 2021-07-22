package br.com.sankhya.mgs.ct.dao;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.TimeUtils;
import br.com.sankhya.jape.dao.JdbcWrapper;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Entidade: MGSCT_Fila_Processamento
 * Tabela: MGSTCTFILAPROC
 * Chave: NUFILAPROC
 */
public class FilaDAO {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Fila_Processamento");
    private DynamicVO vo;
    private BigDecimal codigoUsuario;
    private Boolean comControleTransacao = false;

    public void setCodigoUsuario(BigDecimal codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public void atualizaFilaErro(BigDecimal numeroUnico, String log) throws Exception {
        atualizaFila(numeroUnico, log, "E");
    }

    public void atualizaFilaProcessado(BigDecimal numeroUnico, String log) throws Exception {
        atualizaFila(numeroUnico, log, "P");
    }


    public void buscaRegistroFilaProcessamento(BigDecimal numeroUnicoFilaProcessamento) throws Exception {
        this.vo = dao.findByPK(numeroUnicoFilaProcessamento);
    }

    private DynamicVO getVo() {
        return vo;
    }

    public void setComControleTransacao(Boolean comControleTransacao) {
        this.comControleTransacao = comControleTransacao;
    }

    public RegistroFila getRegistroFila(BigDecimal numeroUnicoFilaProcessamento) throws Exception {

        System.out.println(" BUSCA REGISTRO FILA getRegistroFila numeroUnicoFilaProcessamento = " + numeroUnicoFilaProcessamento.toString() );

        buscaRegistroFilaProcessamento(numeroUnicoFilaProcessamento);
        RegistroFila registroFila = new RegistroFila();
        registroFila.NUFILAPROC = vo.asBigDecimal("NUFILAPROC");
        registroFila.NUTIPOPROC = vo.asBigDecimal("NUTIPOPROC");
        registroFila.CHAVE = vo.asString("CHAVE");
        registroFila.STATUS = vo.asString("STATUS");
        registroFila.DHINC = vo.asTimestamp("DHINC");
        registroFila.DHPROC = vo.asTimestamp("DHPROC");
        registroFila.LOGEXEC = vo.asString("LOGEXEC");
        registroFila.CODUSU = vo.asBigDecimal("CODUSU");

        return registroFila;
    }

    public void salva(RegistroFila registroFila) throws Exception {

        System.out.println("TENTANDO SALVA FILA " + registroFila.toString() );
        FluidCreateVO fluidCreateVO = dao.create();
        fluidCreateVO.set("NUTIPOPROC", registroFila.NUTIPOPROC);
        fluidCreateVO.set("CHAVE", registroFila.CHAVE);
        fluidCreateVO.set("STATUS", registroFila.STATUS);
        fluidCreateVO.set("DHINC", registroFila.DHINC);
        fluidCreateVO.set("DHPROC", registroFila.DHPROC);
        fluidCreateVO.set("LOGEXEC", registroFila.LOGEXEC);
        fluidCreateVO.set("CODUSU", registroFila.CODUSU);
        fluidCreateVO.save();
    }


    public void salvaComControleTransacao(final RegistroFila registroFila) throws Exception {

        System.out.println("INICIANDO METODO salvaComControleTransacao REGISTRO = "+registroFila.toString());
        JapeSession.SessionHandle hnd = JapeSession.open();
        final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbc = dwfFacade.getJdbcWrapper();
        jdbc.openSession();


        hnd.execWithTX(new JapeSession.TXBlock() {
            public void doWithTx() throws Exception {
                salva(registroFila);
            }
        });
        JapeSession.close(hnd);
        JdbcWrapper.closeSession(jdbc);

    }

    public void incializaFila(String chave, String nomeProcessamento) throws Exception {

        System.out.println("GRAVANDO NA FILA COM A CHAVE " + chave + " NOME PROCESSAMENTO = " + nomeProcessamento);

        RegistroFila registroFila = new RegistroFila();
        DynamicVO tipoProcessamentoVO = JapeFactory
                .dao("MGSCT_Tipo_Processamento")
                .findOne("NOME = ?", nomeProcessamento);

        if (tipoProcessamentoVO == null) {

            System.out.println("Erro ao localizar tipo de processamento " + nomeProcessamento + ", favor entrar em contato com o setor de T.I.!");

            ErroUtils.disparaErro("Erro ao localizar tipo de processamento " + nomeProcessamento + ", favor entrar em contato com o setor de T.I.!");
        }

        BigDecimal numeroUnicoTipoProcessamento = tipoProcessamentoVO.asBigDecimal("NUTIPOPROC");

        registroFila.NUTIPOPROC = numeroUnicoTipoProcessamento;
        registroFila.CHAVE = chave;
        registroFila.STATUS = "I";
        registroFila.DHINC = TimeUtils.getNow();
        registroFila.DHPROC = null;
        registroFila.LOGEXEC = null;
        if (codigoUsuario == null) {
            registroFila.CODUSU = AuthenticationInfo.getCurrent().getUserID();
        } else {
            registroFila.CODUSU = codigoUsuario;
        }
        if(comControleTransacao){
            System.out.println(" CONTROLE DE TRANSACAO = " + registroFila.toString() );
            salvaComControleTransacao(registroFila);
        } else {
            salva(registroFila);
        }
    }


    private void atualizaFila(BigDecimal numeroUnico, String log, String status) throws Exception {
        try {

            NativeSqlDecorator filaEnvioConsulta = new NativeSqlDecorator(this, "atualizaFilaProcessamento.sql");
            filaEnvioConsulta.setParametro("NUFILAPROC", numeroUnico);
            filaEnvioConsulta.setParametro("LOGEXEC", log);
            filaEnvioConsulta.setParametro("STATUS", status);

            filaEnvioConsulta.atualizar();
        } catch (Exception e) {
            System.out.println("Atualização Fila Erro: " + e);
        }
    }

    public class RegistroFila {
        private BigDecimal NUFILAPROC;
        private BigDecimal NUTIPOPROC;
        private String CHAVE;
        private String STATUS;
        private Timestamp DHINC;
        private Timestamp DHPROC;
        private String LOGEXEC;
        private BigDecimal CODUSU;

        public BigDecimal getNUFILAPROC() {
            return NUFILAPROC;
        }

        public void setNUFILAPROC(BigDecimal NUFILAPROC) {
            this.NUFILAPROC = NUFILAPROC;
        }

        public BigDecimal getNUTIPOPROC() {
            return NUTIPOPROC;
        }

        public void setNUTIPOPROC(BigDecimal NUTIPOPROC) {
            this.NUTIPOPROC = NUTIPOPROC;
        }

        public String getCHAVE() {
            return CHAVE;
        }

        public void setCHAVE(String CHAVE) {
            this.CHAVE = CHAVE;
        }

        public String getSTATUS() {
            return STATUS;
        }

        public void setSTATUS(String STATUS) {
            this.STATUS = STATUS;
        }

        public Timestamp getDHINC() {
            return DHINC;
        }

        public void setDHINC(Timestamp DHINC) {
            this.DHINC = DHINC;
        }

        public Timestamp getDHPROC() {
            return DHPROC;
        }

        public void setDHPROC(Timestamp DHPROC) {
            this.DHPROC = DHPROC;
        }

        public String getLOGEXEC() {
            return LOGEXEC;
        }

        public void setLOGEXEC(String LOGEXEC) {
            this.LOGEXEC = LOGEXEC;
        }

        public BigDecimal getCODUSU() {
            return CODUSU;
        }

        public void setCODUSU(BigDecimal CODUSU) {
            this.CODUSU = CODUSU;
        }
    }
}
