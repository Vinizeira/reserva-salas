package com.desafio.desafio_alura.domain.service;

import com.desafio.desafio_alura.api.v1.dto.ReservaRequestDTO;
import com.desafio.desafio_alura.api.v1.dto.ReservaResponseDTO;
import com.desafio.desafio_alura.domain.exception.RecursoNaoEncontradoException;
import com.desafio.desafio_alura.domain.exception.RegraNegocioException;
import com.desafio.desafio_alura.domain.model.Reserva;
import com.desafio.desafio_alura.domain.model.Sala;
import com.desafio.desafio_alura.domain.model.Usuario;
import com.desafio.desafio_alura.domain.repository.ReservaRepository;
import com.desafio.desafio_alura.domain.repository.SalaRepository;
import com.desafio.desafio_alura.domain.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          SalaRepository salaRepository,
                          UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ReservaResponseDTO criarReserva(ReservaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + dto.usuarioId()));

        Sala sala = salaRepository.findById(dto.salaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada com ID: " + dto.salaId()));

        if (!sala.isAtiva()) {
            throw new RegraNegocioException("Não é permitido criar reservas para salas inativas.");
        }

        boolean conflito = reservaRepository.existeConflitoHorario(dto.salaId(), dto.inicio(), dto.fim());
        if (conflito) {
            throw new RegraNegocioException("Já existe uma reserva ATIVA para esta sala no horário solicitado.");
        }

        Reserva reserva = new Reserva(usuario, sala, dto.inicio(), dto.fim());
        return toDTO(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public Page<ReservaResponseDTO> listarPorSala(Long salaId, Pageable pageable) {
        return reservaRepository.findBySalaId(salaId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ReservaResponseDTO> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        return reservaRepository.findByPeriodo(inicio, fim, pageable).map(this::toDTO);
    }

    @Transactional
    public void cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada com ID: " + id));

        reserva.cancelar();
        reservaRepository.save(reserva);
    }

    private ReservaResponseDTO toDTO(Reserva r) {
        return new ReservaResponseDTO(
                r.getId(),
                r.getUsuario().getId(),
                r.getUsuario().getNome(),
                r.getSala().getId(),
                r.getSala().getNome(),
                r.getInicio(),
                r.getFim(),
                r.getStatus()
        );
    }
}