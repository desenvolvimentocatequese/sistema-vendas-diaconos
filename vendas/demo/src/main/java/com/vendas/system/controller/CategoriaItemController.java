package com.vendas.system.controller;

import com.vendas.system.model.CategoriaItemModel;
import com.vendas.system.service.CategoriaItemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaItemController {

    private final CategoriaItemService service;

    public CategoriaItemController(CategoriaItemService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", service.findAll());
        return "categorias/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        CategoriaItemModel categoria = new CategoriaItemModel();
        categoria.setAtivo(true);
        model.addAttribute("categoria", categoria);
        return "categorias/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("categoria") CategoriaItemModel categoria,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "categorias/form";
        }
        service.save(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        service.findById(id).ifPresent(c -> model.addAttribute("categoria", c));
        return "categorias/form";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/categorias";
    }
}
