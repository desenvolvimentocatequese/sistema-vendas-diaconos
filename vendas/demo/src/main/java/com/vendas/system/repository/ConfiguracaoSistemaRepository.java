package com.vendas.system.repository;

import com.vendas.system.model.ConfiguracaoSistemaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoSistemaRepository extends JpaRepository<ConfiguracaoSistemaModel, Long> {
}
