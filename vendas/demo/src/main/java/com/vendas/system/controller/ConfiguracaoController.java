package com.vendas.system.controller;

import com.vendas.system.service.ConfiguracaoSistemaService;
import com.vendas.system.service.ManutencaoService;
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
    private final ManutencaoService manutencaoService;

    public ConfiguracaoController(ConfiguracaoSistemaService configuracaoService,
                                  ManutencaoService manutencaoService) {
        this.configuracaoService = configuracaoService;
        this.manutencaoService = manutencaoService;
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

    @PostMapping("/limpar-dados")
    public String limparDados(@RequestParam(defaultValue = "") String confirmacao,
                              RedirectAttributes redirectAttributes) {
        if (!"LIMPAR".equals(confirmacao)) {
            redirectAttributes.addFlashAttribute("erro",
                    "Confirmação inválida. Digite LIMPAR para confirmar a limpeza dos dados.");
            return "redirect:/configuracao";
        }
        manutencaoService.limparDadosExcetoAdmin();
        redirectAttributes.addFlashAttribute("sucesso",
                "Dados limpos com sucesso. O usuário admin foi preservado e o catálogo base foi recriado.");
        return "redirect:/configuracao";
    }
}
