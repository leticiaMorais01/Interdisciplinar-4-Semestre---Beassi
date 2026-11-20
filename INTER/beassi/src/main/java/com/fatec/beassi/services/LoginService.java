package com.fatec.beassi.services;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.repositories.LoginRepository;
import com.fatec.beassi.repositories.LoginRepository.UsuarioLogin;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse autenticar(String email, String senha) {

        // Verifica os dados recebidos
        if (email == null || email.isBlank()
                || senha == null || senha.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "E-mail e senha são obrigatórios."
            );
        }

        // Normaliza o e-mail
        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);

        // Busca o usuário através da procedure SQL
        UsuarioLogin usuario = loginRepository
                .buscarPorEmail(emailNormalizado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "E-mail ou senha inválidos."
                ));

        // Confere a senha digitada com o hash BCrypt
        if (!passwordEncoder.matches(senha, usuario.senhaHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "E-mail ou senha inválidos."
            );
        }

        // Verifica se o perfil é válido
        if (usuario.tipoUsuario() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Perfil de usuário não identificado."
            );
        }

        String tipo = usuario.tipoUsuario();

        // Verifica se o cliente está ativo
        if ("CLIENTE".equalsIgnoreCase(tipo)
                && !Boolean.TRUE.equals(usuario.statusCliente())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta conta está inativa."
            );
        }

        // Verifica se o perfil é cliente ou administrador
        if (!"CLIENTE".equalsIgnoreCase(tipo)
                && !"ADMINISTRADOR".equalsIgnoreCase(tipo)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Perfil de usuário inválido."
            );
        }

        // Retorna somente dados seguros, sem o hash da senha
        return new LoginResponse(
                usuario.idUsuario(),
                usuario.email(),
                tipo.toUpperCase(Locale.ROOT),
                usuario.nome(),
                usuario.registroCau()
        );
    }

    // Dados devolvidos após autenticação
    public record LoginResponse(
            Integer idUsuario,
            String email,
            String tipoUsuario,
            String nome,
            String registroCau
    ) {}
}

