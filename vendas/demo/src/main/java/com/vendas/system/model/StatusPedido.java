package com.vendas.system.model;

public enum StatusPedido {
    NOVO("novo"),
    EM_PRODUCAO("em_producao"),
    FINALIZADO("finalizado");

    private final String status;

    StatusPedido(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
