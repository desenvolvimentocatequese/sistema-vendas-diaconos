package com.vendas.system.configuration;

import com.vendas.system.controller.ChamadoController;
import com.vendas.system.controller.PedidoController;
import com.vendas.system.controller.ProdutoController;
import com.vendas.system.controller.WebController;
import com.vendas.system.model.Cor;
import com.vendas.system.model.Tamanho;
import com.vendas.system.model.TipoProduto;
import com.vendas.system.model.UsuarioModel;
import com.vendas.system.model.UsuarioRole;
import com.vendas.system.model.cart.Carrinho;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {WebController.class, ProdutoController.class, ChamadoController.class, PedidoController.class})
public class LayoutModelAdvice {

    @ModelAttribute("tipos")
    public TipoProduto[] tiposTipoProduto() {
        return TipoProduto.values();
    }

    @ModelAttribute("cores")
    public Cor[] cores() {
        return Cor.values();
    }

    @ModelAttribute("tamanhos")
    public Tamanho[] tamanhos() {
        return Tamanho.values();
    }

    @ModelAttribute("carrinhoQuantidade")
    public int carrinhoQuantidade(HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            return 0;
        }
        return carrinho.quantidadeTotalItens();
    }

    /**
     * Disponível em todos os fragmentos/layouts (Thymeleaf não expõe mais #request por padrão no Spring Boot 6+).
     */
    @ModelAttribute("usuarioLogado")
    public boolean usuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return !(authentication instanceof AnonymousAuthenticationToken);
    }

    /** Gestão interna (produtos, pedidos, chamados, painel) — oculto para perfil CLIENTE. */
    @ModelAttribute("podeAcessarGestao")
    public boolean podeAcessarGestao() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        if (!(authentication.getPrincipal() instanceof UsuarioModel usuario)) {
            return false;
        }
        return usuario.getRole() != UsuarioRole.CLIENTE;
    }
}
