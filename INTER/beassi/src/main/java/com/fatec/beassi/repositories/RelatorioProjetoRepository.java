
package com.fatec.beassi.repositories;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fatec.beassi.dto.RelatorioProjetoDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RelatorioProjetoRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<RelatorioProjetoDTO> listarRelatorio() {

        String sql = """
            SELECT
                ID_PROJETO,
                TITULO,
                DESCRICAO,
                DATA,
                STATUS_PUBLICACAO,
                ETAPA_ATUAL,
                PORCENTAGEM_CONCLUSAO,
                CLIENTE,
                TIPO_PROJETO,
                EMAIL_ADMINISTRADOR,
                TOTAL_IMAGENS
            FROM v_RelatorioProjetos
            ORDER BY DATA DESC
            """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {

                RelatorioProjetoDTO dto = new RelatorioProjetoDTO();

                dto.setId_projeto(rs.getInt("ID_PROJETO"));
                dto.setTitulo(rs.getString("TITULO"));
                dto.setDescricao(rs.getString("DESCRICAO"));

                if (rs.getDate("DATA") != null) {
                    dto.setData(rs.getDate("DATA").toLocalDate());
                }

                dto.setStatus_publicacao(rs.getString("STATUS_PUBLICACAO"));
                dto.setEtapa_atual(rs.getString("ETAPA_ATUAL"));
                dto.setPorcentagem_conclusao(rs.getInt("PORCENTAGEM_CONCLUSAO"));
                dto.setCliente(rs.getString("CLIENTE"));
                dto.setTipo_projeto(rs.getString("TIPO_PROJETO"));
                dto.setEmail_administrador(rs.getString("EMAIL_ADMINISTRADOR"));
                dto.setTotal_imagens(rs.getInt("TOTAL_IMAGENS"));

                return dto;
            }
        );
    }
}
