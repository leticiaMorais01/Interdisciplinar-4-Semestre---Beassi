package com.fatec.beassi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fatec.beassi.entities.ImagemEntity;

@Repository
public interface ImagemRepository extends JpaRepository<ImagemEntity, Integer> {
    // Aqui você pode criar métodos de consulta personalizados, se quiser
    // Exemplo: List<Livro> findByTituloContaining(String titulo);
}