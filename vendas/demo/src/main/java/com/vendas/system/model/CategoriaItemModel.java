package com.vendas.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categorias_item")
@Data
@NoArgsConstructor
public class CategoriaItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório.")
    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(nullable = false)
    private Boolean ativo = true;
}
