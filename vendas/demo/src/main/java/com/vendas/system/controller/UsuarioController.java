package com.vendas.system.controller;

import com.vendas.system.dto.AuthResponseDTO;
import com.vendas.system.dto.LoginRequestDTO;
import com.vendas.system.dto.RegisterRequestDTO;
import com.vendas.system.model.UsuarioModel;
import com.vendas.system.service.TokenService;
import com.vendas.system.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final TokenService tokenService;

    public UsuarioController(UsuarioService usuarioService, TokenService tokenService) {
        this.usuarioService = usuarioService;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegisterRequestDTO dados) {
        try {
            UsuarioModel usuario = usuarioService.registrar(dados);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dados) {
        try {
            String token = usuarioService.autenticar(dados.email(), dados.senha());
            
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Email ou senha inválidos");
            }

            AuthResponseDTO response = new AuthResponseDTO(
                    token,
                    "Bearer",
                    tokenService.getExpirationTime()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou senha inválidos");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
