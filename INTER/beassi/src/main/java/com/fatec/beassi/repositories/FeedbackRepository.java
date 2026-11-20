
package com.fatec.beassi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fatec.beassi.entities.FeedbackEntity;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Integer> {

    @Query("SELECT f FROM FeedbackEntity f " +
           "WHERE f.cliente.id_usuario = :idCliente " +
           "ORDER BY f.data_envio DESC")
    List<FeedbackEntity> listarPorCliente(@Param("idCliente") Integer idCliente);
}
