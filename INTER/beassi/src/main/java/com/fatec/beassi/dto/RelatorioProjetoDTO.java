package com.fatec.beassi.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RelatorioProjetoDTO {

    private Integer id_projeto;
    private String titulo;
    private String descricao;
    private LocalDate data;
    private String status_publicacao;
    private String etapa_atual;
    private Integer porcentagem_conclusao;
    private String cliente;
    private String tipo_projeto;
    private String email_administrador;
    private Integer total_imagens;
}

