
package com.fatec.beassi.repositories;

import java.math.BigDecimal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FuncaoProjetoRepository {

    private final JdbcTemplate jdbcTemplate;

    public BigDecimal calcularPercentualAvaliacoesPositivas(Integer idProjeto) {

        String sql = """
            SELECT dbo.fc_PercentualAvaliacoesPositivasProjeto(?) 
            """;

        return jdbcTemplate.queryForObject(
            sql,
            BigDecimal.class,
            idProjeto
        );
    }
}
