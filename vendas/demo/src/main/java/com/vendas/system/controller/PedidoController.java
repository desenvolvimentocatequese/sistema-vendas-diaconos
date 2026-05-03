package com.vendas.system.controller;

import com.vendas.system.model.PedidoModel;
import com.vendas.system.model.StatusPedido;
import com.vendas.system.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.findAllByOrderByDataCriacaoDesc());
        return "pedidos/lista";
    }

    @GetMapping("/kanban")
    public String kanbanPedidos(Model model) {
        model.addAttribute("pedidosNovo", pedidoService.findByStatusOrderByDataCriacaoDesc(StatusPedido.NOVO));
        model.addAttribute("pedidosEmProducao", pedidoService.findByStatusOrderByDataCriacaoDesc(StatusPedido.EM_PRODUCAO));
        model.addAttribute("pedidosFinalizado", pedidoService.findByStatusOrderByDataCriacaoDesc(StatusPedido.FINALIZADO));
        return "pedidos/kanban";
    }

    @GetMapping("/novo")
    public String novoPedido(Model model) {
        model.addAttribute("pedido", new PedidoModel());
        return "pedidos/form";
    }

    @PostMapping
    public String salvarPedido(@ModelAttribute PedidoModel pedido) {
        pedidoService.save(pedido);
        return "redirect:/pedidos";
    }

    @GetMapping("/editar/{id}")
    public String editarPedido(@PathVariable Long id, Model model) {
        pedidoService.findById(id).ifPresent(pedido -> model.addAttribute("pedido", pedido));
        return "pedidos/form";
    }

    @GetMapping("/{id}")
    public String detalhePedido(@PathVariable Long id, Model model) {
        pedidoService.findById(id).ifPresent(pedido -> model.addAttribute("pedido", pedido));
        return "pedidos/detalhe";
    }

    @PostMapping("/mudar-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> mudarStatus(@RequestParam Long id, @RequestParam StatusPedido status) {
        var pedidoOptional = pedidoService.findById(id);
        if (pedidoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", "Pedido não encontrado"
            ));
        }
        PedidoModel pedido = pedidoOptional.get();
        pedido.atualizarStatus(status);
        pedidoService.save(pedido);
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "id", pedido.getId(),
                "status", pedido.getStatus().name()
        ));
    }

    @GetMapping("/deletar/{id}")
    public String deletarPedido(@PathVariable Long id) {
        pedidoService.deleteById(id);
        return "redirect:/pedidos";
    }
}