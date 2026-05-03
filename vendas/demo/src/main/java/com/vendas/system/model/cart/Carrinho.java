package com.vendas.system.model.cart;

import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Carrinho implements Serializable {

    private final List<CarrinhoItem> itens = new ArrayList<>();

    public void adicionar(CarrinhoItem novoItem) {
        for (CarrinhoItem item : itens) {
            if (item.mesmaLinha(novoItem)) {
                item.setQuantidade(item.getQuantidade() + novoItem.getQuantidade());
                return;
            }
        }
        itens.add(novoItem);
    }

    public void remover(int indice) {
        if (indice >= 0 && indice < itens.size()) {
            itens.remove(indice);
        }
    }

    public BigDecimal total() {
        return itens.stream()
                .map(CarrinhoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int quantidadeTotalItens() {
        return itens.stream()
                .mapToInt(CarrinhoItem::getQuantidade)
                .sum();
    }

    public boolean estaVazio() {
        return itens.isEmpty();
    }
}
