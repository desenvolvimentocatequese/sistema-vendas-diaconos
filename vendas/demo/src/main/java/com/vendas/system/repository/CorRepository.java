package com.vendas.system.repository;

import com.vendas.system.model.CorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorRepository extends JpaRepository<CorModel, Long> {

    List<CorModel> findAllByAtivoTrueOrderByNomeAsc();

    List<CorModel> findAllByOrderByNomeAsc();
}
