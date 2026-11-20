package com.fatec.beassi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fatec.beassi.entities.ProjetoEntity;

@Repository
public interface ProjetoRepository
        extends JpaRepository<ProjetoEntity, Integer> {

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM ProjetoEntity p
        WHERE p.administrador.id_usuario = :id
    """)
    boolean possuiProjetosDoAdministrador(@Param("id") Integer id);
}
