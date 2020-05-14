package br.com.sankhya.bh.utils;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/*
 * Desenvolvido por Fernando Lopes Sankhya Unidade BH
 * Versao 1.0
 * suporte a xls, xlsx
 * */
public class LerArquivoDeDadosDecorator {
    private String arquivo;
    private String extensao;
    private int numeroDeLinhas = 0;
    private int linhaSelecionada = 0;
    private boolean aberto = false;
    private Map<String, Integer> mapaDeColunas = new HashMap<String, Integer>();

    private HSSFSheet planilhaXsl;
    private XSSFSheet planilhaXslx;

    private LerArquivoDeDadosDecorator() {
    }

    public LerArquivoDeDadosDecorator(String arquivo) {
        this.arquivo = arquivo;
        this.extensao = arquivo.substring(arquivo.lastIndexOf(".") + 1, arquivo.length());
    }

    public LerArquivoDeDadosDecorator(String caminhaAquivo, String extensao) {
        this.arquivo = caminhaAquivo;
        this.extensao = extensao;
    }

    public void setColuna(String nomeColuna, int posicaoColuna) {
        mapaDeColunas.put(nomeColuna, posicaoColuna);
    }

    public void setColuna(String nomeColuna, String coluna) {
        char caracter = coluna.toUpperCase().charAt(0);
        int codigo = (int) caracter;
        int PosicaoColuna = codigo - 65;

        mapaDeColunas.put(nomeColuna, PosicaoColuna);
    }

    public boolean proximo() throws Exception {
        if (!aberto) {
            abrirArquivo();
            aberto = true;
        }
        linhaSelecionada++;
        return linhaSelecionada < numeroDeLinhas;
    }

    private void abrirArquivo() throws Exception {
        switch (extensao.toLowerCase()) {
            case "xls":
                abrirArquivoXls();
                break;
            case "xslx":
                abrirArquivoXslx();
                break;
        }
    }

    private void abrirArquivoXls() throws Exception {
        FileInputStream file = new FileInputStream(new File(arquivo));
        //abre pasta de trabalho
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(file);
        //abre planilha
        planilhaXsl = hssfWorkbook.getSheetAt(0);

        numeroDeLinhas = planilhaXsl.getPhysicalNumberOfRows();
    }

    private void abrirArquivoXslx() throws Exception {
        FileInputStream file = new FileInputStream(new File(arquivo));
        //abre pasta de trabalho
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
        //abre planilha
        planilhaXslx = xssfWorkbook.getSheetAt(0);

        numeroDeLinhas = planilhaXslx.getPhysicalNumberOfRows();
    }


    public BigDecimal getValorBigDecimal(String campo) throws Exception {
        BigDecimal valor = null;

        switch (extensao.toLowerCase()) {
            case "xls":
                valor = new BigDecimal(planilhaXsl.getRow(linhaSelecionada).getCell(mapaDeColunas.get(campo)).getNumericCellValue());
                break;
            case "xslx":
                valor = new BigDecimal(planilhaXslx.getRow(linhaSelecionada).getCell(mapaDeColunas.get(campo)).getNumericCellValue());
                break;
        }

        return valor;
    }

    public String getValorString(String campo) throws Exception {
        String valor = null;

        switch (extensao.toLowerCase()) {
            case "xls":
                valor = planilhaXsl.getRow(linhaSelecionada).getCell(mapaDeColunas.get(campo)).getStringCellValue();
                break;
            case "xslx":
                valor = planilhaXslx.getRow(linhaSelecionada).getCell(mapaDeColunas.get(campo)).getStringCellValue();
                break;
        }

        return valor;
    }

    public Timestamp getValorTimestamp(String campo) throws Exception {
        Timestamp valor = null;

        switch (extensao.toLowerCase()) {
            case "xls":
                valor = new Timestamp(planilhaXsl.getRow(linhaSelecionada).getCell(mapaDeColunas.get(campo)).getDateCellValue().getTime());
                break;
            case "xslx":
                valor = new Timestamp(planilhaXslx.getRow(linhaSelecionada).getCell(mapaDeColunas.get(campo)).getDateCellValue().getTime());
                break;
        }

        return valor;
    }
}
