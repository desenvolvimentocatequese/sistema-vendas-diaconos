package com.vendas.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tamanhos")
@Data
@NoArgsConstructor
public class TamanhoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório.")
    @Column(nullable = false, length = 20, unique = true)
    private String nome;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(nullable = false)
    private Boolean ativo = true;
}
