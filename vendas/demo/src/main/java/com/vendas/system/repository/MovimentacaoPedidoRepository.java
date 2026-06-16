package com.vendas.system.repository;

import com.vendas.system.model.MovimentacaoPedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MovimentacaoPedidoRepository extends JpaRepository<MovimentacaoPedidoModel, Long> {

    List<MovimentacaoPedidoModel> findByPedidoIdOrderByDataSaidaAscIdAsc(Long pedidoId);

    @Query("SELECT COALESCE(SUM(m.custo), 0) FROM MovimentacaoPedidoModel m WHERE m.pedido.id = :pedidoId")
    BigDecimal somarCustoPorPedido(@Param("pedidoId") Long pedidoId);
}
