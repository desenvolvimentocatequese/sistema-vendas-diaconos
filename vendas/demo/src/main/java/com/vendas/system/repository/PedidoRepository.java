package com.vendas.system.repository;

import com.vendas.system.model.LocalTramite;
import com.vendas.system.model.PedidoModel;
import com.vendas.system.model.StatusPedido;
import com.vendas.system.model.TipoEntrega;
import com.vendas.system.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoModel, Long> {

    List<PedidoModel> findByUsuario(UsuarioModel usuario);

    List<PedidoModel> findByStatus(StatusPedido status);

    List<PedidoModel> findByStatusOrderByDataCriacaoDesc(StatusPedido status);

    List<PedidoModel> findByTipoEntrega(TipoEntrega tipoEntrega);

    @Query("SELECT p FROM PedidoModel p WHERE p.dataCriacao BETWEEN :inicio AND :fim")
    List<PedidoModel> findByDataCriacaoBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT p FROM PedidoModel p WHERE p.prazoEntrega BETWEEN :inicio AND :fim")
    List<PedidoModel> findByPrazoEntregaBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    List<PedidoModel> findByUsuarioAndStatus(UsuarioModel usuario, StatusPedido status);

    @Query("SELECT COUNT(p) FROM PedidoModel p WHERE p.status = :status")
    Long countByStatus(@Param("status") StatusPedido status);

    List<PedidoModel> findAllByOrderByDataCriacaoDesc();

    List<PedidoModel> findByLocalAtualOrderByDataCriacaoDesc(LocalTramite localAtual);

    List<PedidoModel> findByLocalAtualIsNullOrderByDataCriacaoDesc();
}