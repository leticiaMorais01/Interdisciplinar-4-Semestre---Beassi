package com.fatec.beassi.repositories;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fatec.beassi.dto.ResumoFeedbackProjetoDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ResumoFeedbackProjetoRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<ResumoFeedbackProjetoDTO> listarResumo() {

        String sql = """
            SELECT
                ID_PROJETO,
                TITULO,
                TOTAL_FEEDBACKS,
                MEDIA_NOTAS
            FROM dbo.fc_ResumoFeedbackProjetos()
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            ResumoFeedbackProjetoDTO dto =
                new ResumoFeedbackProjetoDTO();

            dto.setId_projeto(rs.getInt("ID_PROJETO"));
            dto.setTitulo(rs.getString("TITULO"));
            dto.setTotal_feedbacks(rs.getInt("TOTAL_FEEDBACKS"));
            dto.setMedia_notas(rs.getBigDecimal("MEDIA_NOTAS"));

            return dto;
        });
    }
}

