
package com.fatec.beassi.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.beassi.dto.RelatorioProjetoDTO;
import com.fatec.beassi.repositories.RelatorioProjetoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RelatorioProjetoService {

    private final RelatorioProjetoRepository relatorioProjetoRepository;

    public List<RelatorioProjetoDTO> listarRelatorio() {

        return relatorioProjetoRepository.listarRelatorio();
    }
}
