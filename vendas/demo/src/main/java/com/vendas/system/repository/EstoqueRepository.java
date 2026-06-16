package com.vendas.system.repository;

import com.vendas.system.model.EstoqueModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EstoqueRepository extends JpaRepository<EstoqueModel, Long> {

    Optional<EstoqueModel> findByItemIdAndCorIdAndTamanhoId(Long itemId, Long corId, Long tamanhoId);

    @Query("SELECT DISTINCT e.item.id FROM EstoqueModel e WHERE e.quantidade > 0")
    Set<Long> findItemIdsComEstoque();

    @Query("SELECT DISTINCT e.cor.id FROM EstoqueModel e WHERE e.item.id = :itemId AND e.cor.id IS NOT NULL AND e.quantidade > 0")
    Set<Long> findCorIdsComEstoque(@Param("itemId") Long itemId);

    @Query("SELECT DISTINCT e.tamanho.id FROM EstoqueModel e WHERE e.item.id = :itemId AND e.quantidade > 0")
    Set<Long> findTamanhoIdsComEstoque(@Param("itemId") Long itemId);

    @Query("""
            SELECT e FROM EstoqueModel e
            JOIN FETCH e.item i
            JOIN FETCH i.categoria
            LEFT JOIN FETCH e.cor
            JOIN FETCH e.tamanho
            WHERE (:itemId IS NULL OR i.id = :itemId)
              AND (:categoriaId IS NULL OR i.categoria.id = :categoriaId)
            ORDER BY i.categoria.ordem, i.codigo, e.tamanho.ordem
            """)
    List<EstoqueModel> findByFiltros(@Param("itemId") Long itemId,
                                     @Param("categoriaId") Long categoriaId);

    List<EstoqueModel> findByItemId(Long itemId);
}
