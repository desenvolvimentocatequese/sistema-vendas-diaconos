package com.vendas.system.dto;

import com.vendas.system.model.StatusChamado;
import java.time.LocalDateTime;

public class ChamadoDTO {

    private Long id;
    private UsuarioDTO usuario;
    private String titulo;
    private String descricao;
    private StatusChamado status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataResolucao;
    private UsuarioDTO responsavel;
    private String resposta;
    private String prioridade;
}