package com.vendas.system.model.cart;

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

    private Long itemId;
    private String codigoItem;
    private String nomeItem;
    private String imagemPath;
    private BigDecimal preco;
    private Long corId;
    private String corNome;
    private Long tamanhoId;
    private String tamanhoNome;
    private int quantidade;

    public BigDecimal getSubtotal() {
        if (preco == null) {
            return BigDecimal.ZERO;
        }
        return preco.multiply(BigDecimal.valueOf(quantidade));
    }

    public boolean mesmaLinha(CarrinhoItem outro) {
        return outro != null
                && Objects.equals(itemId, outro.itemId)
                && Objects.equals(tamanhoId, outro.tamanhoId)
                && Objects.equals(corId, outro.corId);
    }
}
