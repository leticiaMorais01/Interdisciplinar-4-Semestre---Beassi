
package com.fatec.beassi.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.dto.ProjetosPorClienteDTO;
import com.fatec.beassi.services.LoginService.LoginResponse;
import com.fatec.beassi.services.ProjetosPorClienteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/funcoes/clientes")
@RequiredArgsConstructor
public class ProjetosPorClienteController {

    private final ProjetosPorClienteService projetosPorClienteService;

    @GetMapping("/{id}/projetos")
    public ResponseEntity<List<ProjetosPorClienteDTO>> listarPorCliente(
            @PathVariable Integer id,
            HttpSession sessao) {

        // Recupera o usuário autenticado na sessão
        LoginResponse usuario = (LoginResponse)
                sessao.getAttribute("usuarioAutenticado");

        // Verifica se existe usuário autenticado
        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        // Clientes só podem consultar os próprios projetos
        if ("CLIENTE".equals(usuario.tipoUsuario())
                && !usuario.idUsuario().equals(id)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para acessar os projetos de outro cliente."
            );
        }

        // Administradores podem consultar projetos de qualquer cliente
        if (!"CLIENTE".equals(usuario.tipoUsuario())
                && !"ADMINISTRADOR".equals(usuario.tipoUsuario())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Perfil sem permissão para acessar projetos."
            );
        }

        return ResponseEntity.ok(
                projetosPorClienteService.listarPorCliente(id)
        );
    }
}
