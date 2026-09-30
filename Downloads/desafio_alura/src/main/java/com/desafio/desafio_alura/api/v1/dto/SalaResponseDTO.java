package com.desafio.desafio_alura.api.v1.dto;

public record SalaResponseDTO(
        Long id,
        String nome,
        Integer capacidade,
        Boolean ativa
) {}