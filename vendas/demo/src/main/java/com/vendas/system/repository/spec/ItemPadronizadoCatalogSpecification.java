package com.vendas.system.repository.spec;

import com.vendas.system.model.ItemPadronizadoModel;
import org.springframework.data.jpa.domain.Specification;

public final class ItemPadronizadoCatalogSpecification {

    private ItemPadronizadoCatalogSpecification() {
    }

    public static Specification<ItemPadronizadoModel> filtrar(String busca,
                                                                Long categoriaId,
                                                                Long corId,
                                                                Long tamanhoId) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }

            var predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.isTrue(root.get("ativo")));

            if (busca != null && !busca.isBlank()) {
                String termo = "%" + busca.trim().toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("nome")), termo),
                        cb.like(cb.lower(root.get("codigo")), termo)
                ));
            }
            if (categoriaId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("categoria").get("id"), categoriaId));
            }
            if (corId != null) {
                predicate = cb.and(predicate, cb.equal(
                        root.join("coresDisponiveis").get("id"), corId));
            }
            if (tamanhoId != null) {
                predicate = cb.and(predicate, cb.equal(
                        root.join("tamanhosDisponiveis").get("id"), tamanhoId));
            }
            return predicate;
        };
    }
}
