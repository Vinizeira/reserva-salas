package com.desafio.desafio_alura.domain.repository;

import com.desafio.desafio_alura.domain.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, Long> {}