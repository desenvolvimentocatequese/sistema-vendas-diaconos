package com.vendas.system.controller;

import com.vendas.system.model.ChamadoModel;
import com.vendas.system.service.ChamadoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @GetMapping
    public String listarChamados(Model model) {
        model.addAttribute("chamados", chamadoService.findAll());
        return "chamados/lista";
    }

    @GetMapping("/novo")
    public String novoChamado(Model model) {
        model.addAttribute("chamado", new ChamadoModel());
        return "chamados/form";
    }

    @PostMapping
    public String salvarChamado(@ModelAttribute ChamadoModel chamado) {
        chamadoService.save(chamado);
        return "redirect:/chamados";
    }

    @GetMapping("/editar/{id}")
    public String editarChamado(@PathVariable Long id, Model model) {
        chamadoService.findById(id).ifPresent(chamado -> model.addAttribute("chamado", chamado));
        return "chamados/form";
    }

    @GetMapping("/deletar/{id}")
    public String deletarChamado(@PathVariable Long id) {
        chamadoService.deleteById(id);
        return "redirect:/chamados";
    }
}