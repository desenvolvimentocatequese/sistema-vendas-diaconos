package com.vendas.system.service;

import com.vendas.system.model.LocalTramite;
import com.vendas.system.model.ModoTransporte;
import com.vendas.system.model.MovimentacaoPedidoModel;
import com.vendas.system.model.PedidoModel;
import com.vendas.system.repository.MovimentacaoPedidoRepository;
import com.vendas.system.repository.PedidoRepository;
import com.vendas.system.repository.SalaCosturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MovimentacaoPedidoService {

    private final MovimentacaoPedidoRepository movimentacaoRepository;
    private final PedidoRepository pedidoRepository;
    private final SalaCosturaRepository salaRepository;

    public MovimentacaoPedidoService(MovimentacaoPedidoRepository movimentacaoRepository,
                                     PedidoRepository pedidoRepository,
                                     SalaCosturaRepository salaRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.pedidoRepository = pedidoRepository;
        this.salaRepository = salaRepository;
    }

    public List<MovimentacaoPedidoModel> listarPorPedido(Long pedidoId) {
        return movimentacaoRepository.findByPedidoIdOrderByDataSaidaAscIdAsc(pedidoId);
    }

    public BigDecimal custoTotalPorPedido(Long pedidoId) {
        BigDecimal total = movimentacaoRepository.somarCustoPorPedido(pedidoId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public MovimentacaoPedidoModel registrar(Long pedidoId, LocalTramite origem, LocalTramite destino,
                                             Long salaOrigemId, Long salaDestinoId, ModoTransporte modoTransporte,
                                             BigDecimal custo, LocalDate dataSaida, LocalDate dataChegada,
                                             String observacoes) {
        PedidoModel pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

        MovimentacaoPedidoModel movimentacao = new MovimentacaoPedidoModel();
        movimentacao.setPedido(pedido);
        movimentacao.setOrigem(origem);
        movimentacao.setDestino(destino);
        movimentacao.setModoTransporte(modoTransporte);
        movimentacao.setCusto(custo != null ? custo : BigDecimal.ZERO);
        movimentacao.setDataSaida(dataSaida);
        movimentacao.setDataChegada(dataChegada);
        movimentacao.setObservacoes(observacoes);

        if (origem == LocalTramite.SALA_COSTURA && salaOrigemId != null) {
            salaRepository.findById(salaOrigemId).ifPresent(movimentacao::setSalaOrigem);
        }
        if (destino == LocalTramite.SALA_COSTURA && salaDestinoId != null) {
            salaRepository.findById(salaDestinoId).ifPresent(movimentacao::setSalaDestino);
        }

        MovimentacaoPedidoModel salvo = movimentacaoRepository.save(movimentacao);

        pedido.setLocalAtual(destino);
        pedido.setDataAtualizacao(java.time.LocalDateTime.now());
        pedidoRepository.save(pedido);

        return salvo;
    }

    @Transactional
    public void excluir(Long movimentacaoId) {
        movimentacaoRepository.deleteById(movimentacaoId);
    }
}
