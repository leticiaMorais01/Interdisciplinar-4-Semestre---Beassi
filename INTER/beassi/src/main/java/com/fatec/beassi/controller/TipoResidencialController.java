
package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fatec.beassi.entities.TipoResidencialEntity;
import com.fatec.beassi.services.TipoResidencialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tipoResidencial")
@RequiredArgsConstructor
public class TipoResidencialController {

    private final TipoResidencialService tipoResidencialService;

    @GetMapping
    public ResponseEntity<List<TipoResidencialEntity>> listarTodos() {
        return ResponseEntity.ok(tipoResidencialService.listarTodos());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<TipoResidencialEntity>> listarAtivos() {

        return ResponseEntity.ok(tipoResidencialService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoResidencialEntity> buscarPorId(@PathVariable int id) {

        TipoResidencialEntity tipoResidencial =
                tipoResidencialService.buscarPorId(id);

        return ResponseEntity.ok(tipoResidencial);
    }

    @PostMapping
    public ResponseEntity<TipoResidencialEntity> incluir(
            @RequestBody TipoResidencialEntity tipoResidencial) {

        TipoResidencialEntity novo = tipoResidencialService.incluir(tipoResidencial);

        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoResidencialEntity> editar(
            @PathVariable int id,
            @RequestBody TipoResidencialEntity tipoResidencial) {

        TipoResidencialEntity atualizado =
                tipoResidencialService.editar(id, tipoResidencial);

        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {

        tipoResidencialService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    // REATIVAR TIPO RESIDENCIAL
    @PutMapping("/{id}/reativar")
    public ResponseEntity<TipoResidencialEntity> reativar(
            @PathVariable Integer id) {

        TipoResidencialEntity reativado =
                tipoResidencialService.reativar(id);

        return ResponseEntity.ok(reativado);
    }
}
