package com.vendas.system.model;

public enum UsuarioRole {
    ADMIN("admin"),
    USER_COMUM("user_comum"),
    RESPONSAVEL_SETOR("responsavel_setor"),
    /** Comprador da loja (catálogo, carrinho, checkout) — sem acesso à gestão interna. */
    CLIENTE("cliente");

    private final String role;

    UsuarioRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
