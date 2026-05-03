package com.vendas.system.service;

import com.vendas.system.model.ItemPedidoModel;
import com.vendas.system.model.PedidoModel;
import com.vendas.system.model.StatusPedido;
import com.vendas.system.model.TipoEntrega;
import com.vendas.system.model.UsuarioModel;
import com.vendas.system.model.cart.CarrinhoItem;
import com.vendas.system.repository.ItemPedidoRepository;
import com.vendas.system.repository.PedidoRepository;
import com.vendas.system.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<PedidoModel> findAll() {
        return pedidoRepository.findAll();
    }

    public List<PedidoModel> findAllByOrderByDataCriacaoDesc() {
        return pedidoRepository.findAllByOrderByDataCriacaoDesc();
    }

    public List<PedidoModel> findByStatusOrderByDataCriacaoDesc(StatusPedido status) {
        return pedidoRepository.findByStatusOrderByDataCriacaoDesc(status);
    }

    public Optional<PedidoModel> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    public PedidoModel save(PedidoModel pedido) {
        return pedidoRepository.save(pedido);
    }

    public void deleteById(Long id) {
        pedidoRepository.deleteById(id);
    }

    public PedidoModel salvarPedidoComItens(UsuarioModel usuario, String tipoEntrega, String endereco, List<CarrinhoItem> itensCarrinho) {
        // Criar o pedido
        PedidoModel pedido = new PedidoModel();
        pedido.setUsuario(usuario);
        pedido.setStatus(StatusPedido.NOVO);
        pedido.setTipoEntrega(TipoEntrega.valueOf(tipoEntrega));
        pedido.setEnderecoEntrega(endereco);
        pedido.setDataCriacao(LocalDateTime.now());
        
        // Calcular total
        BigDecimal totalProdutos = itensCarrinho.stream()
                .map(CarrinhoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal taxaEntrega = tipoEntrega.equals("ENTREGA") ? new BigDecimal("10.00") : BigDecimal.ZERO;
        BigDecimal totalFinal = totalProdutos.add(taxaEntrega);
        
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setValorTotal(totalFinal);
        
        // Salvar pedido primeiro
        PedidoModel pedidoSalvo = pedidoRepository.save(pedido);
        
        // Criar e salvar itens do pedido
        List<ItemPedidoModel> itens = new ArrayList<>();
        for (CarrinhoItem itemDTO : itensCarrinho) {
            ItemPedidoModel item = new ItemPedidoModel();
            item.setPedido(pedidoSalvo);
            item.setProduto(produtoRepository.findById(itemDTO.getProdutoId()).orElse(null));
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(itemDTO.getPreco());
            item.setValorTotal(itemDTO.getSubtotal());
            item.setTamanho(itemDTO.getTamanho());
            item.setCor(itemDTO.getCor());
            itemPedidoRepository.save(item);
            itens.add(item);
        }
        
        pedidoSalvo.setItens(itens);
        return pedidoSalvo;
    }
}