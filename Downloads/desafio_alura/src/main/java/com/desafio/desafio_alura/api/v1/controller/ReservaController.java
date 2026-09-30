package com.desafio.desafio_alura.api.v1.controller;

import com.desafio.desafio_alura.api.v1.dto.ReservaRequestDTO;
import com.desafio.desafio_alura.api.v1.dto.ReservaResponseDTO;
import com.desafio.desafio_alura.domain.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> criar(@Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.criarReserva(dto));
    }

    @GetMapping("/sala/{salaId}")
    public ResponseEntity<Page<ReservaResponseDTO>> listarPorSala(
            @PathVariable Long salaId,
            @PageableDefault(size = 10, sort = "inicio") Pageable pageable) {
        return ResponseEntity.ok(reservaService.listarPorSala(salaId, pageable));
    }

    @GetMapping("/periodo")
    public ResponseEntity<Page<ReservaResponseDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @PageableDefault(size = 10, sort = "inicio") Pageable pageable) {
        return ResponseEntity.ok(reservaService.listarPorPeriodo(inicio, fim, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }
}