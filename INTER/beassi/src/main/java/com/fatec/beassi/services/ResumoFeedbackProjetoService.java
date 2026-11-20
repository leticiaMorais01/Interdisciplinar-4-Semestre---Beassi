package com.fatec.beassi.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.beassi.dto.ResumoFeedbackProjetoDTO;
import com.fatec.beassi.repositories.ResumoFeedbackProjetoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumoFeedbackProjetoService {

    private final ResumoFeedbackProjetoRepository resumoFeedbackProjetoRepository;

    @Transactional(readOnly = true)
    public List<ResumoFeedbackProjetoDTO> listarResumo() {

        return resumoFeedbackProjetoRepository.listarResumo();
    }
}

