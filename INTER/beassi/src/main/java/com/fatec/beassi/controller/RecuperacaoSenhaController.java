
package com.fatec.beassi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.beassi.dto.SolicitarRecuperacaoDTO;
import com.fatec.beassi.dto.RedefinirSenhaDTO;
import com.fatec.beassi.services.RecuperacaoSenhaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recuperacao-senha")
@RequiredArgsConstructor
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService recuperacaoSenhaService;

    // SOLICITAR RECUPERAÇÃO DE SENHA
    @PostMapping("/solicitar")
    public ResponseEntity<String> solicitarRecuperacao(
            @RequestBody SolicitarRecuperacaoDTO dados) {

        recuperacaoSenhaService.solicitarRecuperacao(
                dados.getEmail()
        );

        return ResponseEntity.ok(
                "Se o e-mail estiver cadastrado, você receberá um link de recuperação."
        );
    }

    // REDEFINIR SENHA
    @PostMapping("/redefinir")
    public ResponseEntity<String> redefinirSenha(
            @RequestBody RedefinirSenhaDTO dados) {

        recuperacaoSenhaService.redefinirSenha(
                dados.getToken(),
                dados.getNovaSenha()
        );

        return ResponseEntity.ok(
                "Senha redefinida com sucesso!"
        );
    }
}
