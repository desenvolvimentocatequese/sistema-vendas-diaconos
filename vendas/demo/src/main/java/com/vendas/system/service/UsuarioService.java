package com.vendas.system.service;

import com.vendas.system.dto.RegisterRequestDTO;
import com.vendas.system.model.UsuarioModel;
import com.vendas.system.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public UsuarioModel registrar(RegisterRequestDTO dados) {
        // Validar se email já existe
        if (usuarioRepository.findByEmail(dados.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Validar se CPF já existe
        if (usuarioRepository.findByCpf(dados.cpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        String senhaEncriptada = passwordEncoder.encode(dados.senha());
        UsuarioModel novoUsuario = new UsuarioModel(
                dados.nome(),
                dados.email(),
                senhaEncriptada,
                dados.cpf(),
                dados.role(),
                dados.telefone()
            
        );

        return usuarioRepository.save(novoUsuario);
    }

    public String autenticar(String email, String senha) {
        UsernamePasswordAuthenticationToken token = 
                new UsernamePasswordAuthenticationToken(email, senha);
        
        Authentication authentication = authenticationManager.authenticate(token);
        UsuarioModel usuario = (UsuarioModel) authentication.getPrincipal();
        
        return tokenService.generateToken(usuario);
    }

    public Optional<UsuarioModel> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<UsuarioModel> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public void desativarUsuario(Long id) {
        Optional<UsuarioModel> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            usuario.get().setIsAtivo(false);
            usuarioRepository.save(usuario.get());
        }
    }
}
