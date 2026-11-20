
package com.fatec.beassi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.fatec.beassi.entities.TipoComercialEntity;

@Repository
public interface TipoComercialRepository
        extends JpaRepository<TipoComercialEntity, Integer> {

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM ProjetoEntity p
        WHERE p.tipoComercial.id_tipo_comercial = :tipoId
    """)
    boolean possuiProjetosAssociados(@Param("tipoId") Integer tipoId);

    // LISTAR SOMENTE TIPOS COMERCIAIS ATIVOS
    List<TipoComercialEntity> findByAtivoTrue();
}
