package com.desafio.desafio_alura.api.v1.dto;

import com.desafio.desafio_alura.domain.enums.StatusReserva;

import java.time.LocalDateTime;

public record ReservaResponseDTO(Long id,
                                 Long usuarioId,
                                 String nomeUsuario,
                                 Long salaId,
                                 String nomeSala,
                                 LocalDateTime inicio,
                                 LocalDateTime fim,
                                 StatusReserva status){}
