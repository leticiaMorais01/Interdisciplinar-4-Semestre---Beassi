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
@Table (name="CLIENTE") /* TABELA NO BANCO */
@PrimaryKeyJoinColumn(name = "id_usuario")

public class ClienteEntity extends UsuarioEntity{

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @Column(name = "cpf", length = 14, nullable = false)
    private String cpf;

    @Column(name = "telefone", length = 15, nullable = false)
    private String telefone;

    @Column(name = "status", nullable = false)
    private Boolean status;
    //só pode ser 1-ativo, 0-inativo

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "cliente")
    private List<ProjetoEntity> projetos = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "cliente")
    private List<FeedbackEntity> feedbacks = new ArrayList<>();

}
