package com.vendas.system.repository;

import com.vendas.system.model.ProdutoModel;
import com.vendas.system.model.TipoProduto;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoModel, Long>, JpaSpecificationExecutor<ProdutoModel> {

    List<ProdutoModel> findByAtivoTrue();

    List<ProdutoModel> findByTipoProduto(TipoProduto tipoProduto);

    List<ProdutoModel> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT p FROM ProdutoModel p WHERE p.preco BETWEEN :minPreco AND :maxPreco")
    List<ProdutoModel> findByPrecoRange(@Param("minPreco") BigDecimal minPreco, @Param("maxPreco") BigDecimal maxPreco);

    @Query("""
            SELECT p FROM ProdutoModel p
            WHERE (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
              AND (:tipoProduto IS NULL OR p.tipoProduto = :tipoProduto)
              AND (:ativo IS NULL OR p.ativo = :ativo)
            """)
    List<ProdutoModel> findByFiltros(@Param("nome") String nome,
                                     @Param("tipoProduto") TipoProduto tipoProduto,
                                     @Param("ativo") Boolean ativo);
}