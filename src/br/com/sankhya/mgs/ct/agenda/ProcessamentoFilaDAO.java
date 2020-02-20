package br.com.sankhya.mgs.ct.agenda;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Entidade: MGSCT_Fila_Processamento
 * Tabela: MGSTCTFILAPROC
 * Chave: NUFILAPROC
 */
public class ProcessamentoFilaDAO {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Fila_Processamento");
    private DynamicVO vo;

    public void atualizaFilaErro(BigDecimal numeroUnico, String log) throws Exception {
        atualizaFila(numeroUnico, log, "E");
    }

    public void atualizaFilaProcessado(BigDecimal numeroUnico) throws Exception {
        atualizaFila(numeroUnico, "OK", "P");
    }


    public void buscaRegistroFilaProcessamento(BigDecimal numeroUnicoFilaProcessamento) throws Exception {
        this.vo = dao.findByPK(numeroUnicoFilaProcessamento);
    }

    private DynamicVO getVo() {
        return vo;
    }

    public RegistroFila getRegistroFila(BigDecimal numeroUnicoFilaProcessamento) throws Exception {
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


    private void atualizaFila(BigDecimal numeroUnico, String log, String status) throws Exception {
        try {

              NativeSqlDecorator filaEnvioConsulta = new NativeSqlDecorator(this, "atualizaFilaProcessamento.sql");
            filaEnvioConsulta.setParametro("NUFILAPROC", numeroUnico);
            filaEnvioConsulta.setParametro("LOGEXEC", log);
            filaEnvioConsulta.setParametro("STATUS", status);

            filaEnvioConsulta.atualziar();
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
