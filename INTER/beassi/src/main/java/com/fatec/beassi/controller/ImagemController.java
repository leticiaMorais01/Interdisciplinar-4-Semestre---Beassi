
package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fatec.beassi.entities.ImagemEntity;
import com.fatec.beassi.services.ImagemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/imagem")
@RequiredArgsConstructor
public class ImagemController {

    private final ImagemService imagemService;

    // LISTAR TODAS AS IMAGENS
    @GetMapping
    public ResponseEntity<List<ImagemEntity>> listarTodos() {

        return ResponseEntity.ok(imagemService.listarTodos());
    }

    // BUSCAR IMAGEM POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ImagemEntity> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(imagemService.buscarPorId(id));
    }

    // CADASTRAR IMAGEM COM UPLOAD
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagemEntity> incluir(

            @RequestParam("arquivo") MultipartFile arquivo,

            @RequestParam("projetoId") Integer projetoId) {

        ImagemEntity nova = imagemService.incluir(
            arquivo,
            projetoId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    // EDITAR IMAGEM E SUBSTITUIR ARQUIVO
    @PutMapping(
        value = "/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ImagemEntity> editar(

            @PathVariable Integer id,

            @RequestParam("arquivo") MultipartFile arquivo,

            @RequestParam("projetoId") Integer projetoId) {

        ImagemEntity atualizada = imagemService.editar(
            id,
            arquivo,
            projetoId
        );

        return ResponseEntity.ok(atualizada);
    }

    // EXCLUIR IMAGEM
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {

        imagemService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
