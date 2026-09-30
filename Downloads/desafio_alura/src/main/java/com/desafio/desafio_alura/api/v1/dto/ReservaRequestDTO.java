package com.desafio.desafio_alura.api.v1.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservaRequestDTO(@NotNull(message = "O ID do usuário é obrigatório")
                                Long usuarioId,

                                @NotNull(message = "O ID da sala é obrigatório")
                                Long salaId,

                                @NotNull(message = "A data de início é obrigatória")
                                @Future(message = "A data de início deve estar no futuro")
                                LocalDateTime inicio,

                                @NotNull(message = "A data de fim é obrigatória")
                                @Future(message = "A data de fim deve estar no futuro")
                                LocalDateTime fim) {
}
