package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.beassi.dto.ResumoFeedbackProjetoDTO;
import com.fatec.beassi.services.ResumoFeedbackProjetoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/funcoes/projetos")
@RequiredArgsConstructor
public class ResumoFeedbackProjetoController {

    private final ResumoFeedbackProjetoService resumoFeedbackProjetoService;

    @GetMapping("/resumo-feedbacks")
    public ResponseEntity<List<ResumoFeedbackProjetoDTO>> listarResumo() {

        return ResponseEntity.ok(
            resumoFeedbackProjetoService.listarResumo()
        );
    }
}

