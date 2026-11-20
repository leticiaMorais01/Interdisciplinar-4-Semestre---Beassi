package com.fatec.beassi.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Data //setter e getter
@EqualsAndHashCode(callSuper = true) // gerar os atributos do pai 
@AllArgsConstructor //construtor com todos os atributos
@NoArgsConstructor //construtor sem argumentos

@Entity
@Table(name = "ADMINISTRADOR", schema = "dbo") /* TABELA NO BANCO */
@PrimaryKeyJoinColumn(name = "id_usuario")

public class AdministradorEntity extends UsuarioEntity{

    @Column(name = "registro_cau", length = 20, nullable = false)
    private String registro_cau;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "administrador")
    private List<ProjetoEntity> projetos = new ArrayList<>();

}


