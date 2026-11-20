package com.fatec.beassi.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.dto.ProjetosPorClienteDTO;
import com.fatec.beassi.repositories.ClienteRepository;
import com.fatec.beassi.repositories.ProjetosPorClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjetosPorClienteService {

    private final ProjetosPorClienteRepository projetosPorClienteRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ProjetosPorClienteDTO> listarPorCliente(Integer idCliente) {

        if (idCliente == null || !clienteRepository.existsById(idCliente)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado."
            );
        }

        return projetosPorClienteRepository.listarPorCliente(idCliente);
    }
}
