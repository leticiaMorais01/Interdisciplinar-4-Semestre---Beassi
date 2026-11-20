package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.beassi.dto.RelatorioClienteDTO;
import com.fatec.beassi.services.RelatorioClienteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/relatorios/clientes")
@RequiredArgsConstructor
public class RelatorioClienteController {

    private final RelatorioClienteService relatorioClienteService;

    @GetMapping
    public ResponseEntity<List<RelatorioClienteDTO>> listarRelatorio() {

        return ResponseEntity.ok(
            relatorioClienteService.listarRelatorio()
        );
    }
}
