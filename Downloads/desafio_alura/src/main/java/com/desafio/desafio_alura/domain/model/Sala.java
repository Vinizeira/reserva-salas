package com.desafio.desafio_alura.domain.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tb_salas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_sala_nome", columnNames = {"nome"})
})
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(nullable = false)
    private Integer capacidade;

    @Column(nullable = false)
    private boolean ativa;

    @Deprecated
    protected Sala() {}

    public Sala(String nome, Integer capacidade, boolean ativa) {
        this.nome = nome;
        setCapacidade(capacidade);
        this.ativa = ativa;
    }

    public void setCapacidade(Integer capacidade) {
        if (capacidade == null || capacidade <= 0) {
            throw new IllegalArgumentException("A capacidade da sala deve ser maior que zero.");
        }
        this.capacidade = capacidade;
    }

    public void ativar() { this.ativa = true; }
    public void inativar() { this.ativa = false; }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Integer getCapacidade() { return capacidade; }
    public boolean isAtiva() { return ativa; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sala sala = (Sala) o;
        return Objects.equals(id, sala.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}