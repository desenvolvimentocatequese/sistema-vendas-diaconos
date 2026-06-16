package com.vendas.system.repository;

import com.vendas.system.model.SalaCosturaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaCosturaRepository extends JpaRepository<SalaCosturaModel, Long> {

    List<SalaCosturaModel> findAllByOrderByNomeAsc();

    List<SalaCosturaModel> findAllByAtivoTrueOrderByNomeAsc();
}
