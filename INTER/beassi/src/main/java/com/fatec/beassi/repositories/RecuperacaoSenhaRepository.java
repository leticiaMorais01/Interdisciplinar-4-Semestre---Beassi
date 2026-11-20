
package com.fatec.beassi.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.beassi.entities.RecuperacaoSenhaEntity;
import com.fatec.beassi.entities.UsuarioEntity;

@Repository
public interface RecuperacaoSenhaRepository extends JpaRepository<RecuperacaoSenhaEntity, Integer> {

    // Localizar um token de recuperação
    Optional<RecuperacaoSenhaEntity> findByToken(String token);

    // Localizar um token que ainda não foi utilizado
    Optional<RecuperacaoSenhaEntity> findByTokenAndUtilizadoFalse(String token);

    // Buscar recuperações não utilizadas de um usuário
    List<RecuperacaoSenhaEntity> findByUsuarioAndUtilizadoFalse(UsuarioEntity usuario);

    // Buscar token não utilizado e ainda dentro do prazo de validade
    Optional<RecuperacaoSenhaEntity> findByTokenAndUtilizadoFalseAndDataExpiracaoAfter(
            String token,
            LocalDateTime agora
    );
}
