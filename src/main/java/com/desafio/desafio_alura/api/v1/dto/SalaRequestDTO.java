package com.desafio.desafio_alura.api.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SalaRequestDTO(
        @NotBlank(message = "O nome da sala é obrigatório")
        String nome,

        @NotNull(message = "A capacidade é obrigatória")
        @Positive(message = "A capacidade deve ser positiva")
        Integer capacidade,

        @NotNull(message = "O status de ativação é obrigatório")
        Boolean ativa
) {}