package com.vendas.system.dto;

import jakarta.validation.constraints.NotBlank;

public record ClienteRegisterDTO(
        @NotBlank String nome,
        @NotBlank String email,
        @NotBlank String senha,
        @NotBlank String cpf,
        @NotBlank String telefone
) {
}
