package com.vendas.system.controller;

import com.vendas.system.model.SalaCosturaModel;
import com.vendas.system.service.SalaCosturaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/salas")
public class SalaCosturaController {

    private final SalaCosturaService service;

    public SalaCosturaController(SalaCosturaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("salas", service.findAll());
        return "salas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        SalaCosturaModel sala = new SalaCosturaModel();
        sala.setAtivo(true);
        model.addAttribute("sala", sala);
        return "salas/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("sala") SalaCosturaModel sala, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "salas/form";
        }
        service.save(sala);
        return "redirect:/salas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        service.findById(id).ifPresent(s -> model.addAttribute("sala", s));
        return "salas/form";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/salas";
    }
}
