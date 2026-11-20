package com.fatec.beassi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdministradorResponseDTO {

    private int id_usuario;
    private String email;
    private String registro_cau;
}

