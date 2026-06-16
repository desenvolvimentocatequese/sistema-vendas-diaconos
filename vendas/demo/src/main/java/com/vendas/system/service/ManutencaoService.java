package com.vendas.system.service;

import com.vendas.system.configuration.DadosIniciaisRunner;
import com.vendas.system.model.UsuarioModel;
import com.vendas.system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ManutencaoService {

    private static final String EMAIL_ADMIN = "admin@admin.com";

    private final MovimentacaoPedidoRepository movimentacaoPedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ChamadoRepository chamadoRepository;
    private final EstoqueRepository estoqueRepository;
    private final ItemPadronizadoRepository itemPadronizadoRepository;
    private final CategoriaItemRepository categoriaItemRepository;
    private final CorRepository corRepository;
    private final TamanhoRepository tamanhoRepository;
    private final SalaCosturaRepository salaCosturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DadosIniciaisRunner dadosIniciaisRunner;

    public ManutencaoService(MovimentacaoPedidoRepository movimentacaoPedidoRepository,
                             ItemPedidoRepository itemPedidoRepository,
                             PedidoRepository pedidoRepository,
                             ChamadoRepository chamadoRepository,
                             EstoqueRepository estoqueRepository,
                             ItemPadronizadoRepository itemPadronizadoRepository,
                             CategoriaItemRepository categoriaItemRepository,
                             CorRepository corRepository,
                             TamanhoRepository tamanhoRepository,
                             SalaCosturaRepository salaCosturaRepository,
                             UsuarioRepository usuarioRepository,
                             DadosIniciaisRunner dadosIniciaisRunner) {
        this.movimentacaoPedidoRepository = movimentacaoPedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.chamadoRepository = chamadoRepository;
        this.estoqueRepository = estoqueRepository;
        this.itemPadronizadoRepository = itemPadronizadoRepository;
        this.categoriaItemRepository = categoriaItemRepository;
        this.corRepository = corRepository;
        this.tamanhoRepository = tamanhoRepository;
        this.salaCosturaRepository = salaCosturaRepository;
        this.usuarioRepository = usuarioRepository;
        this.dadosIniciaisRunner = dadosIniciaisRunner;
    }

    /**
     * Remove todos os dados do sistema preservando apenas o usuário administrador
     * e, em seguida, repovoa o catálogo base (cores, tamanhos, categorias, itens e salas).
     */
    @Transactional
    public void limparDadosExcetoAdmin() {
        // 1) Dados que dependem de outras tabelas (ordem segura para as FKs)
        movimentacaoPedidoRepository.deleteAllInBatch();
        itemPedidoRepository.deleteAllInBatch();
        pedidoRepository.deleteAllInBatch();
        chamadoRepository.deleteAllInBatch();
        estoqueRepository.deleteAllInBatch();

        // 2) Catálogo (deleteAll para limpar as tabelas de junção ManyToMany)
        itemPadronizadoRepository.deleteAll();
        categoriaItemRepository.deleteAll();
        corRepository.deleteAll();
        tamanhoRepository.deleteAll();
        salaCosturaRepository.deleteAll();

        // 3) Usuários, exceto o admin
        List<UsuarioModel> aRemover = usuarioRepository.findAll().stream()
                .filter(u -> !EMAIL_ADMIN.equalsIgnoreCase(u.getEmail()))
                .toList();
        usuarioRepository.deleteAll(aRemover);

        // 4) Garante admin e repovoa catálogo base
        dadosIniciaisRunner.garantirAdmin();
        dadosIniciaisRunner.garantirDadosBase();
    }
}
