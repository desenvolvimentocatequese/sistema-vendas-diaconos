package com.vendas.system.model;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class PedidoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 32)
    private StatusPedido status = StatusPedido.NOVO;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 32)
    private TipoEntrega tipoEntrega;

    @Column(length = 255)
    private String enderecoEntrega;

    @Column(precision = 10, scale = 2)
    private BigDecimal taxaEntrega = BigDecimal.ZERO;

    @Column
    private LocalDateTime prazoEntrega;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemPedidoModel> itens;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("dataSaida ASC, id ASC")
    private List<MovimentacaoPedidoModel> movimentacoes;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 32)
    private LocalTramite localAtual;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(length = 500)
    private String observacoes;

    public PedidoModel() {
    }

    public PedidoModel(Long id, UsuarioModel usuario, StatusPedido status, TipoEntrega tipoEntrega,
                       String enderecoEntrega, BigDecimal taxaEntrega, LocalDateTime prazoEntrega,
                       LocalDateTime dataCriacao, LocalDateTime dataAtualizacao, List<ItemPedidoModel> itens,
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

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioModel usuario) {
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


    public List<ItemPedidoModel> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoModel> itens) {
        this.itens = itens;
    }

    public List<MovimentacaoPedidoModel> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<MovimentacaoPedidoModel> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    public LocalTramite getLocalAtual() {
        return localAtual;
    }

    public void setLocalAtual(LocalTramite localAtual) {
        this.localAtual = localAtual;
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

    public void calcularValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (itens != null) {
            for (ItemPedidoModel item : itens) {
                total = total.add(item.getValorTotal());
            }
        }
        this.valorTotal = total.add(this.taxaEntrega);
    }

    public void atualizarStatus(StatusPedido novoStatus) {
        this.status = novoStatus;
        this.dataAtualizacao = LocalDateTime.now();
    }



}