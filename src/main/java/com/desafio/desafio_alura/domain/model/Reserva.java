package com.desafio.desafio_alura.domain.model;

import com.desafio.desafio_alura.domain.enums.StatusReserva;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_reservas", indexes = {
        @Index(name = "idx_reserva_sala_datas", columnList = "sala_id, inicio, fim, status")
})
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusReserva status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @Deprecated
    protected Reserva() {}

    public Reserva(Usuario usuario, Sala sala, LocalDateTime inicio, LocalDateTime fim) {
        if (usuario == null) throw new IllegalArgumentException("Usuário é obrigatório.");
        if (sala == null) throw new IllegalArgumentException("Sala é obrigatória.");
        if (!sala.isAtiva()) throw new IllegalStateException("A sala precisa estar ativa para ser reservada.");
        if (inicio == null || fim == null || !fim.isAfter(inicio)) {
            throw new IllegalArgumentException("A data final deve ser posterior à data inicial.");
        }

        this.usuario = usuario;
        this.sala = sala;
        this.inicio = inicio;
        this.fim = fim;
        this.status = StatusReserva.ATIVA;
    }

    /**
     * MÉTODOS DE NEGÓCIO E DOMÍNIO
     */

    // Verifica sobreposição de intervalo semiaberto [inicio, fim)
    public boolean sobrepoeCom(LocalDateTime outroInicio, LocalDateTime outroFim) {
        if (this.status == StatusReserva.CANCELADA) {
            return false;
        }
        return this.inicio.isBefore(outroFim) && this.fim.isAfter(outroInicio);
    }

    public void cancelar() {
        if (this.status == StatusReserva.CANCELADA) {
            throw new IllegalStateException("Reserva já se encontra cancelada.");
        }
        this.status = StatusReserva.CANCELADA;
    }

    // GETTERS
    public Long getId() { return id; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFim() { return fim; }
    public StatusReserva getStatus() { return status; }
    public Usuario getUsuario() { return usuario; }
    public Sala getSala() { return sala; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(id, reserva.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}