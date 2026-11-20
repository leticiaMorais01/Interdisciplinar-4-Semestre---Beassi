
package com.fatec.beassi.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "FEEDBACK")
public class FeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_feedback;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "projeto_id", nullable = false)
    private ProjetoEntity projeto;

    @Column(name = "nota", nullable = false)
    private Integer nota;

    @Column(name = "comentario", length = 1000, nullable = false)
    private String comentario;

    @Column(name = "data_envio", nullable = false, updatable = false)
    private LocalDateTime data_envio;

    @Column(name = "status_aprovacao", length = 15, nullable = false)
    private String status_aprovacao;

    @Column(name = "exibir_nome", nullable = false)
    private Boolean exibir_nome;
}
