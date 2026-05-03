package com.vendas.system.dto;

import com.vendas.system.model.Tamanho;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarrinhoItemDTO {
    private Long produtoId;
    private String nomeProduto;
    private BigDecimal preco;
    private Tamanho tamanho;
    private int quantidade;
    private BigDecimal subtotal;
}