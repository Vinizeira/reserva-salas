package com.desafio.desafio_alura.domain.service;

import com.desafio.desafio_alura.api.v1.dto.ReservaRequestDTO;
import com.desafio.desafio_alura.api.v1.dto.ReservaResponseDTO;
import com.desafio.desafio_alura.domain.enums.StatusReserva;
import com.desafio.desafio_alura.domain.exception.RecursoNaoEncontradoException;
import com.desafio.desafio_alura.domain.exception.RegraNegocioException;
import com.desafio.desafio_alura.domain.model.Reserva;
import com.desafio.desafio_alura.domain.model.Sala;
import com.desafio.desafio_alura.domain.model.Usuario;
import com.desafio.desafio_alura.domain.repository.ReservaRepository;
import com.desafio.desafio_alura.domain.repository.SalaRepository;
import com.desafio.desafio_alura.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - ReservaService")
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario usuarioPadrao;
    private Sala salaAtivaPadrao;
    private LocalDateTime inicioPadrao;
    private LocalDateTime fimPadrao;

    @BeforeEach
    void setUp() {
        usuarioPadrao = new Usuario("João Silva", "joao@email.com");
        salaAtivaPadrao = new Sala("Sala A", 10, true);
        inicioPadrao = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        fimPadrao = inicioPadrao.plusHours(2); // 10:00 até 12:00
    }

    @Nested
    @DisplayName("Cenários de Criação de Reserva")
    class CriacaoReservaCenarios {

        @Test
        @DisplayName("Deve criar reserva com sucesso quando todos os dados e horários forem válidos")
        void deveCriarReservaComSucesso() {
            // Arrange
            ReservaRequestDTO dto = new ReservaRequestDTO(1L, 1L, inicioPadrao, fimPadrao);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));
            when(salaRepository.findById(1L)).thenReturn(Optional.of(salaAtivaPadrao));
            when(reservaRepository.existeConflitoHorario(1L, inicioPadrao, fimPadrao)).thenReturn(false);

            Reserva reservaSalva = new Reserva(usuarioPadrao, salaAtivaPadrao, inicioPadrao, fimPadrao);
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaSalva);

            // Act
            ReservaResponseDTO resultado = reservaService.criarReserva(dto);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.nomeUsuario()).isEqualTo("João Silva");
            assertThat(resultado.nomeSala()).isEqualTo("Sala A");
            assertThat(resultado.status()).isEqualTo(StatusReserva.ATIVA);

            verify(reservaRepository, times(1)).save(any(Reserva.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar criar reserva para usuário inexistente")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            ReservaRequestDTO dto = new ReservaRequestDTO(99L, 1L, inicioPadrao, fimPadrao);

            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservaService.criarReserva(dto))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Usuário não encontrado com ID: 99");

            verify(reservaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar criar reserva em uma sala inativa")
        void deveLancarExcecaoQuandoSalaEstiverInativa() {
            Sala salaInativa = new Sala("Sala Inativa", 10, false);
            ReservaRequestDTO dto = new ReservaRequestDTO(1L, 1L, inicioPadrao, fimPadrao);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));
            when(salaRepository.findById(1L)).thenReturn(Optional.of(salaInativa));

            assertThatThrownBy(() -> reservaService.criarReserva(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("Não é permitido criar reservas para salas inativas.");

            verify(reservaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários da Regra de Conflito de Horário")
    class ConflitoHorarioCenarios {

        @Test
        @DisplayName("Deve lançar exceção quando houver conflito de horário com reserva ativa")
        void deveLancarExcecaoQuandoHouverConflitoDeHorario() {
            ReservaRequestDTO dto = new ReservaRequestDTO(1L, 1L, inicioPadrao, fimPadrao);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));
            when(salaRepository.findById(1L)).thenReturn(Optional.of(salaAtivaPadrao));
            when(reservaRepository.existeConflitoHorario(1L, inicioPadrao, fimPadrao)).thenReturn(true);

            assertThatThrownBy(() -> reservaService.criarReserva(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("Já existe uma reserva ATIVA para esta sala no horário solicitado.");

            verify(reservaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve permitir criar reserva contígua (borda do intervalo semiaberto: inicio = fim da anterior)")
        void devePermitirReservaSemConflitoNaBordaDoHorario() {
            // Reserva anterior: 10:00 às 12:00. Nova reserva: 12:00 às 14:00
            LocalDateTime novoInicio = fimPadrao; // 12:00
            LocalDateTime novoFim = novoInicio.plusHours(2); // 14:00

            ReservaRequestDTO dto = new ReservaRequestDTO(1L, 1L, novoInicio, novoFim);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));
            when(salaRepository.findById(1L)).thenReturn(Optional.of(salaAtivaPadrao));
            when(reservaRepository.existeConflitoHorario(1L, novoInicio, novoFim)).thenReturn(false);

            Reserva reservaSalva = new Reserva(usuarioPadrao, salaAtivaPadrao, novoInicio, novoFim);
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaSalva);

            ReservaResponseDTO resultado = reservaService.criarReserva(dto);

            assertThat(resultado).isNotNull();
            assertThat(resultado.inicio()).isEqualTo(novoInicio);
            verify(reservaRepository, times(1)).save(any(Reserva.class));
        }
    }

    @Nested
    @DisplayName("Cenários de Cancelamento de Reserva")
    class CancelamentoReservaCenarios {

        @Test
        @DisplayName("Deve cancelar reserva ativa com sucesso e alterar status para CANCELADA")
        void deveCancelarReservaComSucesso() {
            Reserva reservaAtiva = new Reserva(usuarioPadrao, salaAtivaPadrao, inicioPadrao, fimPadrao);

            when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaAtiva));

            reservaService.cancelarReserva(1L);

            assertThat(reservaAtiva.getStatus()).isEqualTo(StatusReserva.CANCELADA);
            verify(reservaRepository, times(1)).save(reservaAtiva);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar cancelar reserva inexistente")
        void deveLancarExcecaoAoCancelarReservaInexistente() {
            when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservaService.cancelarReserva(99L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Reserva não encontrada com ID: 99");

            verify(reservaRepository, never()).save(any());
        }
    }
}