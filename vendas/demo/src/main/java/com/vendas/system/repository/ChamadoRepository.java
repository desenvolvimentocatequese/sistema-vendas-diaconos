package com.vendas.system.repository;

import com.vendas.system.model.ChamadoModel;
import com.vendas.system.model.StatusChamado;
import com.vendas.system.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChamadoRepository extends JpaRepository<ChamadoModel, Long> {

    List<ChamadoModel> findByUsuario(UsuarioModel usuario);

    List<ChamadoModel> findByResponsavel(UsuarioModel responsavel);

    List<ChamadoModel> findByStatus(StatusChamado status);

    List<ChamadoModel> findByPrioridade(String prioridade);

    @Query("SELECT c FROM ChamadoModel c WHERE c.dataCriacao BETWEEN :inicio AND :fim")
    List<ChamadoModel> findByDataCriacaoBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    List<ChamadoModel> findByUsuarioAndStatus(UsuarioModel usuario, StatusChamado status);

    List<ChamadoModel> findByResponsavelAndStatus(UsuarioModel responsavel, StatusChamado status);

    @Query("SELECT COUNT(c) FROM ChamadoModel c WHERE c.status = :status")
    Long countByStatus(@Param("status") StatusChamado status);
}