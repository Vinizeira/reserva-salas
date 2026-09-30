package com.desafio.desafio_alura.api.exception;

import com.desafio.desafio_alura.domain.exception.RecursoNaoEncontradoException;
import com.desafio.desafio_alura.domain.exception.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    // 1. Erros de validação nos DTOs (@Valid, @NotBlank, @NotNull, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problema> tratarValidacao(MethodArgumentNotValidException ex) {
        List<Problema.CampoInvalido> campos = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new Problema.CampoInvalido(error.getField(), error.getDefaultMessage()))
                .toList();

        Problema problema = new Problema(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                "Erro de Validação",
                "Um ou mais campos estão inválidos. Preencha corretamente e tente novamente.",
                campos
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problema);
    }

    // 2. Erros de Regra de Negócio (ex: conflito de horário, sala inativa) -> 400 Bad Request
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Problema> tratarRegraNegocio(RegraNegocioException ex) {
        Problema problema = new Problema(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                "Violação de Regra de Negócio",
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problema);
    }

    // 3. Recursos Não Encontrados -> 404 Not Found
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Problema> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        Problema problema = new Problema(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                "Recurso não encontrado",
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problema);
    }
}