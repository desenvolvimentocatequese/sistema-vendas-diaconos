package com.vendas.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cores")
@Data
@NoArgsConstructor
public class CorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório.")
    @Column(nullable = false, length = 60, unique = true)
    private String nome;

    @Column(nullable = false)
    private Boolean ativo = true;
}
