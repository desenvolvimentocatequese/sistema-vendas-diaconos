package com.vendas.system.model;

public enum StatusChamado {
    ABERTO("aberto"),
    EM_ANDAMENTO("em_andamento"),
    RESOLVIDO("resolvido"),
    FECHADO("fechado");

    private final String status;

    StatusChamado(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
