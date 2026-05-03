package com.vendas.system.repository.spec;

import com.vendas.system.model.Cor;
import com.vendas.system.model.ProdutoModel;
import com.vendas.system.model.Tamanho;
import com.vendas.system.model.TipoProduto;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProdutoCatalogSpecification {

    private ProdutoCatalogSpecification() {
    }

    public static Specification<ProdutoModel> filtrar(String busca,
                                                      TipoProduto tipo,
                                                      Cor cor,
                                                      Tamanho tamanho,
                                                      BigDecimal precoMin,
                                                      BigDecimal precoMax) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }

            var predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.isTrue(root.get("ativo")));

            if (busca != null && !busca.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("nome")), "%" + busca.trim().toLowerCase() + "%"));
            }
            if (tipo != null) {
                predicate = cb.and(predicate, cb.equal(root.get("tipoProduto"), tipo));
            }
            if (cor != null) {
                predicate = cb.and(predicate, cb.isMember(cor, root.get("coresDisponiveis")));
            }
            if (tamanho != null) {
                predicate = cb.and(predicate, cb.isMember(tamanho, root.get("tamanhosDisponiveis")));
            }
            if (precoMin != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("preco"), precoMin));
            }
            if (precoMax != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("preco"), precoMax));
            }
            return predicate;
        };
    }
}
