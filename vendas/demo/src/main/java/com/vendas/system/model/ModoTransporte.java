package com.vendas.system.model;

public enum ModoTransporte {
    SEDEX("Sedex"),
    TRANSPORTADORA("Transportadora"),
    RETIRADA("Retirada");

    private final String descricao;

    ModoTransporte(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
