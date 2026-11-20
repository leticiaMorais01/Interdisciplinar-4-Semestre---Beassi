
package com.fatec.beassi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.fatec.beassi.entities.TipoResidencialEntity;

@Repository
public interface TipoResidencialRepository
        extends JpaRepository<TipoResidencialEntity, Integer> {

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM ProjetoEntity p
        WHERE p.tipoResidencial.id_tipo_residencial = :tipoId
    """)
    boolean possuiProjetosAssociados(@Param("tipoId") Integer tipoId);

    // LISTAR SOMENTE TIPOS RESIDENCIAIS ATIVOS
    List<TipoResidencialEntity> findByAtivoTrue();
}
