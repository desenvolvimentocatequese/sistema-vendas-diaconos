package com.vendas.system.controller;

import com.vendas.system.model.CorModel;
import com.vendas.system.service.CorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cores")
public class CorController {

    private final CorService service;

    public CorController(CorService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cores", service.findAll());
        return "cores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        CorModel cor = new CorModel();
        cor.setAtivo(true);
        model.addAttribute("cor", cor);
        return "cores/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("cor") CorModel cor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "cores/form";
        }
        service.save(cor);
        return "redirect:/cores";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        service.findById(id).ifPresent(c -> model.addAttribute("cor", c));
        return "cores/form";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/cores";
    }
}
