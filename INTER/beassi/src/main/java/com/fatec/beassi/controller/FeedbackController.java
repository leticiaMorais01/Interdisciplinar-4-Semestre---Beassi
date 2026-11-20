
package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import com.fatec.beassi.services.LoginService.LoginResponse;

import com.fatec.beassi.entities.FeedbackEntity;
import com.fatec.beassi.services.FeedbackService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/Feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    // LISTAR TODOS OS FEEDBACKS
    @GetMapping
    public ResponseEntity<List<FeedbackEntity>> listarTodos() {

        return ResponseEntity.ok(feedbackService.listarTodos());
    }


    // LISTAR FEEDBACKS DO CLIENTE AUTENTICADO
    @GetMapping("/meus")
    public ResponseEntity<List<FeedbackEntity>> listarMeusFeedbacks(
            HttpSession sessao) {

        LoginResponse usuario = (LoginResponse)
            sessao.getAttribute("usuarioAutenticado");

        if (usuario == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Usuário não autenticado."
            );
        }

        if (!"CLIENTE".equalsIgnoreCase(usuario.tipoUsuario())) {
            throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Apenas clientes podem consultar seus próprios feedbacks."
            );
        }

        return ResponseEntity.ok(
            feedbackService.listarPorCliente(usuario.idUsuario())
        );
    }


    // BUSCAR FEEDBACK POR ID
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackEntity> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(feedbackService.buscarPorId(id));
    }

    // CADASTRAR FEEDBACK
    @PostMapping
    public ResponseEntity<FeedbackEntity> incluir(
            @RequestBody FeedbackEntity feedback) {

        FeedbackEntity novo = feedbackService.incluir(feedback);

        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    // EDITAR CONTEÚDO DO FEEDBACK
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackEntity> editar(
            @PathVariable Integer id,
            @RequestBody FeedbackEntity feedback) {

        FeedbackEntity atualizado =
            feedbackService.editar(id, feedback);

        return ResponseEntity.ok(atualizado);
    }

    // ALTERAR STATUS DE APROVAÇÃO
    @PatchMapping("/{id}/status")
    public ResponseEntity<FeedbackEntity> alterarStatus(
            @PathVariable Integer id,
            @RequestParam("status") String status) {

        FeedbackEntity atualizado =
            feedbackService.alterarStatus(id, status);

        return ResponseEntity.ok(atualizado);
    }

    // EXCLUIR FEEDBACK
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Integer id) {

        feedbackService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
