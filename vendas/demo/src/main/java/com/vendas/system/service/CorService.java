package com.vendas.system.service;

import com.vendas.system.model.CorModel;
import com.vendas.system.repository.CorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CorService {

    private final CorRepository repository;

    public CorService(CorRepository repository) {
        this.repository = repository;
    }

    public List<CorModel> findAll() {
        return repository.findAllByOrderByNomeAsc();
    }

    public List<CorModel> findAtivas() {
        return repository.findAllByAtivoTrueOrderByNomeAsc();
    }

    public Optional<CorModel> findById(Long id) {
        return repository.findById(id);
    }

    public CorModel save(CorModel cor) {
        return repository.save(cor);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
