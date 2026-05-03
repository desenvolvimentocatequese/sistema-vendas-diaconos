package com.vendas.system.model;

import jakarta.persistence.*;


@Entity
@Table(name = "responsaveis_setor")

public class ResponsavelSetorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private UsuarioModel usuario;

    @Column(nullable = false, length = 100)
    private String setor;

    @Column(length = 255)
    private String descricaoSetor;

    @Column(nullable = false)
    private Boolean ativo = true;
}