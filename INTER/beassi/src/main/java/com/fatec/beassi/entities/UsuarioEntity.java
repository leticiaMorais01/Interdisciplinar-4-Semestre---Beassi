package com.fatec.beassi.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data //setter e getter
@AllArgsConstructor //construtor com todos os atributos
@NoArgsConstructor //construtor sem argumentos

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table (name="USUARIO") /* TABELA NO BANCO */

public class UsuarioEntity {

    @Id
    @GeneratedValue (strategy=GenerationType.IDENTITY)
    private int id_usuario;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "senha", length = 255, nullable = false)
    private String senha;
    //VARCHAR(255) permite armazenar hashes BCrypt.
    
}

