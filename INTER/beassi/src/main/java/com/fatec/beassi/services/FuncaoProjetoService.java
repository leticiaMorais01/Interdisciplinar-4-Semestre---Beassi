
package com.fatec.beassi.services;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.repositories.FuncaoProjetoRepository;
import com.fatec.beassi.repositories.ProjetoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuncaoProjetoService {

    private final FuncaoProjetoRepository funcaoProjetoRepository;
    private final ProjetoRepository projetoRepository;

    @Transactional(readOnly = true)
    public BigDecimal calcularPercentualAvaliacoesPositivas(Integer idProjeto) {

        if (idProjeto == null || !projetoRepository.existsById(idProjeto)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Projeto não encontrado."
            );
        }

        return funcaoProjetoRepository
            .calcularPercentualAvaliacoesPositivas(idProjeto);
    }
}
