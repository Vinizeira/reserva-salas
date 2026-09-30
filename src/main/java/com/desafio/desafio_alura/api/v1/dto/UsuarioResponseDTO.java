package com.desafio.desafio_alura.api.v1.dto;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email
) {}