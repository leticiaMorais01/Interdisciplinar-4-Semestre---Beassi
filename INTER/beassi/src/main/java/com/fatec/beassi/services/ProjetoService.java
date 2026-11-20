
package com.fatec.beassi.services;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.entities.ProjetoEntity;
import com.fatec.beassi.repositories.ProjetoRepository;
import com.fatec.beassi.repositories.ClienteRepository;
import com.fatec.beassi.repositories.AdministradorRepository;
import com.fatec.beassi.repositories.TipoComercialRepository;
import com.fatec.beassi.repositories.TipoResidencialRepository;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final ClienteRepository clienteRepository;
    private final AdministradorRepository administradorRepository;
    private final TipoComercialRepository tipoComercialRepository;
    private final TipoResidencialRepository tipoResidencialRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final List<String> ETAPAS_PERMITIDAS = Arrays.asList(
        "nao-iniciado",
        "medicao",
        "desenvolvimento",
        "apresentacao",
        "revisao",
        "executivo",
        "concluido"
    );

    // CONVERTER ETAPA DA API PARA O FORMATO ACEITO PELO BANCO
    private String converterEtapaParaBanco(String etapa) {

        switch (etapa) {
            case "nao-iniciado":
                return "Não iniciado";

            case "medicao":
                return "Medição";

            case "desenvolvimento":
                return "Desenvolvimento do projeto";

            case "apresentacao":
                return "Apresentação ao cliente";

            case "revisao":
                return "Revisões";

            case "executivo":
                return "Projeto Executivo";

            case "concluido":
                return "Concluído";

            default:
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Etapa inválida para o projeto."
                );
        }
    }

    // LISTAR TODOS OS PROJETOS
    @Transactional(readOnly = true)
    public List<ProjetoEntity> listarTodos() {
        return projetoRepository.findAll();
    }

    // BUSCAR PROJETO POR ID
    @Transactional(readOnly = true)
    public ProjetoEntity buscarPorId(Integer id) {

        return projetoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Projeto não encontrado."
            ));
    }

        // CADASTRAR PROJETO
        @Transactional
        public ProjetoEntity incluir(ProjetoEntity projeto) {

            validarProjeto(projeto);
            aplicarRelacionamentos(projeto);

            // Define a data automaticamente no cadastro
            projeto.setData(LocalDateTime.now());

            return projetoRepository.save(projeto);
        }

    // EDITAR PROJETO
    @Transactional
    public ProjetoEntity editar(Integer id, ProjetoEntity dadosAtualizados) {

        ProjetoEntity projetoExistente = buscarPorId(id);

        validarProjeto(dadosAtualizados);
        aplicarRelacionamentos(dadosAtualizados);

        projetoExistente.setTitulo(dadosAtualizados.getTitulo());
        projetoExistente.setDescricao(dadosAtualizados.getDescricao());
        // Preserva a data original caso a atualização não envie uma nova data
        if (dadosAtualizados.getData() != null) {
            projetoExistente.setData(dadosAtualizados.getData());
        }

        projetoExistente.setStatus_publicacao(
            dadosAtualizados.getStatus_publicacao()
        );

        projetoExistente.setEtapa_atual(
            dadosAtualizados.getEtapa_atual()
        );

        projetoExistente.setPorcentagem_conclusao(
            dadosAtualizados.getPorcentagem_conclusao()
        );

        projetoExistente.setCliente(dadosAtualizados.getCliente());
        projetoExistente.setAdministrador(dadosAtualizados.getAdministrador());
        projetoExistente.setTipoComercial(dadosAtualizados.getTipoComercial());
        projetoExistente.setTipoResidencial(dadosAtualizados.getTipoResidencial());

        return projetoRepository.save(projetoExistente);
    }

    // ATUALIZAR ETAPA DO PROJETO PELA PROCEDURE
@Transactional
public ProjetoEntity atualizarEtapa(
        Integer id,
        String etapa,
        Integer porcentagem) {

    // Verificar se o projeto existe
    if (id == null || !projetoRepository.existsById(id)) {
        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Projeto não encontrado."
        );
    }

    // Validar etapa
    if (etapa == null || etapa.isBlank()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "A etapa atual é obrigatória."
        );
    }

    String etapaFormatada = etapa.trim().toLowerCase(Locale.ROOT);

    if (!ETAPAS_PERMITIDAS.contains(etapaFormatada)) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Etapa inválida para o projeto."
        );
    }

    // Validar porcentagem
    validarPorcentagem(etapaFormatada, porcentagem);

    // Converter para o formato aceito pelo banco
    String etapaBanco = converterEtapaParaBanco(etapaFormatada);

    // Chamar a Stored Procedure
    jdbcTemplate.update(
        "EXEC dbo.sp_AtualizarEtapaProjeto ?, ?, ?",
        id,
        etapaBanco,
        porcentagem
    );

    // Retornar projeto atualizado
    return projetoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Projeto não encontrado."
        ));
}

    // DESATIVAR PROJETO
    @Transactional
    public void excluir(Integer id) {

        ProjetoEntity projeto = buscarPorId(id);

        projeto.setAtivo(false);

        projetoRepository.save(projeto);
    }

    // REATIVAR PROJETO
    @Transactional
    public void reativar(Integer id) {

        ProjetoEntity projeto = buscarPorId(id);

        projeto.setAtivo(true);

        projetoRepository.save(projeto);
    }

    // VALIDAR OS DADOS DO PROJETO
    private void validarProjeto(ProjetoEntity projeto) {

        if (projeto == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Os dados do projeto são obrigatórios."
            );
        }

        if (projeto.getTitulo() == null ||
                projeto.getTitulo().isBlank() ||
                projeto.getTitulo().trim().length() > 30) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O título é obrigatório e deve possuir no máximo 30 caracteres."
            );
        }

        projeto.setTitulo(projeto.getTitulo().trim());

        if (projeto.getDescricao() == null ||
                projeto.getDescricao().isBlank() ||
                projeto.getDescricao().trim().length() > 1000) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A descrição é obrigatória e deve possuir no máximo 1000 caracteres."
            );
        }

        projeto.setDescricao(projeto.getDescricao().trim());

        if (projeto.getEtapa_atual() == null ||
                projeto.getEtapa_atual().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A etapa atual do projeto é obrigatória."
            );
        }

        String etapa = projeto.getEtapa_atual()
            .trim()
            .toLowerCase(Locale.ROOT);

        if (!ETAPAS_PERMITIDAS.contains(etapa)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Etapa inválida para o projeto."
            );
        }

        validarPorcentagem(etapa, projeto.getPorcentagem_conclusao());

        // Converte para o texto exigido pela constraint do SQL Server
        projeto.setEtapa_atual(converterEtapaParaBanco(etapa));

        boolean possuiTipoComercial = projeto.getTipoComercial() != null;
        boolean possuiTipoResidencial = projeto.getTipoResidencial() != null;

        if (possuiTipoComercial == possuiTipoResidencial) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O projeto deve possuir exatamente um tipo: comercial ou residencial."
            );
        }


        if (projeto.getAdministrador() == null ||
                projeto.getAdministrador().getId_usuario() <= 0) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "É obrigatório informar um administrador válido."
            );
        }
    }

    // VALIDAR ETAPA E PORCENTAGEM
    private void validarPorcentagem(String etapa, Integer porcentagem) {

        int porcentagemEsperada;

        switch (etapa) {
            case "nao-iniciado":
                porcentagemEsperada = 0;
                break;

            case "medicao":
                porcentagemEsperada = 17;
                break;

            case "desenvolvimento":
                porcentagemEsperada = 33;
                break;

            case "apresentacao":
                porcentagemEsperada = 50;
                break;

            case "revisao":
                porcentagemEsperada = 67;
                break;

            case "executivo":
                porcentagemEsperada = 83;
                break;

            case "concluido":
                porcentagemEsperada = 100;
                break;

            default:
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Etapa inválida para o projeto."
                );
        }

        if (porcentagem == null || porcentagem != porcentagemEsperada) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A porcentagem de conclusão não corresponde à etapa atual."
            );
        }
    }

    // VALIDAR E APLICAR OS RELACIONAMENTOS
    private void aplicarRelacionamentos(ProjetoEntity projeto) {

        // CLIENTE: OPCIONAL
        if (projeto.getCliente() != null &&
                projeto.getCliente().getId_usuario() > 0) {

            int clienteId = projeto.getCliente().getId_usuario();

            var cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cliente não encontrado."
                ));

            if (!Boolean.TRUE.equals(cliente.getStatus())) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível vincular um cliente inativo a um projeto."
                );
            }

            projeto.setCliente(cliente);

        } else {
            projeto.setCliente(null);
        }

        // ADMINISTRADOR: OBRIGATÓRIO
        int administradorId = projeto.getAdministrador().getId_usuario();

        projeto.setAdministrador(
            administradorRepository.findById(administradorId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Administrador não encontrado."
                ))
        );

        // TIPO COMERCIAL
        if (projeto.getTipoComercial() != null) {

            int tipoId = projeto.getTipoComercial().getId_tipo_comercial();

            projeto.setTipoComercial(
                tipoComercialRepository.findById(tipoId)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tipo comercial não encontrado."
                    ))
            );
        }

        // TIPO RESIDENCIAL
        if (projeto.getTipoResidencial() != null) {

            int tipoId = projeto.getTipoResidencial().getId_tipo_residencial();

            projeto.setTipoResidencial(
                tipoResidencialRepository.findById(tipoId)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tipo residencial não encontrado."
                    ))
            );
        }
    }
}
