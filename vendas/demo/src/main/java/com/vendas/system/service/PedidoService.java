package com.vendas.system.service;

import com.vendas.system.model.*;
import com.vendas.system.model.cart.CarrinhoItem;
import com.vendas.system.repository.CorRepository;
import com.vendas.system.repository.ItemPedidoRepository;
import com.vendas.system.repository.ItemPadronizadoRepository;
import com.vendas.system.repository.PedidoRepository;
import com.vendas.system.repository.TamanhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ItemPadronizadoRepository itemRepository;
    private final CorRepository corRepository;
    private final TamanhoRepository tamanhoRepository;
    private final ConfiguracaoSistemaService configuracaoService;
    private final EstoqueService estoqueService;

    public PedidoService(PedidoRepository pedidoRepository,
                         ItemPedidoRepository itemPedidoRepository,
                         ItemPadronizadoRepository itemRepository,
                         CorRepository corRepository,
                         TamanhoRepository tamanhoRepository,
                         ConfiguracaoSistemaService configuracaoService,
                         EstoqueService estoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.itemRepository = itemRepository;
        this.corRepository = corRepository;
        this.tamanhoRepository = tamanhoRepository;
        this.configuracaoService = configuracaoService;
        this.estoqueService = estoqueService;
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

    public List<PedidoModel> findByLocalAtual(LocalTramite localAtual) {
        return pedidoRepository.findByLocalAtualOrderByDataCriacaoDesc(localAtual);
    }

    public List<PedidoModel> findSemTramite() {
        return pedidoRepository.findByLocalAtualIsNullOrderByDataCriacaoDesc();
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

    @Transactional
    public PedidoModel salvarPedidoComItens(UsuarioModel usuario, String tipoEntrega, String endereco,
                                            List<CarrinhoItem> itensCarrinho) {
        for (CarrinhoItem itemCarrinho : itensCarrinho) {
            estoqueService.validarDisponibilidade(
                    itemCarrinho.getItemId(),
                    itemCarrinho.getCorId(),
                    itemCarrinho.getTamanhoId(),
                    itemCarrinho.getQuantidade()
            );
        }

        boolean modoSolicitacao = configuracaoService.isModoSolicitacao();

        PedidoModel pedido = new PedidoModel();
        pedido.setUsuario(usuario);
        pedido.setStatus(StatusPedido.NOVO);
        pedido.setDataCriacao(LocalDateTime.now());

        if (modoSolicitacao) {
            pedido.setTipoEntrega(TipoEntrega.SOLICITACAO);
            pedido.setTaxaEntrega(BigDecimal.ZERO);
        } else if (tipoEntrega != null && !tipoEntrega.isBlank()) {
            pedido.setTipoEntrega(TipoEntrega.valueOf(tipoEntrega));
            pedido.setEnderecoEntrega(endereco);
            BigDecimal taxaEntrega = "ENTREGA".equals(tipoEntrega) ? new BigDecimal("10.00") : BigDecimal.ZERO;
            pedido.setTaxaEntrega(taxaEntrega);
        } else {
            pedido.setTipoEntrega(TipoEntrega.RETIRADA);
            pedido.setTaxaEntrega(BigDecimal.ZERO);
        }

        BigDecimal totalProdutos = itensCarrinho.stream()
                .map(CarrinhoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setValorTotal(modoSolicitacao ? BigDecimal.ZERO : totalProdutos.add(pedido.getTaxaEntrega()));

        PedidoModel pedidoSalvo = pedidoRepository.save(pedido);

        List<ItemPedidoModel> itens = new ArrayList<>();
        for (CarrinhoItem itemDTO : itensCarrinho) {
            ItemPedidoModel item = new ItemPedidoModel();
            item.setPedido(pedidoSalvo);
            item.setItem(itemRepository.findById(itemDTO.getItemId()).orElse(null));
            item.setQuantidade(itemDTO.getQuantidade());

            if (!modoSolicitacao) {
                item.setPrecoUnitario(itemDTO.getPreco());
                item.setValorTotal(itemDTO.getSubtotal());
            }

            if (itemDTO.getTamanhoId() != null) {
                tamanhoRepository.findById(itemDTO.getTamanhoId()).ifPresent(item::setTamanho);
            }
            if (itemDTO.getCorId() != null) {
                corRepository.findById(itemDTO.getCorId()).ifPresent(item::setCor);
            }

            itemPedidoRepository.save(item);
            itens.add(item);
        }

        for (CarrinhoItem itemCarrinho : itensCarrinho) {
            estoqueService.debitar(
                    itemCarrinho.getItemId(),
                    itemCarrinho.getCorId(),
                    itemCarrinho.getTamanhoId(),
                    itemCarrinho.getQuantidade()
            );
        }

        pedidoSalvo.setItens(itens);
        return pedidoSalvo;
    }
}
