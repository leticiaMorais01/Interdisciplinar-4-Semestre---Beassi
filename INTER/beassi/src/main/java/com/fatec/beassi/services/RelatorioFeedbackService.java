package com.fatec.beassi.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.beassi.dto.RelatorioFeedbackDTO;
import com.fatec.beassi.repositories.RelatorioFeedbackRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RelatorioFeedbackService {

    private final RelatorioFeedbackRepository relatorioFeedbackRepository;

    @Transactional(readOnly = true)
    public List<RelatorioFeedbackDTO> listarRelatorio() {

        return relatorioFeedbackRepository.listarRelatorio();
    }
}

