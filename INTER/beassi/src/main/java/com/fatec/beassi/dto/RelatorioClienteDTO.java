package com.fatec.beassi.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RelatorioClienteDTO {

    private Integer id_cliente;
    private String nome;
    private String email;
    private String telefone;
    private String status_cliente;
    private Integer total_projetos;
    private Integer total_feedbacks;
    private BigDecimal media_notas;
}

