package com.vendas.system.model;

public enum LocalTramite {
    BRAS("Brás (Fábrica de Tecidos)"),
    CENTRO_DISTRIBUICAO("Centro de Distribuição"),
    SALA_COSTURA("Sala de Costura"),
    SOLICITANTE("Solicitante");

    private final String descricao;

    LocalTramite(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isSalaCostura() {
        return this == SALA_COSTURA;
    }
}
