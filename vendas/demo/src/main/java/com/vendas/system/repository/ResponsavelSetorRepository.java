package com.vendas.system.repository;

import com.vendas.system.model.ResponsavelSetorModel;
import com.vendas.system.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponsavelSetorRepository extends JpaRepository<ResponsavelSetorModel, Long> {

    Optional<ResponsavelSetorModel> findByUsuario(UsuarioModel usuario);

    List<ResponsavelSetorModel> findByAtivoTrue();

    List<ResponsavelSetorModel> findBySetor(String setor);

    boolean existsByUsuario(UsuarioModel usuario);
}