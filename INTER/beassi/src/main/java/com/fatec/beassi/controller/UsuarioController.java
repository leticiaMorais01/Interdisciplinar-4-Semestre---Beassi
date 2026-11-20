package com.fatec.beassi.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fatec.beassi.dto.UsuarioResponseDTO;
import com.fatec.beassi.services.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // LISTAR TODOS OS USUÁRIOS
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {

        List<UsuarioResponseDTO> lista =
            usuarioService.listarTodos();

        return ResponseEntity.ok(lista);
    }

}
