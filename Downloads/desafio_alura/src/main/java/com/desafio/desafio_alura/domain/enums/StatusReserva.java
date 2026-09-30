package com.desafio.desafio_alura.domain.enums;

public enum StatusReserva {
    /**
     * Reserva confirmada e válida para ocupação da sala.
     * Participa da checagem de conflitos de horário.
     */
    ATIVA,

    /**
     * Reserva cancelada pelo usuário ou sistema.
     * Libera o horário para novas reservas e NÃO entra na checagem de conflito.
     */
    CANCELADA;

    /**
     * Valida se a transição para CANCELADA é permitida.
     * Permite transição apenas de ATIVA -> CANCELADA.
     */
    public boolean podeCancelar() {
        return this == ATIVA;
    }
}
