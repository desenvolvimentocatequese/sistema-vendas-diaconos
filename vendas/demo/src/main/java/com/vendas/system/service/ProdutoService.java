package com.vendas.system.service;

import com.vendas.system.model.Cor;
import com.vendas.system.model.ProdutoModel;
import com.vendas.system.model.Tamanho;
import com.vendas.system.model.TipoProduto;
import com.vendas.system.repository.ProdutoRepository;
import com.vendas.system.repository.spec.ProdutoCatalogSpecification;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<ProdutoModel> findAll() {
        return produtoRepository.findAll();
    }

    public List<ProdutoModel> findByFiltros(String nome, TipoProduto tipoProduto, Boolean ativo) {
        return produtoRepository.findByFiltros(nome, tipoProduto, ativo);
    }

    public List<ProdutoModel> buscarCatalogo(String busca,
                                             TipoProduto tipo,
                                             Cor cor,
                                             Tamanho tamanho,
                                             BigDecimal precoMin,
                                             BigDecimal precoMax,
                                             String ordenacao) {
        Sort sort = Sort.unsorted();
        if ("precoAsc".equalsIgnoreCase(ordenacao)) {
            sort = Sort.by(Sort.Direction.ASC, "preco");
        } else if ("precoDesc".equalsIgnoreCase(ordenacao)) {
            sort = Sort.by(Sort.Direction.DESC, "preco");
        }

        return produtoRepository.findAll(
                ProdutoCatalogSpecification.filtrar(busca, tipo, cor, tamanho, precoMin, precoMax),
                sort
        );
    }

    public Optional<ProdutoModel> findById(Long id) {
        return produtoRepository.findById(id);
    }

    public ProdutoModel save(ProdutoModel produto) {
        return produtoRepository.save(produto);
    }

    public void deleteById(Long id) {
        produtoRepository.deleteById(id);
    }
}