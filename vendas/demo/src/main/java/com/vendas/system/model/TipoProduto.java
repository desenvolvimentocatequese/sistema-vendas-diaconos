package com.vendas.system.model;

public enum TipoProduto {
    CAMISA("camisa"),
    SAIA("saia"),
    VESTIDO("vestido"),
    CALCA("calca"),
    BLUSA("blusa"),
    OUTROS("outros");

    private final String tipo;

    TipoProduto(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
