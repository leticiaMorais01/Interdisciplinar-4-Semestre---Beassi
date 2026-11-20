
package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.beassi.dto.RelatorioProjetoDTO;
import com.fatec.beassi.services.RelatorioProjetoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/relatorios/projetos")
@RequiredArgsConstructor
public class RelatorioProjetoController {

    private final RelatorioProjetoService relatorioProjetoService;

    @GetMapping
    public ResponseEntity<List<RelatorioProjetoDTO>> listarRelatorio() {

        return ResponseEntity.ok(
            relatorioProjetoService.listarRelatorio()
        );
    }
}
