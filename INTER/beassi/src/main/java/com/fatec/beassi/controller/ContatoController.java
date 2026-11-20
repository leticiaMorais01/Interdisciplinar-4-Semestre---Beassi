package com.fatec.beassi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.beassi.dto.ContatoDTO;
import com.fatec.beassi.services.ContatoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/contato")
@RequiredArgsConstructor
public class ContatoController {

    private final ContatoService contatoService;

    @PostMapping
    public ResponseEntity<String> enviarMensagem(
            @Valid @RequestBody ContatoDTO dados) {

        contatoService.enviarMensagem(dados);

        return ResponseEntity.ok(
            "Mensagem enviada com sucesso!"
        );
    }
}

