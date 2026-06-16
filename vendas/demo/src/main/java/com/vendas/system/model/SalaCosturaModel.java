package com.vendas.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "salas_costura")
@Data
@NoArgsConstructor
public class SalaCosturaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório.")
    @Column(nullable = false, length = 120, unique = true)
    private String nome;

    @Column(length = 120)
    private String responsavel;

    @Column(length = 40)
    private String telefone;

    @Column(length = 255)
    private String endereco;

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false)
    private Boolean ativo = true;
}
