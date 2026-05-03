package com.vendas.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório.")
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 50)
    private String modelo;

    @NotNull(message = "Tipo é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProduto tipoProduto;

    @NotNull(message = "Preço é obrigatório.")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(length = 255)
    private String imagemPath;

    @ElementCollection
    @CollectionTable(name = "produto_cores", joinColumns = @JoinColumn(name = "produto_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "cor")
    private Set<Cor> coresDisponiveis;

    @Size(min = 1, message = "Selecione ao menos um tamanho.")
    @ElementCollection
    @CollectionTable(name = "produto_tamanhos", joinColumns = @JoinColumn(name = "produto_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tamanho")
    private Set<Tamanho> tamanhosDisponiveis;

    @Column(nullable = false)
    private Boolean ativo = true;
}