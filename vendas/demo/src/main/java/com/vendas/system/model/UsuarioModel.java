package com.vendas.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(
        name = "usuario",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "cpf")
        }
)
public class UsuarioModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String senha;

    @NotBlank
    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @NotBlank
    private String telefone;

    private String congregacao;

    private String regional;

    private String codigoRegional;

    private String endereco;

    @Enumerated(EnumType.STRING)
    private UsuarioRole role;

    @Column(nullable = false)
    private Boolean isAtivo = true;

    public UsuarioModel() {
    }

    public UsuarioModel(String nome, String email, String senha, String cpf, UsuarioRole role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.role = role;
        this.isAtivo = true;
    }


    public UsuarioModel(String nome, String email, String senha, String cpf, UsuarioRole role, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.role = role;
        this.telefone = telefone;
        this.isAtivo = true;
    }

    public UsuarioModel(String email, String encryptedPassword, UsuarioRole role) {
        this.email = email;
        this.senha = encryptedPassword;
        this.role = role;
        this.isAtivo = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UsuarioRole.ADMIN) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER_COMUM"),
                    new SimpleGrantedAuthority("ROLE_RESPONSAVEL_SETOR")
            );
        } else if (this.role == UsuarioRole.RESPONSAVEL_SETOR) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_RESPONSAVEL_SETOR")
            );
        } else {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_USER_COMUM")
            );
        }
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isAtivo;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public UsuarioRole getRole() {
        return role;
    }

    public void setRole(UsuarioRole role) {
        this.role = role;
    }

    public Boolean getIsAtivo() {
        return isAtivo;
    }

    public void setIsAtivo(Boolean isAtivo) {
        this.isAtivo = isAtivo;
    }
}
