
package com.fatec.beassi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.fatec.beassi.entities.CategoriaEntity;

@Repository
public interface CategoriaRepository
        extends JpaRepository<CategoriaEntity, Integer> {

    // VERIFICAR SE A CATEGORIA ESTÁ ASSOCIADA A ALGUM PROJETO
    @Query("""
        SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END
        FROM ProjetoCategoriaEntity pc
        WHERE pc.categoria.id_categoria = :categoriaId
    """)
    boolean possuiProjetosAssociados(
            @Param("categoriaId") Integer categoriaId
    );

    @Query("""
    SELECT c
    FROM CategoriaEntity c
    WHERE UPPER(c.categoria) = UPPER(:nome)
    """)
    Optional<CategoriaEntity> buscarPorNome(
        @Param("nome") String nome
    );

    // LISTAR SOMENTE CATEGORIAS ATIVAS
    List<CategoriaEntity> findByAtivoTrue();
}
