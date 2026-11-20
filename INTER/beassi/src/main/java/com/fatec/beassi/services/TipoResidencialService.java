
package com.fatec.beassi.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.entities.TipoResidencialEntity;
import com.fatec.beassi.repositories.TipoResidencialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoResidencialService {

    private final TipoResidencialRepository tipoResidencialRepository;

    @Transactional(readOnly = true)
    public List<TipoResidencialEntity> listarTodos() {
        return tipoResidencialRepository.findAll();
    }

    // LISTAR SOMENTE TIPOS RESIDENCIAIS ATIVOS
    @Transactional(readOnly = true)
    public List<TipoResidencialEntity> listarAtivos() {

        return tipoResidencialRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public TipoResidencialEntity buscarPorId(Integer id) {

        return tipoResidencialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tipo residencial não encontrado."
                ));
    }

    @Transactional
    public TipoResidencialEntity incluir(TipoResidencialEntity tipoResidencial) {

        validarTipo(tipoResidencial);

        tipoResidencial.setTipo(tipoResidencial.getTipo().trim());

        tipoResidencial.setAtivo(true);

        return tipoResidencialRepository.save(tipoResidencial);
    }

    @Transactional
    public TipoResidencialEntity editar(int id, TipoResidencialEntity tipoResidencial) {

        TipoResidencialEntity tipoExistente = tipoResidencialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tipo residencial não encontrado."
                ));

        validarTipo(tipoResidencial);

        tipoExistente.setTipo(tipoResidencial.getTipo().trim());

        return tipoResidencialRepository.save(tipoExistente);
    }

    // INATIVAR TIPO RESIDENCIAL
    @Transactional
    public TipoResidencialEntity excluir(Integer id) {

        TipoResidencialEntity tipoExistente =
                tipoResidencialRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tipo residencial não encontrado."
                        ));

        tipoExistente.setAtivo(false);

        return tipoResidencialRepository.save(tipoExistente);
    }

    // REATIVAR TIPO RESIDENCIAL
    @Transactional
    public TipoResidencialEntity reativar(Integer id) {

        TipoResidencialEntity tipoExistente =
                tipoResidencialRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tipo residencial não encontrado."
                        ));

        tipoExistente.setAtivo(true);

        return tipoResidencialRepository.save(tipoExistente);
    }

    private void validarTipo(TipoResidencialEntity tipoResidencial) {

        if (tipoResidencial == null
                || tipoResidencial.getTipo() == null
                || tipoResidencial.getTipo().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome do tipo residencial é obrigatório."
            );
        }

        if (tipoResidencial.getTipo().trim().length() > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome do tipo residencial deve ter no máximo 50 caracteres."
            );
        }
    }
}
