
package com.fatec.beassi.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProjetoCategoriaId implements Serializable {

    @Column(name = "projeto_id")
    private Integer projetoId;

    @Column(name = "categoria_id")
    private Integer categoriaId;
}
