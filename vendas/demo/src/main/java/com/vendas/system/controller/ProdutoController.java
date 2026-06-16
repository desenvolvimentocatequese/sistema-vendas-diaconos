package com.vendas.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Redireciona rotas legadas de produtos para itens padronizados.
 */
@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @GetMapping({"", "/", "/novo", "/editar/{id}", "/deletar/{id}"})
    public String redirecionar() {
        return "redirect:/itens";
    }
}
