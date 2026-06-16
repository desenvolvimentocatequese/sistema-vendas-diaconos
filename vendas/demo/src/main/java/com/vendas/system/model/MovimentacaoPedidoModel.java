package com.vendas.system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_pedido")
@Getter
@Setter
@NoArgsConstructor
public class MovimentacaoPedidoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoModel pedido;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 32)
    private LocalTramite origem;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 32)
    private LocalTramite destino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_origem_id")
    private SalaCosturaModel salaOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_destino_id")
    private SalaCosturaModel salaDestino;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 32)
    private ModoTransporte modoTransporte;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal custo = BigDecimal.ZERO;

    @Column
    private LocalDate dataSaida;

    @Column
    private LocalDate dataChegada;

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false)
    private LocalDateTime dataRegistro = LocalDateTime.now();

    @Transient
    public String getOrigemDescricao() {
        if (origem == null) {
            return "";
        }
        if (origem.isSalaCostura() && salaOrigem != null) {
            return origem.getDescricao() + " - " + salaOrigem.getNome();
        }
        return origem.getDescricao();
    }

    @Transient
    public String getDestinoDescricao() {
        if (destino == null) {
            return "";
        }
        if (destino.isSalaCostura() && salaDestino != null) {
            return destino.getDescricao() + " - " + salaDestino.getNome();
        }
        return destino.getDescricao();
    }
}
