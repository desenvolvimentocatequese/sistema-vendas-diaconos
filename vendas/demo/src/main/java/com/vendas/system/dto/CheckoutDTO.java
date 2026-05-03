package com.vendas.system.dto;

import lombok.Data;

@Data
public class CheckoutDTO {
    private String tipoEntrega;
    private String cep;
    private String rua;
    private String numero;
    private String cidade;
}