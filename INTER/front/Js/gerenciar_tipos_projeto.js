
document.addEventListener('DOMContentLoaded', () => {

    // =========================================
    // CONFIGURAÇÕES DAS DUAS ABAS
    // =========================================

    const configuracoes = {

        residencial: {
            endpoint: '/tipoResidencial',

            form: document.getElementById('formResidencial'),
            inputId: document.getElementById('idResidencial'),
            inputNome: document.getElementById('nomeResidencial'),

            titulo: document.getElementById('tituloResidencial'),
            btnSalvar: document.getElementById('btnSalvarResidencial'),
            btnCancelar: document.getElementById('btnCancelarResidencial'),

            lista: document.getElementById('listaResidencial'),
            filtro: document.getElementById('filtroResidencial'),
            vazio: document.getElementById('vazioResidencial'),

            btnAtualizar: document.getElementById('atualizarResidencial'),

            total: document.getElementById('totalResidencial'),
            ativas: document.getElementById('ativasResidencial'),
            inativas: document.getElementById('inativasResidencial'),

            idCampo: 'id_tipo_residencial',
            nomeNovo: 'Novo tipo residencial'
        },

        comercial: {
            endpoint: '/tipoComercial',

            form: document.getElementById('formComercial'),
            inputId: document.getElementById('idComercial'),
            inputNome: document.getElementById('nomeComercial'),

            titulo: document.getElementById('tituloComercial'),
            btnSalvar: document.getElementById('btnSalvarComercial'),
            btnCancelar: document.getElementById('btnCancelarComercial'),

            lista: document.getElementById('listaComercial'),
            filtro: document.getElementById('filtroComercial'),
            vazio: document.getElementById('vazioComercial'),

            btnAtualizar: document.getElementById('atualizarComercial'),

            total: document.getElementById('totalComercial'),
            ativas: document.getElementById('ativasComercial'),
            inativas: document.getElementById('inativasComercial'),

            idCampo: 'id_tipo_comercial',
            nomeNovo: 'Novo tipo comercial'
        }

    };

    const dados = {
        residencial: [],
        comercial: []
    };

    // =========================================
    // FUNÇÕES AUXILIARES
    // =========================================

    function escaparHTML(valor) {

        return String(valor ?? '').replace(/[&<>"']/g, caractere => ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;'
        })[caractere]);

    }

    function resetarFormulario(tipo) {

        const config = configuracoes[tipo];

        config.form.reset();

        config.inputId.value = '';

        config.titulo.textContent = config.nomeNovo;

        config.btnSalvar.innerHTML =
            '<i class="fa-solid fa-plus"></i> Cadastrar tipo';

        config.btnCancelar.hidden = true;

    }

    // =========================================
    // ATUALIZAR INDICADORES
    // =========================================

    function atualizarResumo(tipo) {

        const config = configuracoes[tipo];

        const lista = dados[tipo];

        const ativas = lista.filter(item => item.ativo === true).length;

        const inativas = lista.filter(item => item.ativo === false).length;

        config.total.textContent = lista.length;

        config.ativas.textContent = ativas;

        config.inativas.textContent = inativas;

    }

    // =========================================
    // RENDERIZAR LISTAGEM
    // =========================================

    function renderizarTipos(tipo) {

        const config = configuracoes[tipo];

        const tipoFiltro = config.filtro.value;

        let exibidos = dados[tipo];

        if (tipoFiltro === 'ativas') {

            exibidos = dados[tipo].filter(item => item.ativo === true);

        }

        if (tipoFiltro === 'inativas') {

            exibidos = dados[tipo].filter(item => item.ativo === false);

        }

        config.lista.innerHTML = '';

        config.vazio.hidden = exibidos.length > 0;

        if (exibidos.length === 0) {
            return;
        }

        config.lista.innerHTML = exibidos.map(item => {

            const id = Number(item[config.idCampo]);

            const nome = escaparHTML(item.tipo);

            const ativo = item.ativo === true;

            return `
                <article class="tipo-item-admin">

                    <div class="tipo-info">

                        <strong>${nome}</strong>

                        <span class="tipo-status ${ativo ? 'ativa' : 'inativa'}">
                            ${ativo ? 'Ativo' : 'Inativo'}
                        </span>

                    </div>

                    <div class="tipo-acoes">

                        ${
                            ativo
                            ? `
                                <button
                                    type="button"
                                    class="btn-editar-tipo"
                                    data-acao="editar"
                                    data-id="${id}">

                                    <i class="fa-solid fa-pen"></i>
                                    Editar

                                </button>
                            `
                            : ''
                        }

                        ${
                            ativo
                            ? `
                                <button
                                    type="button"
                                    class="btn-desativar-tipo"
                                    data-acao="desativar"
                                    data-id="${id}">

                                    <i class="fa-solid fa-ban"></i>
                                    Desativar

                                </button>
                            `
                            : `
                                <button
                                    type="button"
                                    class="btn-reativar-tipo"
                                    data-acao="reativar"
                                    data-id="${id}">

                                    <i class="fa-solid fa-rotate-left"></i>
                                    Reativar

                                </button>
                            `
                        }

                    </div>

                </article>
            `;

        }).join('');

    }

    // =========================================
    // CARREGAR TIPOS
    // =========================================

    async function carregarTipos(tipo) {

        const config = configuracoes[tipo];

        config.lista.innerHTML =
            '<p class="estado-lista">Carregando tipos...</p>';

        config.vazio.hidden = true;

        try {

            const resposta = await BeassiAPI.get(config.endpoint);

            dados[tipo] = Array.isArray(resposta)
                ? resposta
                : beassiArray(resposta);

            dados[tipo].sort((a, b) =>
                String(a.tipo).localeCompare(String(b.tipo), 'pt-BR')
            );

            atualizarResumo(tipo);

            renderizarTipos(tipo);

        } catch (erro) {

            config.lista.innerHTML = `
                <p class="estado-lista" role="alert">
                    Não foi possível carregar os tipos:
                    ${escaparHTML(erro.message)}
                </p>
            `;

            beassiMensagem(null, erro.message, true);

        }

    }

    // =========================================
    // CADASTRAR OU EDITAR
    // =========================================

    Object.keys(configuracoes).forEach(tipo => {

        const config = configuracoes[tipo];

        config.form.addEventListener('submit', async event => {

            event.preventDefault();

            const nome = config.inputNome.value.trim();

            const id = config.inputId.value;

            if (!nome) {

                beassiMensagem(null, 'Informe o nome do tipo.', true);

                config.inputNome.focus();

                return;

            }

            // Verifica se já existe outro tipo com o mesmo nome
            const existente = dados[tipo].some(item =>

                String(item.tipo).trim().toLowerCase() === nome.toLowerCase()
                &&
                String(item[config.idCampo]) !== String(id)

            );

            if (existente) {

                beassiMensagem(
                    null,
                    'Já existe um tipo com esse nome nesta categoria.',
                    true
                );

                return;

            }

            const dadosEnvio = {
                tipo: nome
            };

            config.btnSalvar.disabled = true;

            try {

                if (id) {

                    await BeassiAPI.put(
                        `${config.endpoint}/${encodeURIComponent(id)}`,
                        dadosEnvio
                    );

                    beassiMensagem(null, 'Tipo atualizado com sucesso!');

                } else {

                    await BeassiAPI.post(
                        config.endpoint,
                        dadosEnvio
                    );

                    beassiMensagem(null, 'Tipo cadastrado com sucesso!');

                }

                resetarFormulario(tipo);

                await carregarTipos(tipo);

            } catch (erro) {

                beassiMensagem(null, erro.message, true);

            } finally {

                config.btnSalvar.disabled = false;

            }

        });

    });

    // =========================================
    // AÇÕES DA LISTAGEM
    // =========================================

    Object.keys(configuracoes).forEach(tipo => {

        const config = configuracoes[tipo];

        config.lista.addEventListener('click', async event => {

            const botao = event.target.closest('button[data-acao]');

            if (!botao) return;

            const id = botao.dataset.id;

            const acao = botao.dataset.acao;

            const item = dados[tipo].find(elemento =>

                String(elemento[config.idCampo]) === String(id)

            );

            if (!item) return;

            // =====================================
            // EDITAR
            // =====================================

            if (acao === 'editar') {

                if (item.ativo !== true) {
                    beassiMensagem(null, 'Não é possível editar um tipo inativo. Reative-o primeiro.', true);
                    return;
                }

                config.inputId.value = item[config.idCampo];

                config.inputNome.value = item.tipo;

                config.titulo.textContent = 'Editar tipo';

                config.btnSalvar.innerHTML =
                    '<i class="fa-solid fa-floppy-disk"></i> Salvar alterações';

                config.btnCancelar.hidden = false;

                config.inputNome.focus();

                window.scrollTo({
                    top: 0,
                    behavior: 'smooth'
                });

                return;

            }

            // =====================================
            // DESATIVAR
            // =====================================

            if (acao === 'desativar') {

                const confirmar = confirm(
                    `Deseja realmente desativar o tipo "${item.tipo}"?`
                );

                if (!confirmar) return;

                botao.disabled = true;

                try {

                    await BeassiAPI.delete(
                        `${config.endpoint}/${encodeURIComponent(id)}`
                    );

                    resetarFormulario(tipo);

                    await carregarTipos(tipo);

                    beassiMensagem(null, 'Tipo desativado com sucesso!');

                } catch (erro) {

                    botao.disabled = false;

                    beassiMensagem(null, erro.message, true);

                }

                return;

            }

            // =====================================
            // REATIVAR
            // =====================================

            if (acao === 'reativar') {

                const confirmar = confirm(
                    `Deseja reativar o tipo "${item.tipo}"?`
                );

                if (!confirmar) return;

                botao.disabled = true;

                try {

                    await BeassiAPI.put(
                        `${config.endpoint}/${encodeURIComponent(id)}/reativar`,
                        {}
                    );

                    await carregarTipos(tipo);

                    beassiMensagem(null, 'Tipo reativado com sucesso!');

                } catch (erro) {

                    botao.disabled = false;

                    beassiMensagem(null, erro.message, true);

                }

            }

        });

        // CANCELAR EDIÇÃO
        config.btnCancelar.addEventListener('click', () => {

            resetarFormulario(tipo);

        });

        // FILTRO
        config.filtro.addEventListener('change', () => {

            renderizarTipos(tipo);

        });

        // ATUALIZAR LISTAGEM
        config.btnAtualizar.addEventListener('click', () => {

            carregarTipos(tipo);

        });

    });

    // =========================================
    // ALTERNAR ABAS
    // =========================================

    const abas = document.querySelectorAll('.aba-tipo');

    abas.forEach(aba => {

        aba.addEventListener('click', () => {

            const tipoSelecionado = aba.dataset.tipo;

            abas.forEach(item => {

                const selecionada = item === aba;

                item.classList.toggle('ativa', selecionada);

                item.setAttribute(
                    'aria-selected',
                    String(selecionada)
                );

            });

            document.querySelectorAll('.conteudo-tipo').forEach(conteudo => {

                conteudo.classList.remove('ativo');

                conteudo.hidden = true;

            });

            const conteudoAtivo = document.getElementById(
                tipoSelecionado === 'residencial'
                    ? 'conteudoResidencial'
                    : 'conteudoComercial'
            );

            conteudoAtivo.hidden = false;

            conteudoAtivo.classList.add('ativo');

        });

    });

    // =========================================
    // INICIALIZAÇÃO
    // =========================================

    carregarTipos('residencial');

    carregarTipos('comercial');

});
