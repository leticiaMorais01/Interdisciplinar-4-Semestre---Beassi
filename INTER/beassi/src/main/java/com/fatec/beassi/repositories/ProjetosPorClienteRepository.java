package com.fatec.beassi.repositories;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fatec.beassi.dto.ProjetosPorClienteDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProjetosPorClienteRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<ProjetosPorClienteDTO> listarPorCliente(Integer idCliente) {

        String sql = """
            SELECT
                ID_PROJETO,
                TITULO,
                DATA,
                ETAPA_ATUAL,
                PORCENTAGEM_CONCLUSAO,
                STATUS_PUBLICACAO
            FROM dbo.fc_ProjetosPorCliente(?)
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            ProjetosPorClienteDTO dto = new ProjetosPorClienteDTO();

            dto.setId_projeto(rs.getInt("ID_PROJETO"));
            dto.setTitulo(rs.getString("TITULO"));

            if (rs.getDate("DATA") != null) {
                dto.setData(rs.getDate("DATA").toLocalDate());
            }

            dto.setEtapa_atual(rs.getString("ETAPA_ATUAL"));
            dto.setPorcentagem_conclusao(
                rs.getInt("PORCENTAGEM_CONCLUSAO")
            );

            boolean status = rs.getBoolean("STATUS_PUBLICACAO");
            dto.setStatus_publicacao(
                rs.wasNull() ? null : status
            );

            return dto;
        }, idCliente);
    }
}
