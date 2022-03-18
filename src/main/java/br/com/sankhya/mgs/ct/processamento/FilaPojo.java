package br.com.sankhya.mgs.ct.processamento;

import java.math.BigDecimal;

public class FilaPojo {
    BigDecimal NUFILAPROC;
    BigDecimal NUTIPOPROC;
    String CHAVE;
    String NOME;

    public FilaPojo(BigDecimal NUFILAPROC, BigDecimal NUTIPOPROC, String CHAVE, String NOME) {
        this.NUFILAPROC = NUFILAPROC;
        this.NUTIPOPROC = NUTIPOPROC;
        this.CHAVE = CHAVE;
        this.NOME = NOME;
    }

    public BigDecimal getNUFILAPROC() {
        return NUFILAPROC;
    }

    public BigDecimal getNUTIPOPROC() {
        return NUTIPOPROC;
    }

    public String getCHAVE() {
        return CHAVE;
    }

    public String getNOME() {
        return NOME;
    }
}
