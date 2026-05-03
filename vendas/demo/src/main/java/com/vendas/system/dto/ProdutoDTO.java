package com.vendas.system.dto;

import com.vendas.system.model.TipoProduto;
import java.math.BigDecimal;

public class ProdutoDTO {

    private Long id;
    private String nome;
    private String modelo;
    private TipoProduto tipoProduto;
    private BigDecimal preco;
    private String imagemUrl;
    private String cor;
    private String tamanho;
    private Boolean ativo;

    public ProdutoDTO() {
    }

    public ProdutoDTO(Long id, String nome, String modelo, TipoProduto tipoProduto,
                       BigDecimal preco, String imagemUrl, String cor, String tamanho, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.modelo = modelo;
        this.tipoProduto = tipoProduto;
        this.preco = preco;
        this.imagemUrl = imagemUrl;
        this.cor = cor;
        this.tamanho = tamanho;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}