package com.fatec.beassi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteAtualizacaoDTO {

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 50)
    private String email;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 50)
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(
        regexp = "([0-9]{11}|[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}-[0-9]{2})",
        message = "Informe um CPF válido."
    )
    private String cpf;

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(max = 15)
    private String telefone;

}

