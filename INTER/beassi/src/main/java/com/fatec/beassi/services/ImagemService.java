
package com.fatec.beassi.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.entities.ImagemEntity;
import com.fatec.beassi.entities.ProjetoEntity;

import com.fatec.beassi.repositories.ImagemRepository;
import com.fatec.beassi.repositories.ProjetoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImagemService {

    private final ImagemRepository imagemRepository;
    private final ProjetoRepository projetoRepository;

    @Value("${app.upload.dir:uploads}")
    private String diretorioUpload;

    private static final long TAMANHO_MAXIMO = 5 * 1024 * 1024;

    // LISTAR TODAS AS IMAGENS
    @Transactional(readOnly = true)
    public List<ImagemEntity> listarTodos() {

        return imagemRepository.findAll();
    }

    // BUSCAR IMAGEM POR ID
    @Transactional(readOnly = true)
    public ImagemEntity buscarPorId(Integer id) {

        return imagemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Imagem não encontrada."
                ));
    }

    // CADASTRAR IMAGEM
    // CADASTRAR IMAGEM
        @Transactional
        public ImagemEntity incluir(
            MultipartFile arquivo,
            Integer projetoId) {

            validarDados(arquivo, projetoId);

        ProjetoEntity projeto = buscarProjeto(projetoId);

        String caminhoSalvo = armazenarArquivo(arquivo);

        ImagemEntity imagem = new ImagemEntity();

        imagem.setCaminho(caminhoSalvo);
        imagem.setProjeto(projeto);

        try {
            return imagemRepository.saveAndFlush(imagem);

        } catch (RuntimeException e) {
            excluirArquivoFisico(caminhoSalvo);
            throw e;
        }
    }
    
    // EDITAR IMAGEM E SUBSTITUIR ARQUIVO
    @Transactional
    public ImagemEntity editar(
            Integer id,
            MultipartFile arquivo,
            Integer projetoId) {

        ImagemEntity imagemExistente = buscarPorId(id);

        validarDados(arquivo, projetoId);

        ProjetoEntity projeto = buscarProjeto(projetoId);

        String caminhoAntigo = imagemExistente.getCaminho();

        String novoCaminho = armazenarArquivo(arquivo);

        imagemExistente.setCaminho(novoCaminho);
        imagemExistente.setProjeto(projeto);

        ImagemEntity imagemAtualizada;

        try {
            imagemAtualizada =
                imagemRepository.saveAndFlush(imagemExistente);

        } catch (RuntimeException e) {
            excluirArquivoFisico(novoCaminho);
            throw e;
        }

        excluirArquivoFisico(caminhoAntigo);

        return imagemAtualizada;
    }


    // EXCLUIR IMAGEM
    @Transactional
    public void excluir(Integer id) {

        ImagemEntity imagem = buscarPorId(id);

        String caminhoArquivo = imagem.getCaminho();

        imagemRepository.delete(imagem);
        imagemRepository.flush();

        excluirArquivoFisico(caminhoArquivo);
    }

    // VALIDAR DADOS RECEBIDOS
    private void validarDados(
            MultipartFile arquivo,
            Integer projetoId) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O arquivo da imagem é obrigatório."
            );
        }

        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A imagem deve possuir no máximo 5 MB."
            );
        }

        if (projetoId == null || projetoId <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "É obrigatório informar um projeto válido."
            );
        }
    }

    // BUSCAR PROJETO ASSOCIADO
    private ProjetoEntity buscarProjeto(Integer projetoId) {

        return projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Projeto não encontrado."
                ));
    }

    // ARMAZENAR ARQUIVO NO SERVIDOR
    private String armazenarArquivo(MultipartFile arquivo) {

        try {
            byte[] conteudo = arquivo.getBytes();

            String extensao = identificarFormato(conteudo);

            Path diretorio = Paths.get(diretorioUpload)
                    .toAbsolutePath()
                    .normalize()
                    .resolve("projetos");

            Files.createDirectories(diretorio);

            String nomeArquivo = UUID.randomUUID() + "." + extensao;

            Path destino = diretorio.resolve(nomeArquivo).normalize();

            if (!destino.startsWith(diretorio)) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Caminho de arquivo inválido."
                );
            }

            Files.write(
                destino,
                conteudo,
                StandardOpenOption.CREATE_NEW
            );

            return "/uploads/projetos/" + nomeArquivo;

        } catch (IOException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Não foi possível armazenar a imagem."
            );
        }
    }

    // IDENTIFICAR O FORMATO REAL DO ARQUIVO
    private String identificarFormato(byte[] arquivo) {

        // JPEG
        if (arquivo.length >= 3 &&
            (arquivo[0] & 0xFF) == 0xFF &&
            (arquivo[1] & 0xFF) == 0xD8 &&
            (arquivo[2] & 0xFF) == 0xFF) {

            return "jpg";
        }

        // PNG
        if (arquivo.length >= 8 &&
            (arquivo[0] & 0xFF) == 0x89 &&
            arquivo[1] == 0x50 &&
            arquivo[2] == 0x4E &&
            arquivo[3] == 0x47 &&
            arquivo[4] == 0x0D &&
            arquivo[5] == 0x0A &&
            arquivo[6] == 0x1A &&
            arquivo[7] == 0x0A) {

            return "png";
        }

        // WEBP
        if (arquivo.length >= 12 &&
            arquivo[0] == 'R' &&
            arquivo[1] == 'I' &&
            arquivo[2] == 'F' &&
            arquivo[3] == 'F' &&
            arquivo[8] == 'W' &&
            arquivo[9] == 'E' &&
            arquivo[10] == 'B' &&
            arquivo[11] == 'P') {

            return "webp";
        }

        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Formato inválido. Envie uma imagem JPG, PNG ou WEBP."
        );
    }

    // EXCLUIR ARQUIVO FÍSICO
    private void excluirArquivoFisico(String caminhoSalvo) {

        if (caminhoSalvo == null ||
            !caminhoSalvo.startsWith("/uploads/projetos/")) {
            return;
        }

        try {
            Path diretorio = Paths.get(diretorioUpload)
                    .toAbsolutePath()
                    .normalize()
                    .resolve("projetos")
                    .normalize();

            String nomeArquivo = caminhoSalvo.substring(
                "/uploads/projetos/".length()
            );

            // Aceita somente o nome do arquivo, sem subpastas.
            if (nomeArquivo.isBlank() ||
                nomeArquivo.contains("/") ||
                nomeArquivo.contains("\\") ||
                nomeArquivo.equals(".") ||
                nomeArquivo.equals("..")) {
                return;
            }

            Path arquivo = diretorio.resolve(nomeArquivo).normalize();

            if (arquivo.startsWith(diretorio)) {
                Files.deleteIfExists(arquivo);
            }

        } catch (IOException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Não foi possível excluir o arquivo da imagem."
            );
        }
    }
}
