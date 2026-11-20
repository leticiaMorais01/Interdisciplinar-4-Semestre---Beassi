package com.fatec.beassi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    private int id_usuario;
    private String email;
    private String nome;
    private String cpf;
    private String telefone;
    private boolean status;
}

