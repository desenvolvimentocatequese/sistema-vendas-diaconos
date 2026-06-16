package com.vendas.system.service;

import com.vendas.system.model.TamanhoModel;
import com.vendas.system.repository.TamanhoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TamanhoService {

    private final TamanhoRepository repository;

    public TamanhoService(TamanhoRepository repository) {
        this.repository = repository;
    }

    public List<TamanhoModel> findAll() {
        return repository.findAllByOrderByOrdemAscNomeAsc();
    }

    public List<TamanhoModel> findAtivos() {
        return repository.findAllByAtivoTrueOrderByOrdemAscNomeAsc();
    }

    public Optional<TamanhoModel> findById(Long id) {
        return repository.findById(id);
    }

    public TamanhoModel save(TamanhoModel tamanho) {
        return repository.save(tamanho);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
