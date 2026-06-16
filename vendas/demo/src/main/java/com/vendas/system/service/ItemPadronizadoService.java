package com.vendas.system.service;

import com.vendas.system.dto.CatalogoItemView;
import com.vendas.system.model.CorModel;
import com.vendas.system.model.ItemPadronizadoModel;
import com.vendas.system.model.TamanhoModel;
import com.vendas.system.repository.EstoqueRepository;
import com.vendas.system.repository.ItemPadronizadoRepository;
import com.vendas.system.repository.spec.ItemPadronizadoCatalogSpecification;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ItemPadronizadoService {

    private final ItemPadronizadoRepository repository;
    private final EstoqueRepository estoqueRepository;

    public ItemPadronizadoService(ItemPadronizadoRepository repository,
                                  EstoqueRepository estoqueRepository) {
        this.repository = repository;
        this.estoqueRepository = estoqueRepository;
    }

    public List<ItemPadronizadoModel> findAll() {
        return repository.findAll(Sort.by("categoria.ordem", "codigo"));
    }

    public List<ItemPadronizadoModel> findByFiltros(String busca, Long categoriaId, Boolean ativo) {
        return repository.findByFiltros(busca, categoriaId, ativo);
    }

    @Transactional(readOnly = true)
    public List<CatalogoItemView> buscarCatalogo(String busca, Long categoriaId, Long corId, Long tamanhoId) {
        Set<Long> itensComEstoque = estoqueRepository.findItemIdsComEstoque();

        List<ItemPadronizadoModel> itens = repository.findAll(
                ItemPadronizadoCatalogSpecification.filtrar(busca, categoriaId, corId, tamanhoId)
        );
        itens.sort(Comparator
                .comparing((ItemPadronizadoModel i) -> i.getCategoria() != null && i.getCategoria().getOrdem() != null
                        ? i.getCategoria().getOrdem() : Integer.MAX_VALUE)
                .thenComparing(i -> i.getCodigo() != null ? i.getCodigo() : ""));

        return itens.stream()
                .filter(item -> itensComEstoque.contains(item.getId()))
                .map(item -> montarView(item, corId, tamanhoId))
                .filter(view -> !view.tamanhos().isEmpty())
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<CatalogoItemView> buscarDetalhe(Long id) {
        Set<Long> itensComEstoque = estoqueRepository.findItemIdsComEstoque();
        return repository.findById(id)
                .filter(item -> itensComEstoque.contains(item.getId()))
                .map(item -> montarView(item, null, null))
                .filter(view -> !view.tamanhos().isEmpty());
    }

    private CatalogoItemView montarView(ItemPadronizadoModel item, Long corFiltro, Long tamanhoFiltro) {
        Set<Long> coresComEstoque = estoqueRepository.findCorIdsComEstoque(item.getId());
        Set<Long> tamanhosComEstoque = estoqueRepository.findTamanhoIdsComEstoque(item.getId());

        List<CorModel> cores = item.getCoresDisponiveis() == null ? List.of() :
                item.getCoresDisponiveis().stream()
                        .filter(cor -> coresComEstoque.contains(cor.getId()))
                        .filter(cor -> corFiltro == null || corFiltro.equals(cor.getId()))
                        .sorted(Comparator.comparing(c -> c.getNome() != null ? c.getNome() : ""))
                        .toList();

        List<TamanhoModel> tamanhos = item.getTamanhosDisponiveis() == null ? List.of() :
                item.getTamanhosDisponiveis().stream()
                        .filter(tam -> tamanhosComEstoque.contains(tam.getId()))
                        .filter(tam -> tamanhoFiltro == null || tamanhoFiltro.equals(tam.getId()))
                        .sorted(Comparator.comparing(t -> t.getOrdem() != null ? t.getOrdem() : Integer.MAX_VALUE))
                        .toList();

        return new CatalogoItemView(
                item.getId(),
                item.getCodigo(),
                item.getNome(),
                item.getImagemPath(),
                item.getCategoria(),
                cores,
                tamanhos
        );
    }

    public Optional<ItemPadronizadoModel> findById(Long id) {
        return repository.findById(id);
    }

    public ItemPadronizadoModel save(ItemPadronizadoModel item) {
        return repository.save(item);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
