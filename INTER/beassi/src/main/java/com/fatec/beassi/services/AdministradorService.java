
package com.fatec.beassi.services;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.dto.AdministradorCadastroDTO;
import com.fatec.beassi.dto.AdministradorAtualizacaoDTO;
import com.fatec.beassi.dto.AdministradorResponseDTO;

import com.fatec.beassi.entities.AdministradorEntity;

import com.fatec.beassi.repositories.AdministradorRepository;
import com.fatec.beassi.repositories.ProjetoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    // LISTAR TODOS OS ADMINISTRADORES
    @Transactional(readOnly = true)
    public List<AdministradorResponseDTO> listarTodos() {

        return administradorRepository.findAll()
            .stream()
            .map(this::converterParaDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public AdministradorResponseDTO buscarPorId(int id) {
        AdministradorEntity administrador = administradorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrador não encontrado."));
        return converterParaDTO(administrador);
    }

    // CADASTRAR ADMINISTRADOR
    @Transactional
    public AdministradorResponseDTO incluir(AdministradorCadastroDTO dto) {

        String email = usuarioService.normalizarEmail(dto.getEmail());

        usuarioService.validarEmailDisponivel(email, null);

        String registro = normalizarRegistroCau(dto.getRegistro_cau());

        validarRegistroCauDisponivel(registro, null);

        AdministradorEntity administrador = new AdministradorEntity();

        administrador.setEmail(email);
        administrador.setSenha(passwordEncoder.encode(dto.getSenha()));
        administrador.setRegistro_cau(registro);

        AdministradorEntity salvo =
            administradorRepository.save(administrador);

        return converterParaDTO(salvo);
    }

    // EDITAR ADMINISTRADOR
    @Transactional
    public AdministradorResponseDTO editar(
            int id,
            AdministradorAtualizacaoDTO dto) {

        AdministradorEntity administrador =
            administradorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Administrador não encontrado."
                ));

        String email = usuarioService.normalizarEmail(dto.getEmail());

        usuarioService.validarEmailDisponivel(email, id);

        String registro = normalizarRegistroCau(dto.getRegistro_cau());

        validarRegistroCauDisponivel(registro, id);

        administrador.setEmail(email);
        administrador.setRegistro_cau(registro);

        AdministradorEntity atualizado =
            administradorRepository.save(administrador);

        return converterParaDTO(atualizado);
    }

    // EXCLUIR ADMINISTRADOR
    @Transactional
    public void excluir(Integer id) {

        AdministradorEntity administrador =
            administradorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Administrador não encontrado."
                ));

        if (projetoRepository.possuiProjetosDoAdministrador(id)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Não é possível excluir este administrador porque existem projetos vinculados a ele."
            );
        }

        administradorRepository.delete(administrador);
    }

    // NORMALIZAR REGISTRO CAU
    private String normalizarRegistroCau(String registro) {

        if (registro == null || registro.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O registro CAU é obrigatório."
            );
        }

        String normalizado = registro.trim().toUpperCase(Locale.ROOT);

        if (normalizado.length() > 20) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O registro CAU deve ter no máximo 20 caracteres."
            );
        }

        return normalizado;
    }

    // VERIFICAR REGISTRO CAU DUPLICADO
    private void validarRegistroCauDisponivel(
            String registro,
            Integer idIgnorado) {

        administradorRepository.buscarPorRegistroCau(registro)
            .ifPresent(administrador -> {

                if (idIgnorado == null ||
                        administrador.getId_usuario() != idIgnorado) {

                    throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Já existe um administrador com esse registro CAU."
                    );
                }
            });
    }

    // CONVERTER ENTITY PARA RESPONSE DTO
    private AdministradorResponseDTO converterParaDTO(
            AdministradorEntity administrador) {

        return new AdministradorResponseDTO(
            administrador.getId_usuario(),
            administrador.getEmail(),
            administrador.getRegistro_cau()
        );
    }
}
