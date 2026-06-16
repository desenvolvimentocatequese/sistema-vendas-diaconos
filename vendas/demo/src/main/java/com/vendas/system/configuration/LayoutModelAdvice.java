package com.vendas.system.configuration;

import com.vendas.system.controller.*;
import com.vendas.system.model.UsuarioModel;
import com.vendas.system.model.UsuarioRole;
import com.vendas.system.model.cart.Carrinho;
import com.vendas.system.service.CategoriaItemService;
import com.vendas.system.service.ConfiguracaoSistemaService;
import com.vendas.system.service.CorService;
import com.vendas.system.service.TamanhoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {
        WebController.class, ItemPadronizadoController.class, ChamadoController.class,
        PedidoController.class, EquipeUsuarioController.class, ConfiguracaoController.class,
        CategoriaItemController.class, CorController.class, TamanhoController.class,
        EstoqueController.class, SalaCosturaController.class, MovimentacaoPedidoController.class
})
public class LayoutModelAdvice {

    private final CategoriaItemService categoriaService;
    private final CorService corService;
    private final TamanhoService tamanhoService;
    private final ConfiguracaoSistemaService configuracaoService;

    public LayoutModelAdvice(CategoriaItemService categoriaService,
                             CorService corService,
                             TamanhoService tamanhoService,
                             ConfiguracaoSistemaService configuracaoService) {
        this.categoriaService = categoriaService;
        this.corService = corService;
        this.tamanhoService = tamanhoService;
        this.configuracaoService = configuracaoService;
    }

    @ModelAttribute("categorias")
    public Object categorias() {
        return categoriaService.findAtivas();
    }

    @ModelAttribute("cores")
    public Object cores() {
        return corService.findAtivas();
    }

    @ModelAttribute("tamanhos")
    public Object tamanhos() {
        return tamanhoService.findAtivos();
    }

    @ModelAttribute("modoSolicitacao")
    public boolean modoSolicitacao() {
        return configuracaoService.isModoSolicitacao();
    }

    @ModelAttribute("labelPedido")
    public String labelPedido() {
        return configuracaoService.isModoSolicitacao() ? "Solicitação" : "Pedido";
    }

    @ModelAttribute("labelPedidos")
    public String labelPedidos() {
        return configuracaoService.isModoSolicitacao() ? "Solicitações" : "Pedidos";
    }

    @ModelAttribute("labelCarrinho")
    public String labelCarrinho() {
        return configuracaoService.isModoSolicitacao() ? "Solicitação" : "Carrinho";
    }

    @ModelAttribute("labelFinalizar")
    public String labelFinalizar() {
        return configuracaoService.isModoSolicitacao() ? "Enviar solicitação" : "Finalizar compra";
    }

    @ModelAttribute("carrinhoQuantidade")
    public int carrinhoQuantidade(HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            return 0;
        }
        return carrinho.quantidadeTotalItens();
    }

    @ModelAttribute("usuarioLogado")
    public boolean usuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return !(authentication instanceof AnonymousAuthenticationToken);
    }

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

    @ModelAttribute("usuarioEhAdmin")
    public boolean usuarioEhAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        if (!(authentication.getPrincipal() instanceof UsuarioModel usuario)) {
            return false;
        }
        return usuario.getRole() == UsuarioRole.ADMIN;
    }
}
