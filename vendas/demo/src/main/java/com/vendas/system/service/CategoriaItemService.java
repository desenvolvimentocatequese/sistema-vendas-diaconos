package com.vendas.system.service;

import com.vendas.system.model.CategoriaItemModel;
import com.vendas.system.repository.CategoriaItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaItemService {

    private final CategoriaItemRepository repository;

    public CategoriaItemService(CategoriaItemRepository repository) {
        this.repository = repository;
    }

    public List<CategoriaItemModel> findAll() {
        return repository.findAllByOrderByOrdemAscNomeAsc();
    }

    public List<CategoriaItemModel> findAtivas() {
        return repository.findAllByAtivoTrueOrderByOrdemAscNomeAsc();
    }

    public Optional<CategoriaItemModel> findById(Long id) {
        return repository.findById(id);
    }

    public CategoriaItemModel save(CategoriaItemModel categoria) {
        return repository.save(categoria);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
