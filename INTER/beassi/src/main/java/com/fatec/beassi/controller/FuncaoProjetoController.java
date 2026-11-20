
package com.fatec.beassi.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.beassi.services.FuncaoProjetoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/funcoes/projetos")
@RequiredArgsConstructor
public class FuncaoProjetoController {

    private final FuncaoProjetoService funcaoProjetoService;

    @GetMapping("/{id}/percentual-positivas")
    public ResponseEntity<BigDecimal> calcularPercentualAvaliacoesPositivas(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
            funcaoProjetoService.calcularPercentualAvaliacoesPositivas(id)
        );
    }
}
