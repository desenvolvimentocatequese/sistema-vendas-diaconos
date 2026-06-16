package com.vendas.system.service;

import com.vendas.system.model.SalaCosturaModel;
import com.vendas.system.repository.SalaCosturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaCosturaService {

    private final SalaCosturaRepository repository;

    public SalaCosturaService(SalaCosturaRepository repository) {
        this.repository = repository;
    }

    public List<SalaCosturaModel> findAll() {
        return repository.findAllByOrderByNomeAsc();
    }

    public List<SalaCosturaModel> findAtivas() {
        return repository.findAllByAtivoTrueOrderByNomeAsc();
    }

    public Optional<SalaCosturaModel> findById(Long id) {
        return repository.findById(id);
    }

    public SalaCosturaModel save(SalaCosturaModel sala) {
        return repository.save(sala);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
