package com.vendas.system.controller;

import com.vendas.system.dto.CheckoutDTO;
import com.vendas.system.dto.ClienteRegisterDTO;
import com.vendas.system.model.UsuarioModel;
import com.vendas.system.model.cart.Carrinho;
import com.vendas.system.model.cart.CarrinhoItem;
import com.vendas.system.service.*;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class WebController {

    private final UsuarioService usuarioService;
    private final ItemPadronizadoService itemService;
    private final PedidoService pedidoService;
    private final CategoriaItemService categoriaService;
    private final CorService corService;
    private final TamanhoService tamanhoService;
    private final ConfiguracaoSistemaService configuracaoService;

    public WebController(UsuarioService usuarioService,
                         ItemPadronizadoService itemService,
                         PedidoService pedidoService,
                         CategoriaItemService categoriaService,
                         CorService corService,
                         TamanhoService tamanhoService,
                         ConfiguracaoSistemaService configuracaoService) {
        this.usuarioService = usuarioService;
        this.itemService = itemService;
        this.pedidoService = pedidoService;
        this.categoriaService = categoriaService;
        this.corService = corService;
        this.tamanhoService = tamanhoService;
        this.configuracaoService = configuracaoService;
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
                                   @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                                   @RequestParam(value = "corId", required = false) Long corId,
                                   @RequestParam(value = "tamanhoId", required = false) Long tamanhoId,
                                   Model model) {
        model.addAttribute("itens", itemService.buscarCatalogo(busca, categoriaId, corId, tamanhoId));
        model.addAttribute("categorias", categoriaService.findAtivas());
        model.addAttribute("busca", busca);
        model.addAttribute("categoriaSelecionada", categoriaId);
        model.addAttribute("corSelecionada", corId);
        model.addAttribute("tamanhoSelecionado", tamanhoId);
        return "catalogoProdutos";
    }

    @GetMapping("/produto/{id}")
    public String itemDetalhe(@PathVariable Long id, Model model) {
        var detalhe = itemService.buscarDetalhe(id);
        if (detalhe.isEmpty()) {
            return "redirect:/catalogoProdutos";
        }
        model.addAttribute("item", detalhe.get());
        return "produtoDetalhe";
    }

    @PostMapping("/adicionarCarrinho")
    public String adicionarCarrinho(@RequestParam Long itemId,
                                   @RequestParam(required = false) Long corId,
                                   @RequestParam Long tamanhoId,
                                   @RequestParam(defaultValue = "1") int quantidade,
                                   HttpSession session) {
        itemService.findById(itemId).ifPresent(item -> {
            Carrinho carrinho = getCarrinho(session);
            int quantidadeAjustada = Math.max(1, quantidade);

            Long corSelecionada = corId;
            String corNome = null;
            if (corSelecionada == null && item.getCoresDisponiveis() != null && !item.getCoresDisponiveis().isEmpty()) {
                var primeiraCor = item.getCoresDisponiveis().iterator().next();
                corSelecionada = primeiraCor.getId();
                corNome = primeiraCor.getNome();
            } else if (corSelecionada != null) {
                corNome = corService.findById(corSelecionada).map(c -> c.getNome()).orElse(null);
            }

            String tamanhoNome = tamanhoService.findById(tamanhoId).map(t -> t.getNome()).orElse("");

            CarrinhoItem carrinhoItem = new CarrinhoItem();
            carrinhoItem.setItemId(itemId);
            carrinhoItem.setCodigoItem(item.getCodigo());
            carrinhoItem.setNomeItem(item.getCodigo() + " - " + item.getNome());
            carrinhoItem.setImagemPath(item.getImagemPath());
            carrinhoItem.setPreco(configuracaoService.isModoSolicitacao() ? null : BigDecimal.ZERO);
            carrinhoItem.setCorId(corSelecionada);
            carrinhoItem.setCorNome(corNome);
            carrinhoItem.setTamanhoId(tamanhoId);
            carrinhoItem.setTamanhoNome(tamanhoNome);
            carrinhoItem.setQuantidade(quantidadeAjustada);
            carrinho.adicionar(carrinhoItem);

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
        if (configuracaoService.isModoSolicitacao()) {
            return "redirect:/carrinho";
        }
        Carrinho carrinho = getCarrinho(session);
        if (carrinho.estaVazio()) {
            return "redirect:/carrinho";
        }
        model.addAttribute("carrinho", carrinho.getItens());
        model.addAttribute("total", carrinho.total());
        return "checkout";
    }

    @PostMapping("/finalizarSolicitacao")
    public String finalizarSolicitacao(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        return finalizarPedidoInterno(null, session, model, redirectAttributes);
    }

    @PostMapping("/finalizarPedido")
    public String finalizarPedido(@ModelAttribute CheckoutDTO checkout, HttpSession session, Model model,
                                  RedirectAttributes redirectAttributes) {
        return finalizarPedidoInterno(checkout, session, model, redirectAttributes);
    }

    private String finalizarPedidoInterno(CheckoutDTO checkout, HttpSession session, Model model,
                                          RedirectAttributes redirectAttributes) {
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

        boolean modoSolicitacao = configuracaoService.isModoSolicitacao();
        String tipoEntrega = checkout != null ? checkout.getTipoEntrega() : null;
        String endereco = "";
        if (!modoSolicitacao && checkout != null && "ENTREGA".equals(checkout.getTipoEntrega())) {
            endereco = checkout.getRua() + ", " + checkout.getNumero() + ", " + checkout.getCidade();
        }

        try {
            var pedido = pedidoService.salvarPedidoComItens(usuario, tipoEntrega, endereco, carrinho.getItens());
            session.removeAttribute("carrinho");

            model.addAttribute("pedidoId", pedido.getId());
            model.addAttribute("checkout", checkout);
            model.addAttribute("carrinho", carrinho.getItens());
            model.addAttribute("totalProdutos", carrinho.total());
            model.addAttribute("taxaEntrega", pedido.getTaxaEntrega());
            model.addAttribute("total", pedido.getValorTotal());
            return "confirmacao";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrinho";
        }

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
