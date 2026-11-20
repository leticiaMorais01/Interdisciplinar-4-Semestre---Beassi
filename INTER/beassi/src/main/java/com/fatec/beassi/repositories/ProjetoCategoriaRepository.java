package com.fatec.beassi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.beassi.entities.ProjetoCategoriaEntity;
import com.fatec.beassi.entities.ProjetoCategoriaId;

@Repository
public interface ProjetoCategoriaRepository
        extends JpaRepository<ProjetoCategoriaEntity, ProjetoCategoriaId> {
}
