package com.fatec.beassi.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RelatorioFeedbackDTO {

    private Integer id_feedback;
    private Integer projeto_id;
    private String projeto;
    private Integer cliente_id;
    private String cliente;
    private Integer nota;
    private String comentario;
    private LocalDateTime data_envio;
    private String status_aprovacao;
}
