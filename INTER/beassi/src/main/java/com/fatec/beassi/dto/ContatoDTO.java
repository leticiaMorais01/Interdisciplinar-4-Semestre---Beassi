package com.fatec.beassi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class ContatoDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100)
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    private String email;

    @Size(max = 15)
    private String telefone;

    @NotBlank(message = "A mensagem é obrigatória.")
    @Size(min = 10, max = 2000)
    private String mensagem;
}
