package com.desafio.desafio_alura.api.exception;

import java.time.LocalDateTime;
import java.util.List;

public record Problema(
        Integer status,
        LocalDateTime timestamp,
        String titulo,
        String detalhe,
        List<CampoInvalido> campos
) {
    public record CampoInvalido(String nome, String mensagem) {}
}