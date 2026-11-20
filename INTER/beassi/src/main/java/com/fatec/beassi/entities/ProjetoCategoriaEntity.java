package com.fatec.beassi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "PROJETO_CATEGORIA")
public class ProjetoCategoriaEntity {

    @EmbeddedId
    private ProjetoCategoriaId id;

    @ManyToOne
    @MapsId("projetoId")
    @JoinColumn(name = "projeto_id", nullable = false)
    private ProjetoEntity projeto;

    @ManyToOne
    @MapsId("categoriaId")
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;
}
