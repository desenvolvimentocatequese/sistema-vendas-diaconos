package com.vendas.system.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoModel pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_padronizado_id", nullable = false)
    private ItemPadronizadoModel item;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tamanho_id")
    private TamanhoModel tamanho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cor_id")
    private CorModel cor;
}
