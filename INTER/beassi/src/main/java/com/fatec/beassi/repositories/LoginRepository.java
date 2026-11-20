
package com.fatec.beassi.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LoginRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_BUSCAR_USUARIO = """
        SELECT
            U.ID_USUARIO,
            U.EMAIL,
            U.SENHA AS SENHA_HASH,
            CASE
                WHEN C.ID_USUARIO IS NOT NULL THEN 'CLIENTE'
                WHEN A.ID_USUARIO IS NOT NULL THEN 'ADMINISTRADOR'
            END AS TIPO_USUARIO,
            C.NOME,
            C.STATUS AS STATUS_CLIENTE,
            A.REGISTRO_CAU
        FROM dbo.USUARIO U
        LEFT JOIN dbo.CLIENTE C
            ON C.ID_USUARIO = U.ID_USUARIO
        LEFT JOIN dbo.ADMINISTRADOR A
            ON A.ID_USUARIO = U.ID_USUARIO
        WHERE U.EMAIL = ?;
        """;

    public Optional<UsuarioLogin> buscarPorEmail(String email) {

        List<UsuarioLogin> resultados = jdbcTemplate.query(
            SQL_BUSCAR_USUARIO,
            new UsuarioLoginRowMapper(),
            email
        );

        return resultados.stream().findFirst();
    }

    private static class UsuarioLoginRowMapper
            implements RowMapper<UsuarioLogin> {

        @Override
        public UsuarioLogin mapRow(ResultSet rs, int rowNum)
                throws SQLException {

            boolean status = rs.getBoolean("STATUS_CLIENTE");
            Boolean statusCliente = rs.wasNull() ? null : status;

            return new UsuarioLogin(
                rs.getInt("ID_USUARIO"),
                rs.getString("EMAIL"),
                rs.getString("SENHA_HASH"),
                rs.getString("TIPO_USUARIO"),
                rs.getString("NOME"),
                statusCliente,
                rs.getString("REGISTRO_CAU")
            );
        }
    }

    public record UsuarioLogin(
        Integer idUsuario,
        String email,
        String senhaHash,
        String tipoUsuario,
        String nome,
        Boolean statusCliente,
        String registroCau
    ) {}
}
