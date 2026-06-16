package com.vendas.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "itens_padronizados")
@Data
@NoArgsConstructor
public class ItemPadronizadoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Código é obrigatório.")
    @Column(nullable = false, length = 10, unique = true)
    private String codigo;

    @NotBlank(message = "Nome é obrigatório.")
    @Column(nullable = false, length = 150)
    private String nome;

    @NotNull(message = "Categoria é obrigatória.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaItemModel categoria;

    @Column(length = 255)
    private String imagemPath;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "item_padronizado_cores",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "cor_id")
    )
    private Set<CorModel> coresDisponiveis;

    @Size(min = 1, message = "Selecione ao menos um tamanho.")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "item_padronizado_tamanhos",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "tamanho_id")
    )
    private Set<TamanhoModel> tamanhosDisponiveis;

    @Column(nullable = false)
    private Boolean ativo = true;
}
