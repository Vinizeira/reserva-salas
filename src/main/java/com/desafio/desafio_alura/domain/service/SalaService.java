package com.desafio.desafio_alura.domain.service;

import com.desafio.desafio_alura.api.v1.dto.SalaRequestDTO;
import com.desafio.desafio_alura.api.v1.dto.SalaResponseDTO;
import com.desafio.desafio_alura.domain.exception.RecursoNaoEncontradoException;
import com.desafio.desafio_alura.domain.model.Sala;
import com.desafio.desafio_alura.domain.repository.SalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalaService {

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @Transactional
    public SalaResponseDTO criar(SalaRequestDTO dto) {
        Sala sala = new Sala(dto.nome(), dto.capacidade(), dto.ativa());
        return toDTO(salaRepository.save(sala));
    }

    @Transactional(readOnly = true)
    public SalaResponseDTO buscarPorId(Long id) {
        return salaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<SalaResponseDTO> listarTodas() {
        return salaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public SalaResponseDTO atualizar(Long id, SalaRequestDTO dto) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada com ID: " + id));

        sala.setCapacidade(dto.capacidade());
        if (dto.ativa()) {
            sala.ativar();
        } else {
            sala.inativar();
        }

        return toDTO(salaRepository.save(sala));
    }

    @Transactional
    public void deletar(Long id) {
        if (!salaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Sala não encontrada com ID: " + id);
        }
        salaRepository.deleteById(id);
    }

    private SalaResponseDTO toDTO(Sala sala) {
        return new SalaResponseDTO(sala.getId(), sala.getNome(), sala.getCapacidade(), sala.isAtiva());
    }
}