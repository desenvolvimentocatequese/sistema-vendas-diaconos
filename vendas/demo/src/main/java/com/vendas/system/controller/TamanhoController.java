package com.vendas.system.controller;

import com.vendas.system.model.TamanhoModel;
import com.vendas.system.service.TamanhoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tamanhos")
public class TamanhoController {

    private final TamanhoService service;

    public TamanhoController(TamanhoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tamanhos", service.findAll());
        return "tamanhos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        TamanhoModel tamanho = new TamanhoModel();
        tamanho.setAtivo(true);
        model.addAttribute("tamanho", tamanho);
        return "tamanhos/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("tamanho") TamanhoModel tamanho,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "tamanhos/form";
        }
        service.save(tamanho);
        return "redirect:/tamanhos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        service.findById(id).ifPresent(t -> model.addAttribute("tamanho", t));
        return "tamanhos/form";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/tamanhos";
    }
}
