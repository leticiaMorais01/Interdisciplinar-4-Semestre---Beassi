
package com.fatec.beassi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.services.LoginService;
import com.fatec.beassi.services.LoginService.LoginResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/Login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<LoginResponse> autenticar(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        // Valida e autentica o usuário
        LoginResponse resposta = loginService.autenticar(
                request.email(),
                request.senha()
        );

        // Encerra uma sessão anterior, se existir
        HttpSession sessaoAnterior = httpRequest.getSession(false);

        if (sessaoAnterior != null) {
            sessaoAnterior.invalidate();
        }

        // Cria uma nova sessão após o login
        HttpSession sessao = httpRequest.getSession(true);

        // Guarda os dados do usuário autenticado
        sessao.setAttribute("usuarioAutenticado", resposta);

        // Tempo máximo de inatividade: 30 minutos
        sessao.setMaxInactiveInterval(30 * 60);

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/sessao")
    public ResponseEntity<LoginResponse> consultarSessao(
            HttpSession sessao) {

        LoginResponse usuario = (LoginResponse)
                sessao.getAttribute("usuarioAutenticado");

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession sessao) {

        sessao.invalidate();

        return ResponseEntity.noContent().build();
    }

    public record LoginRequest(
            String email,
            String senha
    ) {}
}
