package com.vendas.system.model;

public enum TipoEntrega {
    ENTREGA("entrega"),
    RETIRADA("retirada");

    private final String tipo;

    TipoEntrega(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
