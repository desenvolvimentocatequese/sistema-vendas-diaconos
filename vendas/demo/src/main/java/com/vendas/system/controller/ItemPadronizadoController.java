package com.vendas.system.controller;

import com.vendas.system.model.CorModel;
import com.vendas.system.model.EstoqueModel;
import com.vendas.system.model.ItemPadronizadoModel;
import com.vendas.system.model.TamanhoModel;
import com.vendas.system.service.CategoriaItemService;
import com.vendas.system.service.CorService;
import com.vendas.system.service.EstoqueService;
import com.vendas.system.service.ItemPadronizadoService;
import com.vendas.system.service.TamanhoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/itens")
public class ItemPadronizadoController {

    private final ItemPadronizadoService itemService;
    private final CategoriaItemService categoriaService;
    private final CorService corService;
    private final TamanhoService tamanhoService;
    private final EstoqueService estoqueService;
    private static final String UPLOAD_DIR = "uploads/images/";

    public ItemPadronizadoController(ItemPadronizadoService itemService,
                                     CategoriaItemService categoriaService,
                                     CorService corService,
                                     TamanhoService tamanhoService,
                                     EstoqueService estoqueService) {
        this.itemService = itemService;
        this.categoriaService = categoriaService;
        this.corService = corService;
        this.tamanhoService = tamanhoService;
        this.estoqueService = estoqueService;
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @GetMapping
    public String listar(@RequestParam(value = "busca", required = false) String busca,
                         @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                         @RequestParam(value = "ativo", required = false) Boolean ativo,
                         Model model) {
        model.addAttribute("itens", itemService.findByFiltros(busca, categoriaId, ativo));
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("filtroBusca", busca);
        model.addAttribute("filtroCategoriaId", categoriaId);
        model.addAttribute("filtroAtivo", ativo);
        return "itens/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        ItemPadronizadoModel item = new ItemPadronizadoModel();
        item.setAtivo(true);
        model.addAttribute("item", item);
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("coresLista", corService.findAll());
        model.addAttribute("tamanhosLista", tamanhoService.findAll());
        return "itens/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("item") ItemPadronizadoModel item,
                         BindingResult bindingResult,
                         @RequestParam(value = "imagem", required = false) MultipartFile imagem,
                         @RequestParam(value = "categoria.id", required = false) Long categoriaId,
                         @RequestParam(value = "coresDisponiveis", required = false) Set<Long> corIds,
                         @RequestParam(value = "tamanhosDisponiveis", required = false) Set<Long> tamanhoIds,
                         @RequestParam(value = "quantidadeInicial", required = false) Integer quantidadeInicial,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (categoriaId != null) {
            categoriaService.findById(categoriaId).ifPresent(item::setCategoria);
        }
        Set<CorModel> coresSelecionadas = new LinkedHashSet<>();
        if (corIds != null) {
            corIds.forEach(id -> corService.findById(id).ifPresent(coresSelecionadas::add));
        }
        item.setCoresDisponiveis(coresSelecionadas);

        Set<TamanhoModel> tamanhosSelecionados = new LinkedHashSet<>();
        if (tamanhoIds != null) {
            tamanhoIds.forEach(id -> tamanhoService.findById(id).ifPresent(tamanhosSelecionados::add));
        }
        if (!tamanhosSelecionados.isEmpty()) {
            item.setTamanhosDisponiveis(tamanhosSelecionados);
        }
        ItemPadronizadoModel existente = null;
        if (item.getId() != null) {
            existente = itemService.findById(item.getId()).orElse(null);
            if (existente == null) {
                redirectAttributes.addFlashAttribute("erro", "Item não encontrado.");
                return "redirect:/itens";
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAll());
            model.addAttribute("coresLista", corService.findAll());
            model.addAttribute("tamanhosLista", tamanhoService.findAll());
            return "itens/form";
        }

        if (imagem != null && !imagem.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" + imagem.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, imagem.getBytes());
                item.setImagemPath(fileName);
            } catch (IOException e) {
                model.addAttribute("erroUpload", "Não foi possível salvar a imagem.");
                model.addAttribute("categorias", categoriaService.findAll());
                model.addAttribute("coresLista", corService.findAll());
                model.addAttribute("tamanhosLista", tamanhoService.findAll());
                return "itens/form";
            }
        } else if (existente != null) {
            item.setImagemPath(existente.getImagemPath());
        }

        ItemPadronizadoModel salvo = itemService.save(item);

        if (quantidadeInicial != null && quantidadeInicial > 0) {
            gerarEstoqueInicial(salvo, coresSelecionadas, tamanhosSelecionados, quantidadeInicial);
        }

        return "redirect:/itens";
    }

    /**
     * Cria um registro de estoque por combinação tamanho × cor com a quantidade informada.
     * Sem cores selecionadas, gera um registro por tamanho (cor nula).
     */
    private void gerarEstoqueInicial(ItemPadronizadoModel item,
                                     Set<CorModel> cores,
                                     Set<TamanhoModel> tamanhos,
                                     int quantidade) {
        if (tamanhos == null || tamanhos.isEmpty()) {
            return;
        }
        for (TamanhoModel tamanho : tamanhos) {
            if (cores == null || cores.isEmpty()) {
                salvarOuSomarEstoque(item, null, tamanho, quantidade);
            } else {
                for (CorModel cor : cores) {
                    salvarOuSomarEstoque(item, cor, tamanho, quantidade);
                }
            }
        }
    }

    private void salvarOuSomarEstoque(ItemPadronizadoModel item, CorModel cor, TamanhoModel tamanho, int quantidade) {
        Long corId = cor != null ? cor.getId() : null;
        EstoqueModel estoque = estoqueService
                .findByItemCorTamanho(item.getId(), corId, tamanho.getId())
                .orElseGet(() -> {
                    EstoqueModel novo = new EstoqueModel();
                    novo.setItem(item);
                    novo.setCor(cor);
                    novo.setTamanho(tamanho);
                    novo.setQuantidade(0);
                    return novo;
                });
        estoque.setQuantidade(estoque.getQuantidade() + quantidade);
        estoqueService.save(estoque);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        itemService.findById(id).ifPresent(item -> {
            if (item.getAtivo() == null) {
                item.setAtivo(true);
            }
            model.addAttribute("item", item);
        });
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("coresLista", corService.findAll());
        model.addAttribute("tamanhosLista", tamanhoService.findAll());
        return "itens/form";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        itemService.deleteById(id);
        return "redirect:/itens";
    }
}
