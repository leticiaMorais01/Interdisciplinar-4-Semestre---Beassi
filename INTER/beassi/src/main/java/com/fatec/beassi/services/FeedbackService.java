
package com.fatec.beassi.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.entities.ClienteEntity;
import com.fatec.beassi.entities.FeedbackEntity;
import com.fatec.beassi.entities.ProjetoEntity;

import com.fatec.beassi.repositories.ClienteRepository;
import com.fatec.beassi.repositories.FeedbackRepository;
import com.fatec.beassi.repositories.ProjetoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ClienteRepository clienteRepository;
    private final ProjetoRepository projetoRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final List<String> STATUS_PERMITIDOS =
        Arrays.asList("PENDENTE", "APROVADO", "REJEITADO");

    // LISTAR TODOS OS FEEDBACKS
    @Transactional(readOnly = true)
    public List<FeedbackEntity> listarTodos() {

        return feedbackRepository.findAll();
    }


    // LISTAR FEEDBACKS DE UM CLIENTE
    @Transactional(readOnly = true)
    public List<FeedbackEntity> listarPorCliente(Integer idCliente) {
        return feedbackRepository.listarPorCliente(idCliente);
}


    // BUSCAR FEEDBACK POR ID
    @Transactional(readOnly = true)
    public FeedbackEntity buscarPorId(Integer id) {

        return feedbackRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Feedback não encontrado."
            ));
    }

    // CADASTRAR FEEDBACK
    @Transactional
    public FeedbackEntity incluir(FeedbackEntity feedback) {

        validarDados(feedback);

        ClienteEntity cliente = buscarCliente(feedback);
        ProjetoEntity projeto = buscarProjeto(feedback);

        if (!Boolean.TRUE.equals(cliente.getStatus())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Um cliente inativo não pode enviar feedback."
            );
        }

        if (projeto.getCliente() == null ||
        projeto.getCliente().getId_usuario()
            != cliente.getId_usuario()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O cliente informado não está vinculado ao projeto."
            );
        }

        feedback.setCliente(cliente);
        feedback.setProjeto(projeto);

        // O banco preenche DATA_ENVIO automaticamente.
        // Todo feedback novo começa pendente.
        feedback.setStatus_aprovacao("PENDENTE");

        // O padrão do banco é 1: exibir o nome.
        if (feedback.getExibir_nome() == null) {
            feedback.setExibir_nome(true);
        }

        feedback.setData_envio(LocalDateTime.now());

        Integer idFeedback = jdbcTemplate.queryForObject(
            "EXEC dbo.sp_CadastrarFeedback ?, ?, ?, ?, ?",
            Integer.class,
            cliente.getId_usuario(),
            projeto.getId_projeto(),
            feedback.getNota(),
            feedback.getComentario(),
            feedback.getExibir_nome()
        );

        return feedbackRepository.findById(idFeedback)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Feedback cadastrado, mas não foi possível recuperá-lo."
            ));
    }

    // EDITAR FEEDBACK
    @Transactional
    public FeedbackEntity editar(Integer id, FeedbackEntity dados) {

        FeedbackEntity existente = buscarPorId(id);

        validarDados(dados);

        existente.setNota(dados.getNota());
        existente.setComentario(dados.getComentario().trim());

        if (dados.getExibir_nome() != null) {
            existente.setExibir_nome(dados.getExibir_nome());
        }

        // Cliente, projeto, data e status não são alterados aqui.
        return feedbackRepository.save(existente);
    }

    
    // ALTERAR STATUS DE APROVAÇÃO PELA PROCEDURE
    @Transactional
    public FeedbackEntity alterarStatus(
            Integer id,
            String novoStatus) {

        if (novoStatus == null || novoStatus.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O status de aprovação é obrigatório."
            );
        }

        String status = novoStatus.trim().toUpperCase(Locale.ROOT);

        if (!STATUS_PERMITIDOS.contains(status)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Status inválido. Utilize PENDENTE, APROVADO ou REJEITADO."
            );
        }

        if (!feedbackRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Feedback não encontrado."
            );
        }

        jdbcTemplate.update(
            "EXEC dbo.sp_AtualizarStatusFeedback ?, ?",
            id,
            status
        );

        return feedbackRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Feedback não encontrado."
            ));
    }


    // EXCLUIR FEEDBACK
    @Transactional
    public void excluir(Integer id) {

        FeedbackEntity feedback = buscarPorId(id);

        feedbackRepository.delete(feedback);
    }

    // BUSCAR CLIENTE INFORMADO
    private ClienteEntity buscarCliente(FeedbackEntity feedback) {

        if (feedback.getCliente() == null ||
                feedback.getCliente().getId_usuario() <= 0) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "É obrigatório informar um cliente válido."
            );
        }

        return clienteRepository.findById(
            feedback.getCliente().getId_usuario()
        ).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Cliente não encontrado."
        ));
    }

    // BUSCAR PROJETO INFORMADO
    private ProjetoEntity buscarProjeto(FeedbackEntity feedback) {

        if (feedback.getProjeto() == null ||
                feedback.getProjeto().getId_projeto() <= 0) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "É obrigatório informar um projeto válido."
            );
        }

        return projetoRepository.findById(
            feedback.getProjeto().getId_projeto()
        ).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Projeto não encontrado."
        ));
    }

    // VALIDAR DADOS DO FEEDBACK
    private void validarDados(FeedbackEntity feedback) {

        if (feedback == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Os dados do feedback são obrigatórios."
            );
        }

        if (feedback.getNota() == null ||
                feedback.getNota() < 1 ||
                feedback.getNota() > 5) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A nota deve ser um número inteiro entre 1 e 5."
            );
        }

        if (feedback.getComentario() == null ||
                feedback.getComentario().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O comentário do feedback é obrigatório."
            );
        }

        if (feedback.getComentario().trim().length() > 1000) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O comentário deve ter no máximo 1000 caracteres."
            );
        }
    }
}
