package com.vendas.system.controller;

import com.vendas.system.dto.RegisterRequestDTO;
import com.vendas.system.model.UsuarioRole;
import com.vendas.system.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequestMapping("/equipe/usuarios")
public class EquipeUsuarioController {

    private static final List<UsuarioRole> PERFIS_EQUIPE = List.of(
            UsuarioRole.ADMIN,
            UsuarioRole.USER_COMUM,
            UsuarioRole.RESPONSAVEL_SETOR
    );

    private final UsuarioService usuarioService;

    public EquipeUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/novo")
    public String formNovo(
            @RequestParam(value = "criado", required = false) String criado,
            Model model) {
        if (!model.containsAttribute("cadastroEquipe")) {
            model.addAttribute("cadastroEquipe", new RegisterRequestDTO(
                    "", "", "", "", UsuarioRole.USER_COMUM, ""
            ));
        }
        model.addAttribute("perfisEquipe", PERFIS_EQUIPE);
        if ("1".equals(criado)) {
            model.addAttribute("success", "Usuário da equipe cadastrado com sucesso.");
        }
        return "equipe/usuario-novo";
    }

    @PostMapping
    public String criar(
            @Valid @ModelAttribute("cadastroEquipe") RegisterRequestDTO dados,
            BindingResult bindingResult,
            Model model) {
        model.addAttribute("perfisEquipe", PERFIS_EQUIPE);
        if (bindingResult.hasErrors()) {
            return "equipe/usuario-novo";
        }
        if (dados.role() == UsuarioRole.CLIENTE) {
            model.addAttribute("error", "Perfil cliente só pode ser criado pelo cadastro público da loja.");
            return "equipe/usuario-novo";
        }
        try {
            usuarioService.registrar(dados);
            return "redirect:/equipe/usuarios/novo?criado=1";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "equipe/usuario-novo";
        }
    }
}
