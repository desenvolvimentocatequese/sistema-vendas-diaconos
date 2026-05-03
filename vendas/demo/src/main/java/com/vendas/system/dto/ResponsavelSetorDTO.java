package com.vendas.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelSetorDTO {

    private Long id;
    private UsuarioDTO usuario;
    private String setor;
    private String descricaoSetor;
    private Boolean ativo;
}