package com.vendas.system.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuracao_sistema")
@Data
@NoArgsConstructor
public class ConfiguracaoSistemaModel {

    @Id
    private Long id = 1L;

    /**
     * Quando ativo: oculta preços e entrega/retirada; fluxo de venda vira solicitação.
     */
    @Column(nullable = false)
    private Boolean modoSolicitacao = false;
}
