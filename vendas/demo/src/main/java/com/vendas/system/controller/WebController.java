package com.vendas.system.controller;

import com.vendas.system.dto.CheckoutDTO;
import com.vendas.system.dto.ClienteRegisterDTO;
import com.vendas.system.model.Cor;
import com.vendas.system.model.Tamanho;
import com.vendas.system.model.TipoProduto;
import com.vendas.system.model.UsuarioModel;
import com.vendas.system.model.cart.Carrinho;
import com.vendas.system.model.cart.CarrinhoItem;
import com.vendas.system.service.PedidoService;
import com.vendas.system.service.ProdutoService;
import com.vendas.system.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class WebController {

    private final UsuarioService usuarioService;
    private final ProdutoService produtoService;
    private final PedidoService pedidoService;

    public WebController(UsuarioService usuarioService, ProdutoService produtoService, PedidoService pedidoService) {
        this.usuarioService = usuarioService;
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("clienteRegister", new ClienteRegisterDTO("", "", "", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("clienteRegister") ClienteRegisterDTO dados,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            usuarioService.registrarCliente(dados);
            return "redirect:/login?success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("clienteRegister", dados);
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("usuario", auth.getPrincipal());
        }
        return "dashboard";
    }

    @GetMapping("/catalogoProdutos")
    public String catalogoProdutos(@RequestParam(value = "busca", required = false) String busca,
                                   @RequestParam(value = "tipo", required = false) TipoProduto tipo,
                                   @RequestParam(value = "cor", required = false) Cor cor,
                                   @RequestParam(value = "tamanho", required = false) Tamanho tamanho,
                                   @RequestParam(value = "precoMin", required = false) BigDecimal precoMin,
                                   @RequestParam(value = "precoMax", required = false) BigDecimal precoMax,
                                   @RequestParam(value = "ordenacao", required = false) String ordenacao,
                                   Model model) {
        model.addAttribute("produtos", produtoService.buscarCatalogo(
                busca, tipo, cor, tamanho, precoMin, precoMax, ordenacao
        ));
        model.addAttribute("busca", busca);
        model.addAttribute("tipoSelecionado", tipo);
        model.addAttribute("corSelecionada", cor);
        model.addAttribute("tamanhoSelecionado", tamanho);
        model.addAttribute("precoMin", precoMin);
        model.addAttribute("precoMax", precoMax);
        model.addAttribute("ordenacao", ordenacao);
        return "catalogoProdutos";
    }

    @GetMapping("/produto/{id}")
    public String produtoDetalhe(@PathVariable Long id, Model model) {
        produtoService.findById(id).ifPresent(produto -> model.addAttribute("produto", produto));
        return "produtoDetalhe";
    }

    @PostMapping("/adicionarCarrinho")
    public String adicionarCarrinho(@RequestParam Long produtoId,
                                    @RequestParam(required = false) Cor cor,
                                    @RequestParam Tamanho tamanho,
                                    @RequestParam(defaultValue = "1") int quantidade,
                                    HttpSession session) {
        produtoService.findById(produtoId).ifPresent(produto -> {
            Carrinho carrinho = getCarrinho(session);
            int quantidadeAjustada = Math.max(1, quantidade);

            Cor corSelecionada = cor;
            if (corSelecionada == null && produto.getCoresDisponiveis() != null && !produto.getCoresDisponiveis().isEmpty()) {
                corSelecionada = produto.getCoresDisponiveis().iterator().next();
            }

            CarrinhoItem item = new CarrinhoItem();
            item.setProdutoId(produtoId);
            item.setNomeProduto(produto.getNome());
            item.setImagemPath(produto.getImagemPath());
            item.setPreco(produto.getPreco());
            item.setCor(corSelecionada);
            item.setTamanho(tamanho);
            item.setQuantidade(quantidadeAjustada);
            carrinho.adicionar(item);

            session.setAttribute("carrinho", carrinho);
        });
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/remover")
    public String removerItemCarrinho(@RequestParam int indice, HttpSession session) {
        Carrinho carrinho = getCarrinho(session);
        carrinho.remover(indice);
        session.setAttribute("carrinho", carrinho);
        return "redirect:/carrinho";
    }

    @GetMapping("/carrinho")
    public String carrinho(HttpSession session, Model model) {
        Carrinho carrinho = getCarrinho(session);
        model.addAttribute("itens", carrinho.getItens());
        model.addAttribute("total", carrinho.total());
        return "carrinho";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Carrinho carrinho = getCarrinho(session);
        if (carrinho.estaVazio()) {
            return "redirect:/carrinho";
        }
        model.addAttribute("carrinho", carrinho.getItens());
        model.addAttribute("total", carrinho.total());
        return "checkout";
    }

    @PostMapping("/finalizarPedido")
    public String finalizarPedido(@ModelAttribute CheckoutDTO checkout, HttpSession session, Model model) {
        Carrinho carrinho = getCarrinho(session);
        if (carrinho.estaVazio()) {
            return "redirect:/carrinho";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        UsuarioModel usuario = (UsuarioModel) authentication.getPrincipal();

        BigDecimal totalProdutos = carrinho.total();
        BigDecimal taxaEntrega = checkout.getTipoEntrega().equals("ENTREGA") ? new BigDecimal("10.00") : BigDecimal.ZERO;
        BigDecimal total = totalProdutos.add(taxaEntrega);

        String endereco = "";
        if ("ENTREGA".equals(checkout.getTipoEntrega())) {
            endereco = checkout.getRua() + ", " + checkout.getNumero() + ", " + checkout.getCidade();
        }

        var pedido = pedidoService.salvarPedidoComItens(usuario, checkout.getTipoEntrega(), endereco, carrinho.getItens());

        session.removeAttribute("carrinho");

        model.addAttribute("pedidoId", pedido.getId());
        model.addAttribute("checkout", checkout);
        model.addAttribute("carrinho", carrinho.getItens());
        model.addAttribute("totalProdutos", totalProdutos);
        model.addAttribute("taxaEntrega", taxaEntrega);
        model.addAttribute("total", total);
        return "confirmacao";
    }

    private Carrinho getCarrinho(HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }

}