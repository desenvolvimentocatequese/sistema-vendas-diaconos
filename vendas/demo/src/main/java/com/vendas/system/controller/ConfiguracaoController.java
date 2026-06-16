package com.vendas.system.controller;

import com.vendas.system.service.ConfiguracaoSistemaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/configuracao")
public class ConfiguracaoController {

    private final ConfiguracaoSistemaService configuracaoService;

    public ConfiguracaoController(ConfiguracaoSistemaService configuracaoService) {
        this.configuracaoService = configuracaoService;
    }

    @GetMapping
    public String exibir(Model model) {
        model.addAttribute("configuracao", configuracaoService.obter());
        return "configuracao/form";
    }

    @PostMapping
    public String salvar(@RequestParam(defaultValue = "false") boolean modoSolicitacao,
                         RedirectAttributes redirectAttributes) {
        configuracaoService.salvar(modoSolicitacao);
        redirectAttributes.addFlashAttribute("sucesso", "Configuração salva com sucesso.");
        return "redirect:/configuracao";
    }
}
