package com.vendas.system.configuration;

import com.vendas.system.model.*;
import com.vendas.system.repository.CategoriaItemRepository;
import com.vendas.system.repository.ConfiguracaoSistemaRepository;
import com.vendas.system.repository.CorRepository;
import com.vendas.system.repository.ItemPadronizadoRepository;
import com.vendas.system.repository.SalaCosturaRepository;
import com.vendas.system.repository.TamanhoRepository;
import com.vendas.system.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class DadosIniciaisRunner implements CommandLineRunner {

    private final ConfiguracaoSistemaRepository configuracaoRepository;
    private final CategoriaItemRepository categoriaRepository;
    private final CorRepository corRepository;
    private final TamanhoRepository tamanhoRepository;
    private final ItemPadronizadoRepository itemRepository;
    private final UsuarioRepository usuarioRepository;
    private final SalaCosturaRepository salaCosturaRepository;
    private final PasswordEncoder passwordEncoder;

    public DadosIniciaisRunner(ConfiguracaoSistemaRepository configuracaoRepository,
                               CategoriaItemRepository categoriaRepository,
                               CorRepository corRepository,
                               TamanhoRepository tamanhoRepository,
                               ItemPadronizadoRepository itemRepository,
                               UsuarioRepository usuarioRepository,
                               SalaCosturaRepository salaCosturaRepository,
                               PasswordEncoder passwordEncoder) {
        this.configuracaoRepository = configuracaoRepository;
        this.categoriaRepository = categoriaRepository;
        this.corRepository = corRepository;
        this.tamanhoRepository = tamanhoRepository;
        this.itemRepository = itemRepository;
        this.usuarioRepository = usuarioRepository;
        this.salaCosturaRepository = salaCosturaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdminUsuario();

        if (configuracaoRepository.count() == 0) {
            ConfiguracaoSistemaModel config = new ConfiguracaoSistemaModel();
            config.setId(1L);
            config.setModoSolicitacao(true);
            configuracaoRepository.save(config);
        }

        if (corRepository.count() == 0) {
            List.of("Preto", "Branco", "Azul", "Amarelo", "Verde", "Vermelho", "Cinza", "Bege")
                    .forEach(nome -> {
                        CorModel cor = new CorModel();
                        cor.setNome(nome);
                        corRepository.save(cor);
                    });
        }

        if (tamanhoRepository.count() == 0) {
            String[] tamanhosRoupa = {"P", "M", "G", "GG", "EXG"};
            for (int i = 0; i < tamanhosRoupa.length; i++) {
                TamanhoModel t = new TamanhoModel();
                t.setNome(tamanhosRoupa[i]);
                t.setOrdem(i);
                tamanhoRepository.save(t);
            }
            for (int num = 2; num <= 16; num += 2) {
                TamanhoModel t = new TamanhoModel();
                t.setNome(String.valueOf(num));
                t.setOrdem(100 + num);
                tamanhoRepository.save(t);
            }
            for (int num = 20; num <= 46; num += 2) {
                TamanhoModel t = new TamanhoModel();
                t.setNome(String.valueOf(num));
                t.setOrdem(200 + num);
                tamanhoRepository.save(t);
            }
        }

        if (categoriaRepository.count() == 0) {
            seedItensPadronizados();
        }

        if (salaCosturaRepository.count() == 0) {
            criarSalaCostura("Sala de Costura 1", "Responsável Sala 1");
            criarSalaCostura("Sala de Costura 2", "Responsável Sala 2");
        }
    }

    private void criarSalaCostura(String nome, String responsavel) {
        SalaCosturaModel sala = new SalaCosturaModel();
        sala.setNome(nome);
        sala.setResponsavel(responsavel);
        sala.setAtivo(true);
        salaCosturaRepository.save(sala);
    }

    private void seedAdminUsuario() {
        if (usuarioRepository.findByEmail("admin@admin.com").isPresent()) {
            return;
        }
        UsuarioModel admin = new UsuarioModel(
                "Administrador",
                "admin@admin.com",
                passwordEncoder.encode("admin123"),
                "00000000000",
                UsuarioRole.ADMIN,
                "00000000000"
        );
        usuarioRepository.save(admin);
    }

    private void seedItensPadronizados() {
        Set<CorModel> todasCores = new LinkedHashSet<>(corRepository.findAll());
        Set<TamanhoModel> tamanhosRoupa = tamanhosPorNomes("P", "M", "G", "GG", "EXG");
        Set<TamanhoModel> tamanhosInfantil = tamanhosPorNomes("2", "4", "6", "8", "10", "12", "14", "16");
        Set<TamanhoModel> tamanhosCalcadoAdulto = tamanhosPorRange(36, 46);
        Set<TamanhoModel> tamanhosCalcadoInfantil = tamanhosPorRange(20, 35);
        Set<TamanhoModel> tamanhosDiversos = new LinkedHashSet<>(tamanhoRepository.findAll());

        CategoriaItemModel femAdulto = criarCategoria("FEMININO ADULTO (P a EXG)",
                "Peças femininas adultas — tamanhos P a EXG", 1);
        criarItem("F03", "BLUSA DE FRIO FEM.", femAdulto, todasCores, tamanhosRoupa);
        criarItem("F05", "BLUSA/ CAMISA FEM.", femAdulto, todasCores, tamanhosRoupa);
        criarItem("F04", "CALCINHA", femAdulto, todasCores, tamanhosRoupa);
        criarItem("F07", "CAMISOLA", femAdulto, todasCores, tamanhosRoupa);
        criarItem("F11", "CONJUNTO/ VESTIDO", femAdulto, todasCores, tamanhosRoupa);
        criarItem("F16", "SAIA", femAdulto, todasCores, tamanhosRoupa);
        criarItem("F17", "SUTIÃ", femAdulto, todasCores, tamanhosRoupa);
        criarItem("F20", "VÉU", femAdulto, todasCores, tamanhosRoupa);
        criarItem("F70", "CALÇADO (A PARTIR DO Nº 36)", femAdulto, todasCores, tamanhosCalcadoAdulto);

        CategoriaItemModel femInfantil = criarCategoria("FEMININO INFANTIL (2 a 16)",
                "Peças femininas infantis — tamanhos 2 a 16", 2);
        criarItem("IF05", "BLUSA DE FRIO FEM. INFANTIL", femInfantil, todasCores, tamanhosInfantil);
        criarItem("IF04", "CALCINHA INFANTIL", femInfantil, todasCores, tamanhosInfantil);
        criarItem("IF06", "BLUSINHA", femInfantil, todasCores, tamanhosInfantil);
        criarItem("IF08", "CAMISOLA/ PIJAMA INF.", femInfantil, todasCores, tamanhosInfantil);
        criarItem("IF11", "CONJUNTO/ VESTIDO INFANTIL", femInfantil, todasCores, tamanhosInfantil);
        criarItem("IF03", "SAIA INFANTIL", femInfantil, todasCores, tamanhosInfantil);
        criarItem("IF20", "VÉU INFANTIL", femInfantil, todasCores, tamanhosInfantil);
        criarItem("IF70", "CALÇADO FEM. INFANTIL (20 A 35)", femInfantil, todasCores, tamanhosCalcadoInfantil);

        CategoriaItemModel mascAdulto = criarCategoria("MASCULINO ADULTO (P a EXG)",
                "Peças masculinas adultas — tamanhos P a EXG", 3);
        criarItem("M10", "BLUSA DE FRIO MASC.", mascAdulto, todasCores, tamanhosRoupa);
        criarItem("M03", "CALÇA MASCULINA", mascAdulto, todasCores, tamanhosRoupa);
        criarItem("M05", "CAMISA MASC.", mascAdulto, todasCores, tamanhosRoupa);
        criarItem("M04", "CUECA", mascAdulto, todasCores, tamanhosRoupa);
        criarItem("M07", "MEIA ADULTO", mascAdulto, todasCores, tamanhosRoupa);
        criarItem("M06", "CAMISETA", mascAdulto, todasCores, tamanhosRoupa);
        criarItem("M08", "GRAVATA", mascAdulto, todasCores, tamanhosRoupa);
        criarItem("M13", "PALETÓ", mascAdulto, todasCores, tamanhosRoupa);
        criarItem("M12", "PIJAMA MASC.", mascAdulto, todasCores, tamanhosRoupa);
        criarItem("M14", "TERNO", mascAdulto, todasCores, tamanhosRoupa);
        criarItem("M70", "CALÇADO (A PARTIR DO Nº 36)", mascAdulto, todasCores, tamanhosCalcadoAdulto);

        CategoriaItemModel mascInfantil = criarCategoria("MASCULINO INFANTIL (02 a 16)",
                "Peças masculinas infantis — tamanhos 02 a 16", 4);
        criarItem("IM10", "BLUSA DE FRIO MASC. INFANTIL", mascInfantil, todasCores, tamanhosInfantil);
        criarItem("IM03", "CALÇA INFANTIL", mascInfantil, todasCores, tamanhosInfantil);
        criarItem("IM04", "CUECA INFANTIL", mascInfantil, todasCores, tamanhosInfantil);
        criarItem("IM06", "CAMISA/ CAMISETA INFANTIL", mascInfantil, todasCores, tamanhosInfantil);
        criarItem("IM07", "MEIA INFANTIL", mascInfantil, todasCores, tamanhosInfantil);
        criarItem("IM12", "PIJAMA INFANTIL", mascInfantil, todasCores, tamanhosInfantil);
        criarItem("IM13", "SHORTS INFANTIL (02 A 12)", mascInfantil, todasCores,
                tamanhosPorNomes("2", "4", "6", "8", "10", "12"));
        criarItem("IM14", "TERNO INFANTIL", mascInfantil, todasCores, tamanhosInfantil);
        criarItem("IM70", "CALÇADO MASC. INFANTIL (20 A 35)", mascInfantil, todasCores, tamanhosCalcadoInfantil);

        CategoriaItemModel diversos = criarCategoria("ROUPA DE CAMA, BANHO & DIVERSOS",
                "Enxoval, cama, banho e itens diversos", 5);
        criarItem("B90", "ENXOVAL PARA BEBÊ", diversos, todasCores, tamanhosDiversos);
        criarItem("C01S", "COLCHA/ EDREDON SOLTEIRO", diversos, todasCores, tamanhosDiversos);
        criarItem("C01C", "COLCHA/ EDREDON CASAL", diversos, todasCores, tamanhosDiversos);
        criarItem("C03-S", "COBERTOR DE SOLTEIRO", diversos, todasCores, tamanhosDiversos);
        criarItem("C03-C", "COBERTOR DE CASAL", diversos, todasCores, tamanhosDiversos);
        criarItem("C05-S", "JOGO DE LENÇOL SOLTEIRO", diversos, todasCores, tamanhosDiversos);
        criarItem("C05-C", "JOGO DE LENÇOL CASAL", diversos, todasCores, tamanhosDiversos);
        criarItem("C06-B", "TOALHA DE BANHO", diversos, todasCores, tamanhosDiversos);
        criarItem("D02", "FRALDAS", diversos, todasCores, tamanhosDiversos);
        criarItem("D31", "UTENSÍLIOS BEBÊS E MATERNIDADE", diversos, todasCores, tamanhosDiversos);
        criarItem("D32", "BRINQUEDOS", diversos, todasCores, tamanhosDiversos);
        criarItem("D33", "ACESSÓRIOS USO PESSOAL FEMININO", diversos, todasCores, tamanhosDiversos);
        criarItem("D34", "ACESSÓRIOS USO PESSOAL MASCULINO", diversos, todasCores, tamanhosDiversos);
        criarItem("D35", "ACESSÓRIOS PARA ENFERMOS", diversos, todasCores, tamanhosDiversos);
        criarItem("D36", "ACESSÓRIOS CAMA MESA E BANHO", diversos, todasCores, tamanhosDiversos);
        criarItem("D37", "UTENSÍLIOS DOMÉSTICOS", diversos, todasCores, tamanhosDiversos);
        criarItem("D38", "ELETRODOMÉSTICOS", diversos, todasCores, tamanhosDiversos);
        criarItem("D39", "MÓVEIS", diversos, todasCores, tamanhosDiversos);
    }

    private CategoriaItemModel criarCategoria(String nome, String descricao, int ordem) {
        CategoriaItemModel cat = new CategoriaItemModel();
        cat.setNome(nome);
        cat.setDescricao(descricao);
        cat.setOrdem(ordem);
        return categoriaRepository.save(cat);
    }

    private void criarItem(String codigo, String nome, CategoriaItemModel categoria,
                           Set<CorModel> cores, Set<TamanhoModel> tamanhos) {
        if (itemRepository.findByCodigo(codigo).isPresent()) {
            return;
        }
        ItemPadronizadoModel item = new ItemPadronizadoModel();
        item.setCodigo(codigo);
        item.setNome(nome);
        item.setCategoria(categoria);
        item.setCoresDisponiveis(cores);
        item.setTamanhosDisponiveis(tamanhos);
        item.setAtivo(true);
        itemRepository.save(item);
    }

    private Set<TamanhoModel> tamanhosPorNomes(String... nomes) {
        Set<TamanhoModel> result = new LinkedHashSet<>();
        for (String nome : nomes) {
            tamanhoRepository.findAll().stream()
                    .filter(t -> t.getNome().equals(nome))
                    .findFirst()
                    .ifPresent(result::add);
        }
        return result;
    }

    private Set<TamanhoModel> tamanhosPorRange(int inicio, int fim) {
        Set<TamanhoModel> result = new LinkedHashSet<>();
        for (TamanhoModel t : tamanhoRepository.findAll()) {
            try {
                int num = Integer.parseInt(t.getNome());
                if (num >= inicio && num <= fim) {
                    result.add(t);
                }
            } catch (NumberFormatException ignored) {
                // ignora tamanhos alfabéticos
            }
        }
        return result;
    }
}
