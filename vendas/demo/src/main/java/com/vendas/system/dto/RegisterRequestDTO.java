package com.vendas.system.dto;

import com.vendas.system.model.UsuarioRole;

public record RegisterRequestDTO(
        String nome,
        String email,
        String senha,
        String cpf,
        UsuarioRole role,
        String telefone
) {
}
