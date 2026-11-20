package com.fatec.beassi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fatec.beassi.entities.AdministradorEntity;

@Repository
public interface AdministradorRepository
        extends JpaRepository<AdministradorEntity, Integer> {

    @Query("""
        SELECT a
        FROM AdministradorEntity a
        WHERE UPPER(a.registro_cau) = UPPER(:registro)
    """)
    Optional<AdministradorEntity> buscarPorRegistroCau(
        @Param("registro") String registro
    );
}
