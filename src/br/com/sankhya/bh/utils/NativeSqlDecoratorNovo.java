package br.com.sankhya.bh.utils;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;

/*
 * Desenvolvido por Fernando Lopes Sankhya Unidade BH
 * Versao 1.5
 * */
public class NativeSqlDecoratorNovo {
    private NativeSql nativeSql;
    private String sql;
    private boolean aberto = false;
    ResultSet resultSet;
    private JdbcWrapper jdbcWrapper;

    private NativeSqlDecoratorNovo() {
    }

    public NativeSqlDecoratorNovo(String sql, JdbcWrapper jdbcWrapper) {
        this.iniciar();
        this.jdbcWrapper = jdbcWrapper;
        this.nativeSql.appendSql(sql);
    }

    public boolean proximo() throws Exception {
        if (!this.aberto) {
            this.executar();
            this.aberto = true;
        }

        return this.resultSet.next();
    }

    public boolean loop() throws Exception {
        return this.proximo();
    }

    public NativeSqlDecoratorNovo(Object objetobase, String arquivo) throws Exception {
        this.iniciar();
        this.nativeSql.loadSql(objetobase.getClass(), arquivo);
    }

    public NativeSqlDecoratorNovo setParametro(String nome, Object valor) {
        this.nativeSql.setNamedParameter(nome, valor);
        return this;
    }

    public BigDecimal getValorBigDecimal(String campo) throws Exception {
        return this.resultSet.getBigDecimal(campo);
    }

    public String getValorString(String campo) throws Exception {
        return this.resultSet.getString(campo);
    }

    private Boolean getValorBoolean(String campo) throws Exception {
        return this.resultSet.getBoolean(campo);
    }

    public Timestamp getValorTimestamp(String campo) throws Exception {
        return this.resultSet.getTimestamp(campo);
    }

    public int getValorInt(String campo) throws Exception {
        return this.resultSet.getInt(campo);
    }

    private float getValorFloat(String campo) throws Exception {
        return this.resultSet.getFloat(campo);
    }

    private void iniciar() {
        this.jdbcWrapper = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
        this.nativeSql = new NativeSql(this.jdbcWrapper);
    }

    public void executar() throws Exception {
        this.resultSet = this.nativeSql.executeQuery();
        if (this.resultSet != null) {
            this.aberto = true;
        }

    }

    public void atualizar() throws Exception {
        this.nativeSql.executeUpdate();
    }

    private String getSqlResource(Object objetobase, String arquivo) throws Exception {
        InputStream in = objetobase.getClass().getResourceAsStream(arquivo);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuffer buf = new StringBuffer(512);
        String line = null;

        while((line = reader.readLine()) != null) {
            buf.append(line);
            buf.append('\n');
        }

        return buf.toString();
    }

    public void close() throws Exception {
        if(this.nativeSql != null){
            NativeSql.releaseResources(this.nativeSql);
            this.resultSet.close();
        }
        this.jdbcWrapper.closeSession();
    }
}
