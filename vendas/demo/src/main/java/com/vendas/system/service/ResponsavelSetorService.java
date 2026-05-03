package com.vendas.system.service;

import com.vendas.system.model.ResponsavelSetorModel;
import com.vendas.system.repository.ResponsavelSetorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResponsavelSetorService {

    private final ResponsavelSetorRepository responsavelSetorRepository;

    public ResponsavelSetorService(ResponsavelSetorRepository responsavelSetorRepository) {
        this.responsavelSetorRepository = responsavelSetorRepository;
    }

    public List<ResponsavelSetorModel> findAll() {
        return responsavelSetorRepository.findAll();
    }

    public Optional<ResponsavelSetorModel> findById(Long id) {
        return responsavelSetorRepository.findById(id);
    }

    public ResponsavelSetorModel save(ResponsavelSetorModel responsavelSetor) {
        return responsavelSetorRepository.save(responsavelSetor);
    }

    public void deleteById(Long id) {
        responsavelSetorRepository.deleteById(id);
    }
}