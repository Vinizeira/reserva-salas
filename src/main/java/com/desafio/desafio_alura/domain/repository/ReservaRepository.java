package com.desafio.desafio_alura.domain.repository;

import com.desafio.desafio_alura.domain.model.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /**
     * Validação do intervalo semiaberto [inicio, fim).
     * Retorna 'true' se houver sobreposição com outra reserva ATIVA.
     */
    @Query("""
        SELECT COUNT(r) > 0 FROM Reserva r
        WHERE r.sala.id = :salaId
        AND r.status = 'ATIVA'
        AND :inicio < r.fim
        AND :fim > r.inicio
    """)
    boolean existeConflitoHorario(
            @Param("salaId") Long salaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    /**
     * Lista reservas filtradas por sala com suporte a paginação.
     * Utiliza 'JOIN FETCH' para carregar 'usuario' e 'sala' em uma única consulta SQL (resolve N+1).
     */
    @Query(
            value = """
            SELECT r FROM Reserva r
            JOIN FETCH r.usuario
            JOIN FETCH r.sala
            WHERE r.sala.id = :salaId
        """,
            countQuery = "SELECT COUNT(r) FROM Reserva r WHERE r.sala.id = :salaId"
    )
    Page<Reserva> findBySalaId(@Param("salaId") Long salaId, Pageable pageable);

    /**
     * Busca reservas dentro de um período com paginação.
     */
    @Query(
            value = """
            SELECT r FROM Reserva r
            JOIN FETCH r.usuario
            JOIN FETCH r.sala
            WHERE r.inicio >= :inicio AND r.fim <= :fim
        """,
            countQuery = "SELECT COUNT(r) FROM Reserva r WHERE r.inicio >= :inicio AND r.fim <= :fim"
    )
    Page<Reserva> findByPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            Pageable pageable
    );
}