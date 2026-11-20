package com.fatec.beassi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fatec.beassi.entities.ClienteEntity;

@Repository
public interface ClienteRepository
        extends JpaRepository<ClienteEntity, Integer> {

    @Query("""
        SELECT c
        FROM ClienteEntity c
        WHERE c.cpf = :cpf
    """)
    Optional<ClienteEntity> buscarPorCpf(@Param("cpf") String cpf);
}
