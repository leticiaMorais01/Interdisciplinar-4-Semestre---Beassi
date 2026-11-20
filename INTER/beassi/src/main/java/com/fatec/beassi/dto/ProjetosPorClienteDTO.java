package com.fatec.beassi.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ProjetosPorClienteDTO {

    private Integer id_projeto;
    private String titulo;
    private LocalDate data;
    private String etapa_atual;
    private Integer porcentagem_conclusao;
    private Boolean status_publicacao;
}

