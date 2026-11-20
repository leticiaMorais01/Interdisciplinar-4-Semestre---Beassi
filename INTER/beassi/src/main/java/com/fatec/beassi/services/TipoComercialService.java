
package com.fatec.beassi.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.entities.TipoComercialEntity;
import com.fatec.beassi.repositories.TipoComercialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoComercialService {

    private final TipoComercialRepository tipoComercialRepository;

    @Transactional(readOnly = true)
    public List<TipoComercialEntity> listarTodos() {
        return tipoComercialRepository.findAll();
    }

    // LISTAR SOMENTE TIPOS COMERCIAIS ATIVOS
    @Transactional(readOnly = true)
    public List<TipoComercialEntity> listarAtivos() {

        return tipoComercialRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public TipoComercialEntity buscarPorId(Integer id) {

        return tipoComercialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tipo comercial não encontrado."
                ));
    }

    @Transactional
    public TipoComercialEntity incluir(TipoComercialEntity tipoComercial) {

        validarTipo(tipoComercial);

        tipoComercial.setTipo(tipoComercial.getTipo().trim());

        tipoComercial.setAtivo(true);

        return tipoComercialRepository.save(tipoComercial);
    }

    @Transactional
    public TipoComercialEntity editar(int id, TipoComercialEntity tipoComercial) {

        TipoComercialEntity tipoExistente = tipoComercialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tipo comercial não encontrado."
                ));

        validarTipo(tipoComercial);

        tipoExistente.setTipo(tipoComercial.getTipo().trim());

        return tipoComercialRepository.save(tipoExistente);
    }


    // INATIVAR TIPO COMERCIAL
    @Transactional
    public TipoComercialEntity excluir(Integer id) {

        TipoComercialEntity tipoExistente =
                tipoComercialRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tipo comercial não encontrado."
                        ));

        tipoExistente.setAtivo(false);

        return tipoComercialRepository.save(tipoExistente);
    }

    // REATIVAR TIPO COMERCIAL
    @Transactional
    public TipoComercialEntity reativar(Integer id) {

        TipoComercialEntity tipoExistente =
                tipoComercialRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tipo comercial não encontrado."
                        ));

        tipoExistente.setAtivo(true);

        return tipoComercialRepository.save(tipoExistente);
    }

    private void validarTipo(TipoComercialEntity tipoComercial) {

        if (tipoComercial == null
                || tipoComercial.getTipo() == null
                || tipoComercial.getTipo().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome do tipo comercial é obrigatório."
            );
        }

        if (tipoComercial.getTipo().trim().length() > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome do tipo comercial deve ter no máximo 50 caracteres."
            );
        }
    }
}
