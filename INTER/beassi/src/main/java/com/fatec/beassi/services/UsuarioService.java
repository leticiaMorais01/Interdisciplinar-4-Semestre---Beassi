
package com.fatec.beassi.services;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.dto.UsuarioResponseDTO;
import com.fatec.beassi.entities.UsuarioEntity;
import com.fatec.beassi.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // LISTAR TODOS OS USUÁRIOS
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {

        return usuarioRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    // BUSCAR USUÁRIO PELO E-MAIL
    @Transactional(readOnly = true)
    public Optional<UsuarioEntity> buscarPorEmail(String email) {

        return usuarioRepository.findByEmailIgnoreCase(email);
    }

    // NORMALIZAR E-MAIL
    public String normalizarEmail(String email) {

    if (email == null || email.isBlank()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "O e-mail é obrigatório."
        );
    }

    String normalizado = email.trim().toLowerCase(Locale.ROOT);

    if (normalizado.length() > 50) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "O e-mail deve ter no máximo 50 caracteres."
        );
    }

    return normalizado;
}

    // VALIDAR DISPONIBILIDADE DO E-MAIL
    @Transactional(readOnly = true)
    public void validarEmailDisponivel(
            String email,
            Integer idUsuarioIgnorado) {

        usuarioRepository.findByEmailIgnoreCase(email)
                .ifPresent(usuario -> {

                    if (idUsuarioIgnorado == null ||
                                !Objects.equals(usuario.getId_usuario(), idUsuarioIgnorado)) {

                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Já existe um usuário cadastrado com esse e-mail."
                        );
                    }
                });
    }

    // CONVERTER ENTITY PARA RESPONSE DTO
    private UsuarioResponseDTO converterParaDTO(UsuarioEntity usuario) {

        return new UsuarioResponseDTO(
                usuario.getId_usuario(),
                usuario.getEmail()
        );
    }
}