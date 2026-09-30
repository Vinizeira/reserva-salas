package com.desafio.desafio_alura.domain.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tb_usuarios")
public class Usuario {

    // Identificador único (Primary Key) gerado automaticamente pelo banco
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome completo do responsável pela reserva
    @Column(nullable = false, length = 100)
    private String nome;

    // E-mail para identificação, contato e envio de confirmações
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Deprecated // Exigido pelo JPA
    public Usuario() {}

    public Usuario(String nome, String email) {
        validarEstado(nome, email);
        this.nome = nome;
        this.email = email;
    }

    private void validarEstado(String nome, String email) {
        if(nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do usuário é obrigatório");
        }
        if(email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("O email do usuário é inválido");
        }
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

