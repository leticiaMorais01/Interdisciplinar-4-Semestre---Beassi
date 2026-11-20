
package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.beassi.entities.CategoriaEntity;
import com.fatec.beassi.services.CategoriaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // LISTAR TODAS AS CATEGORIAS
    @GetMapping
    public ResponseEntity<List<CategoriaEntity>> listarTodos() {

        return ResponseEntity.ok(categoriaService.listarTodos());
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<CategoriaEntity>> listarAtivas() {

        return ResponseEntity.ok(categoriaService.listarAtivas());
    }

    // BUSCAR CATEGORIA POR ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaEntity> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    // CADASTRAR CATEGORIA
    @PostMapping
    public ResponseEntity<CategoriaEntity> incluir(
            @RequestBody CategoriaEntity categoria) {

        CategoriaEntity nova = categoriaService.incluir(categoria);

        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    // EDITAR CATEGORIA
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaEntity> editar(
            @PathVariable int id,
            @RequestBody CategoriaEntity categoria) {

        CategoriaEntity atualizada =
                categoriaService.editar(id, categoria);

        return ResponseEntity.ok(atualizada);
    }

    // EXCLUIR CATEGORIA
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {

        categoriaService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    // REATIVAR CATEGORIA
    @PutMapping("/{id}/reativar")
    public ResponseEntity<CategoriaEntity> reativar(
            @PathVariable Integer id) {

        CategoriaEntity reativada = categoriaService.reativar(id);

        return ResponseEntity.ok(reativada);
    }
}
