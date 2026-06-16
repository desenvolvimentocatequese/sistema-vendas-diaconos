package com.vendas.system.controller;

import com.vendas.system.model.EstoqueModel;
import com.vendas.system.service.CategoriaItemService;
import com.vendas.system.service.CorService;
import com.vendas.system.service.EstoqueService;
import com.vendas.system.service.ItemPadronizadoService;
import com.vendas.system.service.TamanhoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;
    private final ItemPadronizadoService itemService;
    private final CategoriaItemService categoriaService;
    private final CorService corService;
    private final TamanhoService tamanhoService;

    public EstoqueController(EstoqueService estoqueService,
                             ItemPadronizadoService itemService,
                             CategoriaItemService categoriaService,
                             CorService corService,
                             TamanhoService tamanhoService) {
        this.estoqueService = estoqueService;
        this.itemService = itemService;
        this.categoriaService = categoriaService;
        this.corService = corService;
        this.tamanhoService = tamanhoService;
    }

    @GetMapping
    public String listar(@RequestParam(value = "itemId", required = false) Long itemId,
                         @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                         Model model) {
        model.addAttribute("estoques", estoqueService.findByFiltros(itemId, categoriaId));
        model.addAttribute("itens", itemService.findAll());
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("filtroItemId", itemId);
        model.addAttribute("filtroCategoriaId", categoriaId);
        return "estoque/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        EstoqueModel estoque = new EstoqueModel();
        estoque.setQuantidade(0);
        model.addAttribute("estoque", estoque);
        model.addAttribute("itens", itemService.findAll());
        model.addAttribute("coresLista", corService.findAll());
        model.addAttribute("tamanhosLista", tamanhoService.findAll());
        return "estoque/form";
    }

    @PostMapping
    public String salvar(@RequestParam(required = false) Long id,
                         @RequestParam Long itemId,
                         @RequestParam(required = false) Long corId,
                         @RequestParam Long tamanhoId,
                         @RequestParam int quantidade,
                         Model model) {
        if (quantidade < 0) {
            model.addAttribute("estoque", new EstoqueModel());
            model.addAttribute("itens", itemService.findAll());
            model.addAttribute("coresLista", corService.findAll());
            model.addAttribute("tamanhosLista", tamanhoService.findAll());
            model.addAttribute("erro", "Quantidade não pode ser negativa.");
            return "estoque/form";
        }

        EstoqueModel estoque = id != null
                ? estoqueService.findById(id).orElse(new EstoqueModel())
                : new EstoqueModel();

        itemService.findById(itemId).ifPresent(estoque::setItem);
        if (corId != null) {
            corService.findById(corId).ifPresent(estoque::setCor);
        } else {
            estoque.setCor(null);
        }
        tamanhoService.findById(tamanhoId).ifPresent(estoque::setTamanho);
        estoque.setQuantidade(quantidade);

        estoqueService.save(estoque);
        return "redirect:/estoque";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        estoqueService.findById(id).ifPresent(e -> model.addAttribute("estoque", e));
        model.addAttribute("itens", itemService.findAll());
        model.addAttribute("coresLista", corService.findAll());
        model.addAttribute("tamanhosLista", tamanhoService.findAll());
        return "estoque/form";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        estoqueService.deleteById(id);
        return "redirect:/estoque";
    }
}
