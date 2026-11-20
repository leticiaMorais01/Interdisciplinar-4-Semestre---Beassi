package com.fatec.beassi.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data //setter e getter
@AllArgsConstructor //construtor com todos os atributos
@NoArgsConstructor //construtor sem argumentos

@Entity
@Table (name="TIPO_COMERCIAL") /* TABELA NO BANCO */

public class TipoComercialEntity {

    @Id
    @GeneratedValue (strategy=GenerationType.IDENTITY)
    private Integer id_tipo_comercial;

    @Column(name = "tipo", length = 50, nullable = false)
    private String tipo;

    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = true;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "tipoComercial")
    private List<ProjetoEntity> projetos = new ArrayList<>();

}
