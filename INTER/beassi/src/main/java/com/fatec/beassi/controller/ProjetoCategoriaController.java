
package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fatec.beassi.entities.ProjetoCategoriaEntity;
import com.fatec.beassi.services.ProjetoCategoriaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projetoCategoria")
@RequiredArgsConstructor
public class ProjetoCategoriaController {

    private final ProjetoCategoriaService projetoCategoriaService;

    // LISTAR TODAS AS ASSOCIAÇÕES
    @GetMapping
    public ResponseEntity<List<ProjetoCategoriaEntity>> listarTodos() {

        return ResponseEntity.ok(
            projetoCategoriaService.listarTodos()
        );
    }

    @GetMapping("/{projetoId}/{categoriaId}")
public ResponseEntity<ProjetoCategoriaEntity> buscarPorId(
        @PathVariable Integer projetoId,
        @PathVariable Integer categoriaId) {

    return ResponseEntity.ok(
        projetoCategoriaService.buscarPorId(
            projetoId, categoriaId
        )
    );
}

    // INCLUIR ASSOCIAÇÃO
    @PostMapping
    public ResponseEntity<ProjetoCategoriaEntity> incluir(
            @RequestBody ProjetoCategoriaEntity projetoCategoria) {

        ProjetoCategoriaEntity novaAssociacao =
            projetoCategoriaService.incluir(projetoCategoria);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(novaAssociacao);
    }

    // EDITAR ASSOCIAÇÃO
    @PutMapping("/{projetoId}/{categoriaId}")
    public ResponseEntity<ProjetoCategoriaEntity> editar(
            @PathVariable Integer projetoId,
            @PathVariable Integer categoriaId,
            @RequestBody ProjetoCategoriaEntity projetoCategoria) {

        ProjetoCategoriaEntity atualizada =
            projetoCategoriaService.editar(
                projetoId,
                categoriaId,
                projetoCategoria
            );

        return ResponseEntity.ok(atualizada);
    }

    // EXCLUIR ASSOCIAÇÃO
    @DeleteMapping("/{projetoId}/{categoriaId}")
    public ResponseEntity<Void> excluir(
            @PathVariable Integer projetoId,
            @PathVariable Integer categoriaId) {

        projetoCategoriaService.excluir(projetoId, categoriaId);

        return ResponseEntity.noContent().build();
    }
}
