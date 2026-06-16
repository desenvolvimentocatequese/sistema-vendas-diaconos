package com.vendas.system.controller;

import com.vendas.system.model.LocalTramite;
import com.vendas.system.model.ModoTransporte;
import com.vendas.system.service.MovimentacaoPedidoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/pedidos/{pedidoId}/movimentacoes")
public class MovimentacaoPedidoController {

    private final MovimentacaoPedidoService service;

    public MovimentacaoPedidoController(MovimentacaoPedidoService service) {
        this.service = service;
    }

    @PostMapping
    public String registrar(@PathVariable Long pedidoId,
                            @RequestParam LocalTramite origem,
                            @RequestParam LocalTramite destino,
                            @RequestParam(required = false) Long salaOrigemId,
                            @RequestParam(required = false) Long salaDestinoId,
                            @RequestParam ModoTransporte modoTransporte,
                            @RequestParam(required = false) BigDecimal custo,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataSaida,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataChegada,
                            @RequestParam(required = false) String observacoes) {
        service.registrar(pedidoId, origem, destino, salaOrigemId, salaDestinoId,
                modoTransporte, custo, dataSaida, dataChegada, observacoes);
        return "redirect:/pedidos/" + pedidoId;
    }

    @GetMapping("/deletar/{movimentacaoId}")
    public String excluir(@PathVariable Long pedidoId, @PathVariable Long movimentacaoId) {
        service.excluir(movimentacaoId);
        return "redirect:/pedidos/" + pedidoId;
    }
}
