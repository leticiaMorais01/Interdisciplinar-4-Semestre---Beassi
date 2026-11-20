
package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.dto.AdministradorCadastroDTO;
import com.fatec.beassi.dto.AdministradorAtualizacaoDTO;
import com.fatec.beassi.dto.AdministradorResponseDTO;
import com.fatec.beassi.services.AdministradorService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.fatec.beassi.services.LoginService;

@RestController
@RequestMapping("/Administrador")
@RequiredArgsConstructor
@Validated
public class AdministradorController {

    private final AdministradorService administradorService;

    private void validarAdministrador(HttpServletRequest request) {

        HttpSession sessao = request.getSession(false);

        if (sessao == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        Object usuario = sessao.getAttribute("usuarioAutenticado");

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        LoginService.LoginResponse autenticado =
                (LoginService.LoginResponse) usuario;

        if (!"ADMINISTRADOR".equals(autenticado.tipoUsuario())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acesso permitido somente para administradores."
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<AdministradorResponseDTO>> listarTodos(
            HttpServletRequest request) {

        validarAdministrador(request);

        List<AdministradorResponseDTO> lista =
                administradorService.listarTodos();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponseDTO> buscarPorId(
            @PathVariable int id, HttpServletRequest request) {
        validarAdministrador(request);
        return ResponseEntity.ok(administradorService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AdministradorResponseDTO> incluir(
            @Valid @RequestBody AdministradorCadastroDTO administradorDTO,
            HttpServletRequest request) {

        validarAdministrador(request);

        AdministradorResponseDTO novo =
                administradorService.incluir(administradorDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorResponseDTO> editar(
            @PathVariable int id,
            @Valid @RequestBody AdministradorAtualizacaoDTO administradorDTO,
            HttpServletRequest request) {

        validarAdministrador(request);

        AdministradorResponseDTO atualizado =
                administradorService.editar(id, administradorDTO);

        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable int id,
            HttpServletRequest request) {

        validarAdministrador(request);

        administradorService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
