package com.vendas.system.model.cart;

import com.vendas.system.model.Cor;
import com.vendas.system.model.Tamanho;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarrinhoItem implements Serializable {

    private Long produtoId;
    private String nomeProduto;
    private String imagemPath;
    private BigDecimal preco;
    private Cor cor;
    private Tamanho tamanho;
    private int quantidade;

    public BigDecimal getSubtotal() {
        if (preco == null) {
            return BigDecimal.ZERO;
        }
        return preco.multiply(BigDecimal.valueOf(quantidade));
    }

    public boolean mesmaLinha(CarrinhoItem outro) {
        return outro != null
                && Objects.equals(produtoId, outro.produtoId)
                && tamanho == outro.tamanho
                && cor == outro.cor;
    }
}
