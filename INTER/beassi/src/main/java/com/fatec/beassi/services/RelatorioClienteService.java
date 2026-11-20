package com.fatec.beassi.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.beassi.dto.RelatorioClienteDTO;
import com.fatec.beassi.repositories.RelatorioClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RelatorioClienteService {

    private final RelatorioClienteRepository relatorioClienteRepository;

    @Transactional(readOnly = true)
    public List<RelatorioClienteDTO> listarRelatorio() {

        return relatorioClienteRepository.listarRelatorio();
    }
}
