
package com.fatec.beassi.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.entities.ProjetoCategoriaEntity;
import com.fatec.beassi.entities.ProjetoCategoriaId;
import com.fatec.beassi.entities.ProjetoEntity;
import com.fatec.beassi.entities.CategoriaEntity;

import com.fatec.beassi.repositories.ProjetoCategoriaRepository;
import com.fatec.beassi.repositories.ProjetoRepository;
import com.fatec.beassi.repositories.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjetoCategoriaService {

    private final ProjetoCategoriaRepository projetoCategoriaRepository;
    private final ProjetoRepository projetoRepository;
    private final CategoriaRepository categoriaRepository;

    // LISTAR TODAS AS ASSOCIAÇÕES
    @Transactional(readOnly = true)
    public List<ProjetoCategoriaEntity> listarTodos() {

        return projetoCategoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
public ProjetoCategoriaEntity buscarPorId(
        Integer projetoId, Integer categoriaId) {

    ProjetoCategoriaId id =
        new ProjetoCategoriaId(projetoId, categoriaId);

    return projetoCategoriaRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Associação entre projeto e categoria não encontrada."
        ));
}

    // INCLUIR ASSOCIAÇÃO
    @Transactional
    public ProjetoCategoriaEntity incluir(
            ProjetoCategoriaEntity projetoCategoria) {

        validarDados(projetoCategoria);

        Integer projetoId =
            projetoCategoria.getProjeto().getId_projeto();

        Integer categoriaId =
            projetoCategoria.getCategoria().getId_categoria();

        ProjetoEntity projeto = buscarProjeto(projetoId);
        CategoriaEntity categoria = buscarCategoria(categoriaId);

        ProjetoCategoriaId id = new ProjetoCategoriaId(
            projetoId,
            categoriaId
        );

        if (projetoCategoriaRepository.existsById(id)) {

            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Essa categoria já está associada ao projeto."
            );
        }

        ProjetoCategoriaEntity novaAssociacao =
            new ProjetoCategoriaEntity();

        novaAssociacao.setId(id);
        novaAssociacao.setProjeto(projeto);
        novaAssociacao.setCategoria(categoria);

        return projetoCategoriaRepository.save(novaAssociacao);
    }

    // EDITAR ASSOCIAÇÃO
    @Transactional
    public ProjetoCategoriaEntity editar(
            Integer projetoIdAtual,
            Integer categoriaIdAtual,
            ProjetoCategoriaEntity dadosAtualizados) {

        validarDados(dadosAtualizados);

        ProjetoCategoriaId idAtual = new ProjetoCategoriaId(
            projetoIdAtual,
            categoriaIdAtual
        );

        ProjetoCategoriaEntity associacaoExistente =
            projetoCategoriaRepository.findById(idAtual)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Associação entre projeto e categoria não encontrada."
                ));

        Integer novoProjetoId =
            dadosAtualizados.getProjeto().getId_projeto();

        Integer novaCategoriaId =
            dadosAtualizados.getCategoria().getId_categoria();

        ProjetoEntity novoProjeto = buscarProjeto(novoProjetoId);
        CategoriaEntity novaCategoria = buscarCategoria(novaCategoriaId);

        ProjetoCategoriaId novoId = new ProjetoCategoriaId(
            novoProjetoId,
            novaCategoriaId
        );

        if (!idAtual.equals(novoId) &&
            projetoCategoriaRepository.existsById(novoId)) {

            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Essa categoria já está associada ao projeto."
            );
        }

        // Se a chave composta mudou, recria a associação.
        if (!idAtual.equals(novoId)) {

            projetoCategoriaRepository.delete(associacaoExistente);
            projetoCategoriaRepository.flush();

            ProjetoCategoriaEntity novaAssociacao =
                new ProjetoCategoriaEntity();

            novaAssociacao.setId(novoId);
            novaAssociacao.setProjeto(novoProjeto);
            novaAssociacao.setCategoria(novaCategoria);

            return projetoCategoriaRepository.save(novaAssociacao);
        }

        // Se a chave não mudou, mantém a associação existente.
        associacaoExistente.setProjeto(novoProjeto);
        associacaoExistente.setCategoria(novaCategoria);

        return projetoCategoriaRepository.save(associacaoExistente);
    }

    // EXCLUIR ASSOCIAÇÃO
    @Transactional
    public void excluir(Integer projetoId, Integer categoriaId) {

        ProjetoCategoriaId id = new ProjetoCategoriaId(
            projetoId,
            categoriaId
        );

        ProjetoCategoriaEntity associacao =
            projetoCategoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Associação entre projeto e categoria não encontrada."
                ));

        projetoCategoriaRepository.delete(associacao);
    }

    // VALIDAR DADOS RECEBIDOS
    private void validarDados(ProjetoCategoriaEntity projetoCategoria) {

        if (projetoCategoria == null ||
            projetoCategoria.getProjeto() == null ||
            projetoCategoria.getCategoria() == null) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Projeto e categoria são obrigatórios."
            );
        }

        Integer projetoId =
    projetoCategoria.getProjeto().getId_projeto();

Integer categoriaId =
    projetoCategoria.getCategoria().getId_categoria();

if (projetoId == null || projetoId <= 0 ||
    categoriaId == null || categoriaId <= 0) {

    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Os IDs do projeto e da categoria devem ser válidos."
    );
}
    }

    // BUSCAR PROJETO
    private ProjetoEntity buscarProjeto(Integer id) {

        return projetoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Projeto não encontrado."
            ));
    }

    // BUSCAR CATEGORIA
    private CategoriaEntity buscarCategoria(Integer id) {

        return categoriaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Categoria não encontrada."
            ));
    }
}
