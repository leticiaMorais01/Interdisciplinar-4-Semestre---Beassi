
package com.fatec.beassi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.dto.ClienteCadastroDTO;
import com.fatec.beassi.dto.ClienteResponseDTO;
import com.fatec.beassi.dto.ClienteAtualizacaoDTO;
import com.fatec.beassi.services.ClienteService;
import com.fatec.beassi.services.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/Cliente")
@RequiredArgsConstructor
@Validated
public class ClienteController {

    private final ClienteService clienteService;

    private void validarAdministrador(HttpServletRequest request) {

        HttpSession sessao = request.getSession(false);

        if (sessao == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        Object usuario = sessao.getAttribute("usuarioAutenticado");

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        LoginService.LoginResponse autenticado =
                (LoginService.LoginResponse) usuario;

        if (!"ADMINISTRADOR".equals(autenticado.tipoUsuario())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acesso permitido somente para administradores."
            );
        }
    }

    private void validarAcessoCliente(HttpServletRequest request, int idCliente) {
        HttpSession sessao = request.getSession(false);
        Object usuario = sessao == null ? null : sessao.getAttribute("usuarioAutenticado");
        if (usuario == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        LoginService.LoginResponse autenticado = (LoginService.LoginResponse) usuario;
        boolean administrador = "ADMINISTRADOR".equals(autenticado.tipoUsuario());
        boolean proprioCliente = "CLIENTE".equals(autenticado.tipoUsuario()) && autenticado.idUsuario() == idCliente;
        if (!administrador && !proprioCliente) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este cadastro.");
    }

    // LISTAR TODOS OS CLIENTES
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos(
            HttpServletRequest request) {

        validarAdministrador(request);

        List<ClienteResponseDTO> lista =
                clienteService.listarTodos();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(
            @PathVariable int id, HttpServletRequest request) {
        validarAcessoCliente(request, id);
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    // CADASTRAR CLIENTE
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> incluir(
            @Valid @RequestBody ClienteCadastroDTO clienteDTO) {

        ClienteResponseDTO novo =
                clienteService.incluir(clienteDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novo);
    }

    // EDITAR CLIENTE
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> editar(
            @PathVariable int id,
            @Valid @RequestBody ClienteAtualizacaoDTO clienteDTO,
            HttpServletRequest request) {

        validarAcessoCliente(request, id);

        ClienteResponseDTO atualizado =
                clienteService.editar(id, clienteDTO);

        return ResponseEntity.ok(atualizado);
    }


    // CLIENTE INATIVA A PRÓPRIA CONTA
    @PostMapping("/minha-conta/inativar")
    public ResponseEntity<Void> inativarMinhaConta(
            HttpServletRequest request) {

        HttpSession sessao = request.getSession(false);

        if (sessao == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Usuário não autenticado."
            );
        }

        Object usuario = sessao.getAttribute("usuarioAutenticado");

        if (usuario == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Usuário não autenticado."
            );
        }

        LoginService.LoginResponse autenticado =
            (LoginService.LoginResponse) usuario;

        if (!"CLIENTE".equals(autenticado.tipoUsuario())) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Apenas clientes podem inativar a própria conta."
            );
        }

        clienteService.inativar(autenticado.idUsuario());

        // Encerra imediatamente a sessão
        sessao.invalidate();

        return ResponseEntity.noContent().build();
    }

    // ADMINISTRADOR REATIVA CLIENTE
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(
            @PathVariable int id,
            HttpServletRequest request) {

        validarAdministrador(request);

        clienteService.reativar(id);

        return ResponseEntity.noContent().build();
    }
    
}
