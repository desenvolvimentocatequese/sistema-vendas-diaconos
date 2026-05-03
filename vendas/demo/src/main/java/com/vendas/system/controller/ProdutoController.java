package com.vendas.system.controller;

import com.vendas.system.model.ProdutoModel;
import com.vendas.system.model.TipoProduto;
import com.vendas.system.service.ProdutoService;
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
import java.util.UUID;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private static final String UPLOAD_DIR = "uploads/images/";

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @GetMapping
    public String listarProdutos(@RequestParam(value = "nome", required = false) String nome,
                                 @RequestParam(value = "tipo", required = false) TipoProduto tipo,
                                 @RequestParam(value = "ativo", required = false) Boolean ativo,
                                 Model model) {
        model.addAttribute("produtos", produtoService.findByFiltros(nome, tipo, ativo));
        model.addAttribute("tipos", TipoProduto.values());
        model.addAttribute("filtroNome", nome);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroAtivo", ativo);
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String novoProduto(Model model) {
        ProdutoModel produto = new ProdutoModel();
        produto.setAtivo(true);
        model.addAttribute("produto", produto);
        return "produtos/form";
    }

    @PostMapping
    public String salvarProduto(@Valid @ModelAttribute("produto") ProdutoModel produto,
                                BindingResult bindingResult,
                                @RequestParam("imagem") MultipartFile imagem,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        ProdutoModel existente = null;
        if (produto.getId() != null) {
            existente = produtoService.findById(produto.getId()).orElse(null);
            if (existente == null) {
                redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
                return "redirect:/produtos";
            }
        }

        if (bindingResult.hasErrors()) {
            return "produtos/form";
        }

        if (!imagem.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" + imagem.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, imagem.getBytes());
                produto.setImagemPath(fileName);
            } catch (IOException e) {
                model.addAttribute("erroUpload", "Não foi possível salvar a imagem.");
                return "produtos/form";
            }
        } else if (existente != null) {
            produto.setImagemPath(existente.getImagemPath());
        }

        produtoService.save(produto);
        return "redirect:/produtos";
    }

    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        produtoService.findById(id).ifPresent(produto -> {
            if (produto.getAtivo() == null) {
                produto.setAtivo(true);
            }
            model.addAttribute("produto", produto);
        });
        return "produtos/form";
    }

    @GetMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable Long id) {
        produtoService.deleteById(id);
        return "redirect:/produtos";
    }

}