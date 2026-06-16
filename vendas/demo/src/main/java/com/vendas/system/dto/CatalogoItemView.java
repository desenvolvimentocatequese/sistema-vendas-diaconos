package com.vendas.system.dto;

import com.vendas.system.model.CategoriaItemModel;
import com.vendas.system.model.CorModel;
import com.vendas.system.model.TamanhoModel;

import java.util.List;

/**
 * Representa um item do catálogo já filtrado pela disponibilidade em estoque,
 * expondo apenas as cores e tamanhos que possuem quantidade disponível.
 */
public record CatalogoItemView(
        Long id,
        String codigo,
        String nome,
        String imagemPath,
        CategoriaItemModel categoria,
        List<CorModel> cores,
        List<TamanhoModel> tamanhos
) {
}
