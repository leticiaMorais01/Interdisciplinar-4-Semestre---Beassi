
package com.fatec.beassi.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.entities.CategoriaEntity;
import com.fatec.beassi.repositories.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    // LISTAR TODAS AS CATEGORIAS
    @Transactional(readOnly = true)
    public List<CategoriaEntity> listarTodos() {

        return categoriaRepository.findAll();
    }

    // LISTAR SOMENTE CATEGORIAS ATIVAS
    @Transactional(readOnly = true)
    public List<CategoriaEntity> listarAtivas() {

        return categoriaRepository.findByAtivoTrue();
    }

    // BUSCAR CATEGORIA POR ID
@Transactional(readOnly = true)
public CategoriaEntity buscarPorId(Integer id) {

    return categoriaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Categoria não encontrada."
            ));
}

    // CADASTRAR CATEGORIA
    @Transactional
    public CategoriaEntity incluir(CategoriaEntity categoria) {

        validarCategoria(categoria);

        categoria.setCategoria(categoria.getCategoria().trim());

        String nome = categoria.getCategoria().trim();

        validarNomeDisponivel(nome, null);

        categoria.setCategoria(nome);

        categoria.setAtivo(true);

        return categoriaRepository.save(categoria);
    }

    // EDITAR CATEGORIA
    @Transactional
    public CategoriaEntity editar(int id, CategoriaEntity categoria) {

        CategoriaEntity categoriaExistente =
                categoriaRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Categoria não encontrada."
                        ));

        validarCategoria(categoria);

        categoriaExistente.setCategoria(
                categoria.getCategoria().trim()
        );

        String nome = categoria.getCategoria().trim();

        validarNomeDisponivel(nome, id);

        categoriaExistente.setCategoria(nome);

        return categoriaRepository.save(categoriaExistente);
    }

    // INATIVAR CATEGORIA
    @Transactional
    public CategoriaEntity excluir(Integer id) {

        CategoriaEntity categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoria não encontrada."
                ));

        categoria.setAtivo(false);

        return categoriaRepository.save(categoria);
    }

    // REATIVAR CATEGORIA
    @Transactional
    public CategoriaEntity reativar(Integer id) {

        CategoriaEntity categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoria não encontrada."
                ));

        categoria.setAtivo(true);

        return categoriaRepository.save(categoria);
    }

    // VALIDAR DADOS DA CATEGORIA
    private void validarCategoria(CategoriaEntity categoria) {

        if (categoria == null ||
                categoria.getCategoria() == null ||
                categoria.getCategoria().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da categoria é obrigatório."
            );
        }

        if (categoria.getCategoria().trim().length() > 50) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da categoria deve ter no máximo 50 caracteres."
            );
        }
    }

    private void validarNomeDisponivel(String nome, Integer idIgnorado) {

    categoriaRepository.buscarPorNome(nome)
        .ifPresent(categoria -> {

            if (idIgnorado == null ||
                    categoria.getId_categoria() != idIgnorado) {

                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma categoria cadastrada com esse nome."
                );
            }
        });
    }

}
