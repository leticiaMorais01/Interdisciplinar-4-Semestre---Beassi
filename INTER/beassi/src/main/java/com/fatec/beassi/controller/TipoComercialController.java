
package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fatec.beassi.entities.TipoComercialEntity;
import com.fatec.beassi.services.TipoComercialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tipoComercial")
@RequiredArgsConstructor
public class TipoComercialController {

    private final TipoComercialService tipoComercialService;

    @GetMapping
    public ResponseEntity<List<TipoComercialEntity>> listarTodos() {
        return ResponseEntity.ok(tipoComercialService.listarTodos());
    }

    // LISTAR SOMENTE TIPOS COMERCIAIS ATIVOS
    @GetMapping("/ativos")
    public ResponseEntity<List<TipoComercialEntity>> listarAtivos() {

        return ResponseEntity.ok(tipoComercialService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoComercialEntity> buscarPorId(@PathVariable int id) {

        TipoComercialEntity tipoComercial =
                tipoComercialService.buscarPorId(id);

        return ResponseEntity.ok(tipoComercial);
    }

    @PostMapping
    public ResponseEntity<TipoComercialEntity> incluir(
            @RequestBody TipoComercialEntity tipoComercial) {

        TipoComercialEntity novo = tipoComercialService.incluir(tipoComercial);

        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoComercialEntity> editar(
            @PathVariable int id,
            @RequestBody TipoComercialEntity tipoComercial) {

        TipoComercialEntity atualizado =
                tipoComercialService.editar(id, tipoComercial);

        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {

        tipoComercialService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    // REATIVAR TIPO COMERCIAL
    @PutMapping("/{id}/reativar")
    public ResponseEntity<TipoComercialEntity> reativar(
            @PathVariable Integer id) {

        TipoComercialEntity reativado =
                tipoComercialService.reativar(id);

        return ResponseEntity.ok(reativado);
    }


}
