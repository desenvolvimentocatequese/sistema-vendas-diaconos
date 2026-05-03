package com.vendas.system.service;

import com.vendas.system.model.ItemPedidoModel;
import com.vendas.system.repository.ItemPedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoService(ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public List<ItemPedidoModel> findAll() {
        return itemPedidoRepository.findAll();
    }

    public Optional<ItemPedidoModel> findById(Long id) {
        return itemPedidoRepository.findById(id);
    }

    public ItemPedidoModel save(ItemPedidoModel itemPedido) {
        return itemPedidoRepository.save(itemPedido);
    }

    public void deleteById(Long id) {
        itemPedidoRepository.deleteById(id);
    }
}