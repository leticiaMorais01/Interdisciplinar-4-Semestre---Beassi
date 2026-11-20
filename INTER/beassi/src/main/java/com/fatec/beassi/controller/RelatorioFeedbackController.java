package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.beassi.dto.RelatorioFeedbackDTO;
import com.fatec.beassi.services.RelatorioFeedbackService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/relatorios/feedbacks")
@RequiredArgsConstructor
public class RelatorioFeedbackController {

    private final RelatorioFeedbackService relatorioFeedbackService;

    @GetMapping
    public ResponseEntity<List<RelatorioFeedbackDTO>> listarRelatorio() {

        return ResponseEntity.ok(
            relatorioFeedbackService.listarRelatorio()
        );
    }
}
