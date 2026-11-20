
package com.fatec.beassi.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Data //setter e getter
@AllArgsConstructor //construtor com todos os atributos
@NoArgsConstructor //construtor sem argumentos

@Entity
@Table (name="PROJETO") /* TABELA NO BANCO */

public class ProjetoEntity {

    @Id
    @GeneratedValue (strategy=GenerationType.IDENTITY)
    private Integer id_projeto;

    @Column(name = "titulo", length = 30, nullable = false)
    private String titulo;

    @Column(name = "descricao", length = 1000, nullable = false)
    private String descricao;

    @Column(name = "data", nullable = true)
    private LocalDateTime data;

    @Column(name = "status_publicacao", nullable = true)
    private Boolean status_publicacao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "etapa_atual", length = 30, nullable = false)
    private String etapa_atual; 

    @Column(name = "porcentagem_conclusao", nullable = false)
    private Integer porcentagem_conclusao; 

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "cliente_id", nullable = true, referencedColumnName = "id_usuario")
    private ClienteEntity cliente;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "administrador_id", nullable = false, referencedColumnName = "id_usuario")
    private AdministradorEntity administrador;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "tipo_comercial_id", nullable = true)
    private TipoComercialEntity tipoComercial;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "tipo_residencial_id", nullable = true)
    private TipoResidencialEntity tipoResidencial;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "projeto")
    private List<ProjetoCategoriaEntity> categorias = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "projeto")
    private List<ImagemEntity> imagens = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "projeto")
    private List<FeedbackEntity> feedbacks = new ArrayList<>();
}
