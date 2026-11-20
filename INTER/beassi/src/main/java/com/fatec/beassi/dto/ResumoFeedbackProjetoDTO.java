package com.fatec.beassi.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ResumoFeedbackProjetoDTO {

    private Integer id_projeto;
    private String titulo;
    private Integer total_feedbacks;
    private BigDecimal media_notas;
}

