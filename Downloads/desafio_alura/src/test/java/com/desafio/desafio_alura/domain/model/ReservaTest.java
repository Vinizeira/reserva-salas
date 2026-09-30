package com.desafio.desafio_alura.domain.model;

import com.desafio.desafio_alura.domain.enums.StatusReserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes do Modelo de Domínio - Reserva")
class ReservaTest {

    private Usuario usuarioValido;
    private Sala salaAtiva;
    private LocalDateTime inicioPadrao;
    private LocalDateTime fimPadrao;

    @BeforeEach
    void setUp() {
        usuarioValido = new Usuario("Maria Silva", "maria@email.com");
        salaAtiva = new Sala("Sala de Reunião A", 10, true);
        inicioPadrao = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        fimPadrao = inicioPadrao.plusHours(2); // 10:00 às 12:00
    }

    @Nested
    @DisplayName("Validações na Criação da Reserva")
    class ValidacoesCriacao {

        @Test
        @DisplayName("Deve criar reserva com status ATIVA quando os parâmetros forem válidos")
        void deveCriarReservaComSucesso() {
            Reserva reserva = new Reserva(usuarioValido, salaAtiva, inicioPadrao, fimPadrao);

            assertThat(reserva).isNotNull();
            assertThat(reserva.getUsuario()).isEqualTo(usuarioValido);
            assertThat(reserva.getSala()).isEqualTo(salaAtiva);
            assertThat(reserva.getInicio()).isEqualTo(inicioPadrao);
            assertThat(reserva.getFim()).isEqualTo(fimPadrao);
            assertThat(reserva.getStatus()).isEqualTo(StatusReserva.ATIVA);
        }

        @Test
        @DisplayName("Não deve permitir criar reserva sem Usuário")
        void naoDevePermitirUsuarioNulo() {
            assertThatThrownBy(() -> new Reserva(null, salaAtiva, inicioPadrao, fimPadrao))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Usuário é obrigatório.");
        }

        @Test
        @DisplayName("Não deve permitir criar reserva sem Sala")
        void naoDevePermitirSalaNula() {
            assertThatThrownBy(() -> new Reserva(usuarioValido, null, inicioPadrao, fimPadrao))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sala é obrigatória.");
        }

        @Test
        @DisplayName("Não deve permitir criar reserva em Sala inativa")
        void naoDevePermitirSalaInativa() {
            Sala salaInativa = new Sala("Sala B", 5, false);

            assertThatThrownBy(() -> new Reserva(usuarioValido, salaInativa, inicioPadrao, fimPadrao))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("A sala precisa estar ativa para ser reservada.");
        }

        @Test
        @DisplayName("Não deve permitir criar reserva com data final anterior ao início")
        void naoDevePermitirDataFimAnteriorAoInicio() {
            LocalDateTime fimInvalido = inicioPadrao.minusHours(1);

            assertThatThrownBy(() -> new Reserva(usuarioValido, salaAtiva, inicioPadrao, fimInvalido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("A data final deve ser posterior à data inicial.");
        }

        @Test
        @DisplayName("Não deve permitir criar reserva com data final igual ao início")
        void naoDevePermitirDataFimIgualAoInicio() {
            assertThatThrownBy(() -> new Reserva(usuarioValido, salaAtiva, inicioPadrao, inicioPadrao))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("A data final deve ser posterior à data inicial.");
        }
    }

    @Nested
    @DisplayName("Regra de Intervalo Semiaberto e Sobreposição")
    class SobreposicaoHorarios {

        @Test
        @DisplayName("Deve detectar sobreposição total de horário em reserva ATIVA")
        void deveDetectarSobreposicaoTotal() {
            Reserva reserva = new Reserva(usuarioValido, salaAtiva, inicioPadrao, fimPadrao); // 10:00 às 12:00

            boolean sobrepoe = reserva.sobrepoeCom(inicioPadrao, fimPadrao);

            assertThat(sobrepoe).isTrue();
        }

        @Test
        @DisplayName("Deve detectar sobreposição parcial (novo horário começa durante a reserva existente)")
        void deveDetectarSobreposicaoParcialInicio() {
            Reserva reserva = new Reserva(usuarioValido, salaAtiva, inicioPadrao, fimPadrao); // 10:00 às 12:00
            LocalDateTime novoInicio = inicioPadrao.plusHours(1); // 11:00
            LocalDateTime novoFim = novoInicio.plusHours(2); // 13:00

            boolean sobrepoe = reserva.sobrepoeCom(novoInicio, novoFim);

            assertThat(sobrepoe).isTrue();
        }

        @Test
        @DisplayName("Não deve indicar sobreposição na borda do intervalo semiaberto [inicio, fim)")
        void naoDeveDetectarSobreposicaoNaBorda() {
            Reserva reserva = new Reserva(usuarioValido, salaAtiva, inicioPadrao, fimPadrao); // 10:00 às 12:00
            LocalDateTime novoInicio = fimPadrao; // 12:00
            LocalDateTime novoFim = novoInicio.plusHours(2); // 14:00

            boolean sobrepoe = reserva.sobrepoeCom(novoInicio, novoFim);

            assertThat(sobrepoe).isFalse();
        }

        @Test
        @DisplayName("Reserva CANCELADA não deve indicar sobreposição de horário")
        void reservaCanceladaNaoDeveIndicarSobreposicao() {
            Reserva reserva = new Reserva(usuarioValido, salaAtiva, inicioPadrao, fimPadrao);
            reserva.cancelar();

            boolean sobrepoe = reserva.sobrepoeCom(inicioPadrao, fimPadrao);

            assertThat(sobrepoe).isFalse();
            assertThat(reserva.getStatus()).isEqualTo(StatusReserva.CANCELADA);
        }
    }

    @Nested
    @DisplayName("Ciclo de Vida e Cancelamento")
    class CancelamentoReserva {

        @Test
        @DisplayName("Deve alterar o status para CANCELADA com sucesso")
        void deveCancelarReservaAtiva() {
            Reserva reserva = new Reserva(usuarioValido, salaAtiva, inicioPadrao, fimPadrao);

            reserva.cancelar();

            assertThat(reserva.getStatus()).isEqualTo(StatusReserva.CANCELADA);
        }

        @Test
        @DisplayName("Não deve permitir cancelar uma reserva que já está cancelada")
        void naoDeveCancelarReservaJaCancelada() {
            Reserva reserva = new Reserva(usuarioValido, salaAtiva, inicioPadrao, fimPadrao);
            reserva.cancelar();

            assertThatThrownBy(reserva::cancelar)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Reserva já se encontra cancelada.");
        }
    }
}