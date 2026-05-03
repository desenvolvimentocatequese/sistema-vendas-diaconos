package com.vendas.system.dto;

public record AuthResponseDTO(
        String token,
        String type,
        Long expiresIn
) {
}
