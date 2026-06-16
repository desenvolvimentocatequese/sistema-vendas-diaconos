package com.vendas.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estoque", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"item_id", "cor_id", "tamanho_id"})
})
@Data
@NoArgsConstructor
public class EstoqueModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Item é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemPadronizadoModel item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cor_id")
    private CorModel cor;

    @NotNull(message = "Tamanho é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tamanho_id", nullable = false)
    private TamanhoModel tamanho;

    @NotNull(message = "Quantidade é obrigatória.")
    @Min(value = 0, message = "Quantidade não pode ser negativa.")
    @Column(nullable = false)
    private Integer quantidade = 0;
}
