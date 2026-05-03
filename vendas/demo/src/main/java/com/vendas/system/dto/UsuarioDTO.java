package com.vendas.system.dto;

import com.vendas.system.model.UsuarioRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String congregacao;
    private String regional;
    private String codigoRegional;
    private String endereco;
    private UsuarioRole role;
    private LocalDateTime dataCriacao;
    private Boolean ativo;
}