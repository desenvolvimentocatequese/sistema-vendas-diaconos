package com.vendas.system.service;

import com.vendas.system.model.EstoqueModel;
import com.vendas.system.repository.EstoqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EstoqueService {

    private final EstoqueRepository repository;

    public EstoqueService(EstoqueRepository repository) {
        this.repository = repository;
    }

    public List<EstoqueModel> findByFiltros(Long itemId, Long categoriaId) {
        return repository.findByFiltros(itemId, categoriaId);
    }

    public Optional<EstoqueModel> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<EstoqueModel> findByItemCorTamanho(Long itemId, Long corId, Long tamanhoId) {
        return repository.findByItemIdAndCorIdAndTamanhoId(itemId, corId, tamanhoId);
    }

    public EstoqueModel save(EstoqueModel estoque) {
        return repository.save(estoque);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void ajustarQuantidade(Long itemId, Long corId, Long tamanhoId, int delta) {
        EstoqueModel estoque = buscarObrigatorio(itemId, corId, tamanhoId);
        int novaQtd = estoque.getQuantidade() + delta;
        if (novaQtd < 0) {
            throw new IllegalArgumentException("Quantidade em estoque insuficiente.");
        }
        estoque.setQuantidade(novaQtd);
        repository.save(estoque);
    }

    public void validarDisponibilidade(Long itemId, Long corId, Long tamanhoId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida para o pedido.");
        }
        EstoqueModel estoque = buscarObrigatorio(itemId, corId, tamanhoId);
        if (estoque.getQuantidade() < quantidade) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente para a combinação solicitada (disponível: " + estoque.getQuantidade() + ").");
        }
    }

    @Transactional
    public void debitar(Long itemId, Long corId, Long tamanhoId, int quantidade) {
        ajustarQuantidade(itemId, corId, tamanhoId, -quantidade);
    }

    private EstoqueModel buscarObrigatorio(Long itemId, Long corId, Long tamanhoId) {
        return repository.findByItemIdAndCorIdAndTamanhoId(itemId, corId, tamanhoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não há estoque cadastrado para a combinação de item, cor e tamanho solicitada."));
    }
}
