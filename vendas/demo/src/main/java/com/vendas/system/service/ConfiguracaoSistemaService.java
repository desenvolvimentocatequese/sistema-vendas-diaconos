package com.vendas.system.service;

import com.vendas.system.model.ConfiguracaoSistemaModel;
import com.vendas.system.repository.ConfiguracaoSistemaRepository;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracaoSistemaService {

    private final ConfiguracaoSistemaRepository repository;

    public ConfiguracaoSistemaService(ConfiguracaoSistemaRepository repository) {
        this.repository = repository;
    }

    public ConfiguracaoSistemaModel obter() {
        return repository.findById(1L).orElseGet(() -> {
            ConfiguracaoSistemaModel config = new ConfiguracaoSistemaModel();
            config.setId(1L);
            config.setModoSolicitacao(false);
            return repository.save(config);
        });
    }

    public boolean isModoSolicitacao() {
        return Boolean.TRUE.equals(obter().getModoSolicitacao());
    }

    public ConfiguracaoSistemaModel salvar(boolean modoSolicitacao) {
        ConfiguracaoSistemaModel config = obter();
        config.setModoSolicitacao(modoSolicitacao);
        return repository.save(config);
    }
}
