
package com.fatec.beassi.repositories;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fatec.beassi.dto.RelatorioClienteDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RelatorioClienteRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<RelatorioClienteDTO> listarRelatorio() {

        String sql = """
            SELECT
                ID_CLIENTE,
                NOME,
                EMAIL,
                TELEFONE,
                STATUS_CLIENTE,
                TOTAL_PROJETOS,
                TOTAL_FEEDBACKS,
                MEDIA_NOTAS
            FROM v_RelatorioClientes
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            RelatorioClienteDTO dto = new RelatorioClienteDTO();

            dto.setId_cliente(rs.getInt("ID_CLIENTE"));
            dto.setNome(rs.getString("NOME"));
            dto.setEmail(rs.getString("EMAIL"));
            dto.setTelefone(rs.getString("TELEFONE"));
            dto.setStatus_cliente(rs.getString("STATUS_CLIENTE"));
            dto.setTotal_projetos(rs.getInt("TOTAL_PROJETOS"));
            dto.setTotal_feedbacks(rs.getInt("TOTAL_FEEDBACKS"));
            dto.setMedia_notas(rs.getBigDecimal("MEDIA_NOTAS"));

            return dto;
        });
    }
}

