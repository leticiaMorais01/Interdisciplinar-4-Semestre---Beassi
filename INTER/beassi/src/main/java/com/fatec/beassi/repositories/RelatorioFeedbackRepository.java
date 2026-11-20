
package com.fatec.beassi.repositories;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fatec.beassi.dto.RelatorioFeedbackDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RelatorioFeedbackRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<RelatorioFeedbackDTO> listarRelatorio() {

        String sql = """
            SELECT
                ID_FEEDBACK,
                PROJETO_ID,
                PROJETO,
                CLIENTE_ID,
                CLIENTE,
                NOTA,
                COMENTARIO,
                DATA_ENVIO,
                STATUS_APROVACAO
            FROM v_RelatorioFeedbacks
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            RelatorioFeedbackDTO dto = new RelatorioFeedbackDTO();

            dto.setId_feedback(rs.getInt("ID_FEEDBACK"));
            dto.setProjeto_id(rs.getInt("PROJETO_ID"));
            dto.setProjeto(rs.getString("PROJETO"));
            dto.setCliente_id(rs.getInt("CLIENTE_ID"));
            dto.setCliente(rs.getString("CLIENTE"));
            dto.setNota(rs.getInt("NOTA"));
            dto.setComentario(rs.getString("COMENTARIO"));

            if (rs.getTimestamp("DATA_ENVIO") != null) {
                dto.setData_envio(
                    rs.getTimestamp("DATA_ENVIO").toLocalDateTime()
                );
            }

            dto.setStatus_aprovacao(
                rs.getString("STATUS_APROVACAO")
            );

            return dto;
        });
    }
}
