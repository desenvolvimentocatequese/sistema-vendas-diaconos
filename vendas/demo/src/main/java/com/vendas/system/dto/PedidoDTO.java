package com.vendas.system.dto;

import com.vendas.system.model.StatusPedido;
import com.vendas.system.model.TipoEntrega;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {

    private Long id;
    private UsuarioDTO usuario;
    private StatusPedido status;
    private TipoEntrega tipoEntrega;
    private String enderecoEntrega;
    private BigDecimal taxaEntrega;
    private LocalDateTime prazoEntrega;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private List<ItemPedidoDTO> itens;
    private BigDecimal valorTotal;
    private String observacoes;

    public PedidoDTO() {
    }

    public PedidoDTO(Long id, UsuarioDTO usuario, StatusPedido status, TipoEntrega tipoEntrega,
                     String enderecoEntrega, BigDecimal taxaEntrega, LocalDateTime prazoEntrega,
                     LocalDateTime dataCriacao, LocalDateTime dataAtualizacao, List<ItemPedidoDTO> itens,
                     BigDecimal valorTotal, String observacoes) {
        this.id = id;
        this.usuario = usuario;
        this.status = status;
        this.tipoEntrega = tipoEntrega;
        this.enderecoEntrega = enderecoEntrega;
        this.taxaEntrega = taxaEntrega;
        this.prazoEntrega = prazoEntrega;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
        this.itens = itens;
        this.valorTotal = valorTotal;
        this.observacoes = observacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public TipoEntrega getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(TipoEntrega tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public BigDecimal getTaxaEntrega() {
        return taxaEntrega;
    }

    public void setTaxaEntrega(BigDecimal taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
    }

    public LocalDateTime getPrazoEntrega() {
        return prazoEntrega;
    }

    public void setPrazoEntrega(LocalDateTime prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}