package com.vendas.system.repository;

import com.vendas.system.model.TamanhoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TamanhoRepository extends JpaRepository<TamanhoModel, Long> {

    List<TamanhoModel> findAllByAtivoTrueOrderByOrdemAscNomeAsc();

    List<TamanhoModel> findAllByOrderByOrdemAscNomeAsc();
}
