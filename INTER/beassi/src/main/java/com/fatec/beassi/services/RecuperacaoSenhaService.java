package com.fatec.beassi.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.entities.RecuperacaoSenhaEntity;
import com.fatec.beassi.entities.UsuarioEntity;
import com.fatec.beassi.repositories.RecuperacaoSenhaRepository;
import com.fatec.beassi.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecuperacaoSenhaService {

    private static final int MINUTOS_VALIDADE = 30;

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final RecuperacaoSenhaRepository recuperacaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String remetente;


    // SOLICITAR RECUPERAÇÃO DE SENHA

    @Transactional
    public void solicitarRecuperacao(String emailInformado) {

        String email = emailInformado == null
                ? ""
                : emailInformado.trim();

        Optional<UsuarioEntity> usuarioEncontrado =
                usuarioRepository.findByEmailIgnoreCase(email);

        // Não revelar se o e-mail está cadastrado
        if (usuarioEncontrado.isEmpty()) {
            return;
        }

        UsuarioEntity usuario = usuarioEncontrado.get();

        LocalDateTime agora = LocalDateTime.now();

        // Invalidar tokens anteriores ainda não utilizados
        recuperacaoRepository
                .findByUsuarioAndUtilizadoFalse(usuario)
                .forEach(r -> r.setUtilizado(true));

        // Gerar um novo token
        String tokenOriginal = gerarToken();

        RecuperacaoSenhaEntity recuperacao =
                new RecuperacaoSenhaEntity();

        recuperacao.setUsuario(usuario);
        recuperacao.setToken(hashToken(tokenOriginal));
        recuperacao.setDataCriacao(agora);
        recuperacao.setDataExpiracao(
                agora.plusMinutes(MINUTOS_VALIDADE)
        );
        recuperacao.setUtilizado(false);

        recuperacaoRepository.save(recuperacao);

        // Montar o link de recuperação
        String link = frontendUrl.replaceAll("/$", "")
                + "/nova_senha.html?token=" + tokenOriginal;

        // Preparar e enviar o e-mail
        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setFrom(remetente);
        mensagem.setTo(usuario.getEmail());
        mensagem.setSubject(
                "Redefinição de senha - BEASSI Arquitetura"
        );

        mensagem.setText(
                "Olá!\n\n"
                + "Recebemos uma solicitação para redefinir "
                + "a senha da sua conta BEASSI.\n\n"
                + "Acesse o link abaixo para criar uma nova senha. "
                + "Ele é válido por 30 minutos e pode ser utilizado "
                + "apenas uma vez:\n\n"
                + link + "\n\n"
                + "Se você não solicitou a redefinição, ignore "
                + "este e-mail."
        );

        mailSender.send(mensagem);
    }


    // REDEFINIR SENHA

    @Transactional
    public void redefinirSenha(
            String tokenOriginal,
            String novaSenha) {

        String tokenHash = hashToken(tokenOriginal);

        LocalDateTime agora = LocalDateTime.now();

        RecuperacaoSenhaEntity recuperacao =
                recuperacaoRepository
                        .findByTokenAndUtilizadoFalseAndDataExpiracaoAfter(
                                tokenHash,
                                agora
                        )
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Link de recuperação inválido, expirado ou já utilizado."
                        ));

        UsuarioEntity usuario = recuperacao.getUsuario();

        // Criptografar a nova senha
        usuario.setSenha(passwordEncoder.encode(novaSenha));

        usuarioRepository.save(usuario);

        // Marcar o token como utilizado
        recuperacao.setUtilizado(true);

        recuperacaoRepository.save(recuperacao);
    }


    // GERAR TOKEN ALEATÓRIO

    private String gerarToken() {

        byte[] bytes = new byte[32];

        RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }


    // GERAR HASH DO TOKEN

    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            return HexFormat.of().formatHex(
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    )
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Não foi possível processar o token de recuperação.",
                    e
            );
        }
    }
}
