package com.vendas.system.repository;

import com.vendas.system.model.ItemPedidoModel;
import com.vendas.system.model.PedidoModel;
import com.vendas.system.model.ProdutoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedidoModel, Long> {

    List<ItemPedidoModel> findByPedido(PedidoModel pedido);

    List<ItemPedidoModel> findByProduto(ProdutoModel produto);

    void deleteByPedido(PedidoModel pedido);
}