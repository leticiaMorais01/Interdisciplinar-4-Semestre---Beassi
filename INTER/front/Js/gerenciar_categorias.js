
document.addEventListener('DOMContentLoaded', () => {

    const form = document.getElementById('formCategoria');
    const inputId = document.getElementById('idCategoria');
    const inputNome = document.getElementById('nomeCategoria');

    const tituloFormulario = document.getElementById('tituloFormulario');
    const btnSalvar = document.getElementById('btnSalvar');
    const btnCancelar = document.getElementById('btnCancelar');

    const lista = document.getElementById('listaCategorias');
    const filtro = document.getElementById('filtroCategoria');

    const totalCategorias = document.getElementById('totalCategorias');
    const totalAtivas = document.getElementById('totalAtivas');
    const totalInativas = document.getElementById('totalInativas');

    const btnAtualizar = document.getElementById('btnAtualizar');
    const vazio = document.getElementById('categoriaVazio');

    let categorias = [];

    function escaparHTML(valor) {
        return String(valor ?? '').replace(/[&<>"']/g, caractere => ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;'
        })[caractere]);
    }

    function resetarFormulario() {
        form.reset();

        inputId.value = '';

        tituloFormulario.textContent = 'Nova categoria';

        btnSalvar.innerHTML = '<i class="fa-solid fa-plus"></i> Cadastrar categoria';

        btnCancelar.hidden = true;
    }

    function atualizarResumo() {

        const ativas = categorias.filter(c => c.ativo === true).length;
        const inativas = categorias.filter(c => c.ativo === false).length;

        totalCategorias.textContent = categorias.length;
        totalAtivas.textContent = ativas;
        totalInativas.textContent = inativas;
    }

    function renderizarCategorias() {

        const tipoFiltro = filtro.value;

        let exibidas = categorias;

        if (tipoFiltro === 'ativas') {
            exibidas = categorias.filter(c => c.ativo === true);
        }

        if (tipoFiltro === 'inativas') {
            exibidas = categorias.filter(c => c.ativo === false);
        }

        lista.innerHTML = '';

        vazio.hidden = exibidas.length > 0;

        if (exibidas.length === 0) {
            return;
        }

        lista.innerHTML = exibidas.map(categoria => {

            const id = Number(categoria.id_categoria);
            const nome = escaparHTML(categoria.categoria);
            const ativa = categoria.ativo === true;

            return `
                <article class="categoria-item-admin">

                    <div class="categoria-info">

                        <strong>${nome}</strong>

                        <span class="categoria-status ${ativa ? 'ativa' : 'inativa'}">
                            ${ativa ? 'Ativa' : 'Inativa'}
                        </span>

                    </div>

                    <div class="categoria-acoes">

                        ${
                            ativa
                            ? `
                                <button
                                    type="button"
                                    class="btn-editar-categoria"
                                    data-acao="editar"
                                    data-id="${id}">
                                    <i class="fa-solid fa-pen"></i>
                                    Editar
                                </button>
                            `
                            : ''
                        }

                        ${
                            ativa
                            ? `
                                <button
                                    type="button"
                                    class="btn-desativar-categoria"
                                    data-acao="desativar"
                                    data-id="${id}">
                                    <i class="fa-solid fa-ban"></i>
                                    Desativar
                                </button>
                            `
                            : `
                                <button
                                    type="button"
                                    class="btn-reativar-categoria"
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

    async function carregarCategorias() {

        lista.innerHTML = '<p class="estado-lista">Carregando categorias...</p>';
        vazio.hidden = true;

        try {

            const resposta = await BeassiAPI.get('/categoria');

            categorias = Array.isArray(resposta) ? resposta : beassiArray(resposta);

            categorias.sort((a, b) =>
                String(a.categoria).localeCompare(String(b.categoria), 'pt-BR')
            );

            atualizarResumo();
            renderizarCategorias();

        } catch (erro) {

            lista.innerHTML = `
                <p class="estado-lista" role="alert">
                    Não foi possível carregar as categorias: ${escaparHTML(erro.message)}
                </p>
            `;

            beassiMensagem(null, erro.message, true);
        }
    }

    // CADASTRAR OU EDITAR
    form.addEventListener('submit', async event => {

        event.preventDefault();

        const nome = inputNome.value.trim();
        const id = inputId.value;

        if (!nome) {
            beassiMensagem(null, 'Informe o nome da categoria.', true);
            inputNome.focus();
            return;
        }

        const categoriaExistente = categorias.some(c =>
            String(c.categoria).trim().toLowerCase() === nome.toLowerCase()
            && String(c.id_categoria) !== String(id)
        );

        if (categoriaExistente) {
            beassiMensagem(null, 'Já existe uma categoria com esse nome.', true);
            return;
        }

        const dados = {
            categoria: nome
        };

        btnSalvar.disabled = true;

        try {

            if (id) {

                await BeassiAPI.put(`/categoria/${encodeURIComponent(id)}`, dados);

                beassiMensagem(null, 'Categoria atualizada com sucesso!');

            } else {

                await BeassiAPI.post('/categoria', dados);

                beassiMensagem(null, 'Categoria cadastrada com sucesso!');

            }

            resetarFormulario();

            await carregarCategorias();

        } catch (erro) {

            beassiMensagem(null, erro.message, true);

        } finally {

            btnSalvar.disabled = false;

        }
    });

    // AÇÕES DA LISTAGEM
    lista.addEventListener('click', async event => {

        const botao = event.target.closest('button[data-acao]');

        if (!botao) return;

        const id = botao.dataset.id;
        const acao = botao.dataset.acao;

        const categoria = categorias.find(c =>
            String(c.id_categoria) === String(id)
        );

        if (!categoria) return;

        if (acao === 'editar') {

            // Impede a edição de categorias inativas.
            if (categoria.ativo !== true) {
                beassiMensagem(null, 'Não é possível editar uma categoria inativa. Reative-a primeiro.', true);
                return;
            }

            inputId.value = categoria.id_categoria;
            inputNome.value = categoria.categoria;

            tituloFormulario.textContent = 'Editar categoria';

            btnSalvar.innerHTML = '<i class="fa-solid fa-floppy-disk"></i> Salvar alterações';

            btnCancelar.hidden = false;

            inputNome.focus();

            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });

            return;
        }

        if (acao === 'desativar') {

            const confirmar = confirm(
                `Deseja realmente desativar a categoria "${categoria.categoria}"?`
            );

            if (!confirmar) return;

            botao.disabled = true;

            try {

                await BeassiAPI.delete(`/categoria/${encodeURIComponent(id)}`);

                resetarFormulario();

                await carregarCategorias();

                beassiMensagem(null, 'Categoria desativada com sucesso!');

            } catch (erro) {

                botao.disabled = false;

                beassiMensagem(null, erro.message, true);

            }

            return;
        }

        if (acao === 'reativar') {

            const confirmar = confirm(
                `Deseja reativar a categoria "${categoria.categoria}"?`
            );

            if (!confirmar) return;

            botao.disabled = true;

            try {

                await BeassiAPI.put(
                    `/categoria/${encodeURIComponent(id)}/reativar`,
                    {}
                );

                await carregarCategorias();

                beassiMensagem(null, 'Categoria reativada com sucesso!');

            } catch (erro) {

                botao.disabled = false;

                beassiMensagem(null, erro.message, true);

            }
        }

    });

    // CANCELAR EDIÇÃO
    btnCancelar.addEventListener('click', resetarFormulario);

    // FILTRO
    filtro.addEventListener('change', renderizarCategorias);

    // ATUALIZAR LISTAGEM
    btnAtualizar.addEventListener('click', carregarCategorias);

    // INICIALIZAÇÃO
    carregarCategorias();

});
