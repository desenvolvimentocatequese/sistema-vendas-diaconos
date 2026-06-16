package com.vendas.system.repository;

import com.vendas.system.model.ItemPadronizadoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemPadronizadoRepository extends JpaRepository<ItemPadronizadoModel, Long>,
        JpaSpecificationExecutor<ItemPadronizadoModel> {

    Optional<ItemPadronizadoModel> findByCodigo(String codigo);

    List<ItemPadronizadoModel> findByAtivoTrue();

    @Query("""
            SELECT i FROM ItemPadronizadoModel i
            WHERE (:busca IS NULL OR LOWER(i.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(i.codigo) LIKE LOWER(CONCAT('%', :busca, '%')))
              AND (:categoriaId IS NULL OR i.categoria.id = :categoriaId)
              AND (:ativo IS NULL OR i.ativo = :ativo)
            ORDER BY i.categoria.ordem, i.codigo
            """)
    List<ItemPadronizadoModel> findByFiltros(@Param("busca") String busca,
                                             @Param("categoriaId") Long categoriaId,
                                             @Param("ativo") Boolean ativo);
}
