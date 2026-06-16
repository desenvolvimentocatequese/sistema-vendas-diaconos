package com.vendas.system.repository;

import com.vendas.system.model.CategoriaItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaItemRepository extends JpaRepository<CategoriaItemModel, Long> {

    List<CategoriaItemModel> findAllByAtivoTrueOrderByOrdemAscNomeAsc();

    List<CategoriaItemModel> findAllByOrderByOrdemAscNomeAsc();
}
