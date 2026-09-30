package com.desafio.desafio_alura.domain.service;

import com.desafio.desafio_alura.api.v1.dto.UsuarioRequestDTO;
import com.desafio.desafio_alura.api.v1.dto.UsuarioResponseDTO;
import com.desafio.desafio_alura.domain.exception.RecursoNaoEncontradoException;
import com.desafio.desafio_alura.domain.exception.RegraNegocioException;
import com.desafio.desafio_alura.domain.model.Usuario;
import com.desafio.desafio_alura.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }
        Usuario usuario = new Usuario(dto.nome(), dto.email());
        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    private UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}