package com.vendas.system.service;

import com.vendas.system.model.ChamadoModel;
import com.vendas.system.repository.ChamadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;

    public ChamadoService(ChamadoRepository chamadoRepository) {
        this.chamadoRepository = chamadoRepository;
    }

    public List<ChamadoModel> findAll() {
        return chamadoRepository.findAll();
    }

    public Optional<ChamadoModel> findById(Long id) {
        return chamadoRepository.findById(id);
    }

    public ChamadoModel save(ChamadoModel chamado) {
        return chamadoRepository.save(chamado);
    }

    public void deleteById(Long id) {
        chamadoRepository.deleteById(id);
    }
}