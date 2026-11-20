package com.fatec.beassi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fatec.beassi.dto.ContatoDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContatoService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public void enviarMensagem(ContatoDTO dados) {

        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setFrom(remetente);
        mensagem.setTo("beassiarquitetura@gmail.com");
        mensagem.setReplyTo(dados.getEmail());

        mensagem.setSubject(
            "Novo contato pelo site - BEASSI Arquitetura"
        );

        mensagem.setText(
            "Nova mensagem recebida pelo formulário de contato.\n\n"
            + "Nome: " + dados.getNome() + "\n"
            + "E-mail: " + dados.getEmail() + "\n"
            + "Telefone: " + dados.getTelefone() + "\n\n"
            + "Mensagem:\n"
            + dados.getMensagem()
        );

        mailSender.send(mensagem);
    }
}

