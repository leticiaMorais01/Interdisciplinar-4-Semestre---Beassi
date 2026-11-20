
package com.fatec.beassi.controller;

import java.util.List;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.entities.ProjetoEntity;
import com.fatec.beassi.services.LoginService;
import com.fatec.beassi.services.ProjetoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projeto")
@RequiredArgsConstructor
public class ProjetoController {

    public record AtualizarEtapaRequest(
    String etapa_atual,
    Integer porcentagem_conclusao
    ) {}

    private final ProjetoService projetoService;
    private final ObjectMapper objectMapper;

    // VERIFICAR SE O USUÁRIO É ADMINISTRADOR
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

    // LISTAR TODOS OS PROJETOS - PÚBLICO
    @GetMapping
    public ResponseEntity<List<ProjetoEntity>> listarTodos() {

        return ResponseEntity.ok(projetoService.listarTodos());
    }

    // BUSCAR PROJETO POR ID - PÚBLICO
    @GetMapping("/{id}")
    public ResponseEntity<ProjetoEntity> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(projetoService.buscarPorId(id));
    }

    // CADASTRAR PROJETO - SOMENTE ADMINISTRADOR
    @PostMapping
    public ResponseEntity<ProjetoEntity> incluir(
            @RequestBody(required = false) String corpo,
            HttpServletRequest request) {

        validarAdministrador(request);

        if (corpo == null || corpo.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados do projeto são obrigatórios."
            );
        }

        try {
            ProjetoEntity projeto =
                    objectMapper.readValue(corpo, ProjetoEntity.class);

            ProjetoEntity novo = projetoService.incluir(projeto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(novo);

        } catch (JacksonException e) {
            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O JSON enviado é inválido."
            );
        }
    }

    // EDITAR PROJETO - SOMENTE ADMINISTRADOR
    @PutMapping("/{id}")
    public ResponseEntity<ProjetoEntity> editar(
            @PathVariable Integer id,
            @RequestBody(required = false) String corpo,
            HttpServletRequest request) {

        validarAdministrador(request);

        if (corpo == null || corpo.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados do projeto são obrigatórios."
            );
        }

        try {
            ProjetoEntity projeto =
                    objectMapper.readValue(corpo, ProjetoEntity.class);

            ProjetoEntity atualizado =
                    projetoService.editar(id, projeto);

            return ResponseEntity.ok(atualizado);

        } catch (JacksonException e) {
            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O JSON enviado é inválido."
    );
}
    }

    // ATUALIZAR ETAPA DO PROJETO - SOMENTE ADMINISTRADOR
@PutMapping("/{id}/etapa")
public ResponseEntity<ProjetoEntity> atualizarEtapa(
        @PathVariable Integer id,
        @RequestBody AtualizarEtapaRequest dados,
        HttpServletRequest request) {

    validarAdministrador(request);

    ProjetoEntity atualizado = projetoService.atualizarEtapa(
        id,
        dados.etapa_atual(),
        dados.porcentagem_conclusao()
    );

    return ResponseEntity.ok(atualizado);
}

    // REATIVAR PROJETO - SOMENTE ADMINISTRADOR
    @PutMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(
            @PathVariable Integer id,
            HttpServletRequest request) {

        validarAdministrador(request);

        projetoService.reativar(id);

        return ResponseEntity.noContent().build();
    }

    // EXCLUIR PROJETO - SOMENTE ADMINISTRADOR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Integer id,
            HttpServletRequest request) {

        validarAdministrador(request);

        projetoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
