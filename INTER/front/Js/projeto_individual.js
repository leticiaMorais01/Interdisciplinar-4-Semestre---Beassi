
document.addEventListener('DOMContentLoaded', async () => {

    // ==========================================
    // 1. IDENTIFICAÇÃO DO PROJETO
    // ==========================================

    const parametros = new URLSearchParams(window.location.search);
    const id = parametros.get('projeto') || parametros.get('id');

    const titulo = document.getElementById('tituloProjeto');
    const descricao = document.getElementById('descricaoProjeto');
    const tipoProjeto = document.getElementById('tipoProjeto');
    const classificacao = document.getElementById('classificacaoProjeto');

    const principal = document.getElementById('imagemPrincipal');
    const miniaturas = document.getElementById('miniaturasProjeto');
    const categorias = document.getElementById('categoriasProjeto');

    if (!id) {
        if (titulo) titulo.textContent = 'Projeto não informado';
        return;
    }

    try {

        // ==========================================
        // 2. BUSCAR DADOS DO PROJETO
        // ==========================================

        const p = await BeassiAPI.get(
            `/projeto/${encodeURIComponent(id)}`
        );

        // Busca as imagens relacionadas ao projeto
        const todasImagens = await BeassiAPI.get('/imagem');

        p.imagens = todasImagens.filter(imagem =>
            Number(imagem.projeto?.id_projeto) === Number(id)
        );

        // Busca as categorias relacionadas ao projeto
        const todasAssociacoes = await BeassiAPI.get('/projetoCategoria');

        const associacoes = todasAssociacoes.filter(associacao =>
            Number(
                associacao.projeto?.id_projeto ??
                associacao.id?.projetoId
            ) === Number(id)
        );

        p.categorias = associacoes
            .map(associacao => associacao.categoria)
            .filter(Boolean);


        // ==========================================
        // 3. EXIBIR INFORMAÇÕES DO PROJETO
        // ==========================================

        document.title = `${p.titulo} | BEASSI Arquitetura`;

        if (titulo) titulo.textContent = p.titulo ?? '';

        if (descricao) descricao.textContent = p.descricao ?? '';

        if (tipoProjeto) {
            tipoProjeto.textContent = p.tipoResidencial
                ? 'Residencial'
                : 'Comercial';
        }

        if (classificacao) {
            classificacao.textContent =
                p.tipoResidencial?.tipo ??
                p.tipoComercial?.tipo ??
                '';
        }


        // ==========================================
        // 4. CONFIGURAR GALERIA DE IMAGENS
        // ==========================================

        const imagens = p.imagens || [];

        // Monta o endereço completo da imagem
        function obterCaminhoImagem(imagem) {

            const caminho = String(imagem.caminho ?? imagem);

            if (caminho.startsWith('/')) {
                return `${window.BEASSI_API_BASE_URL || 'http://localhost:8080'}${caminho}`;
            }

            return caminho;
        }

        if (principal && miniaturas) {

            // Limpa as miniaturas anteriores
            miniaturas.replaceChildren();

            if (imagens.length > 0) {

                // Define a primeira imagem como principal
                principal.src = obterCaminhoImagem(imagens[0]);
                principal.alt = `Imagem de ${p.titulo}`;

                // Cria as miniaturas
                imagens.forEach((imagem, indice) => {

                    const caminho = obterCaminhoImagem(imagem);

                    // Botão da miniatura
                    const botao = document.createElement('button');

                    botao.type = 'button';
                    botao.className = `miniatura${indice === 0 ? ' ativa' : ''}`;

                    botao.setAttribute(
                        'aria-label',
                        `Ver imagem ${indice + 1} de ${p.titulo}`
                    );

                    // Imagem da miniatura
                    const foto = document.createElement('img');

                    foto.src = caminho;
                    foto.alt = `Imagem ${indice + 1} de ${p.titulo}`;

                    botao.appendChild(foto);

                    // Troca a imagem principal ao clicar
                    botao.addEventListener('click', () => {

                        principal.src = caminho;
                        principal.alt = foto.alt;

                        // Atualiza a miniatura selecionada
                        miniaturas
                            .querySelectorAll('.miniatura')
                            .forEach(miniatura => {
                                miniatura.classList.remove('ativa');
                            });

                        botao.classList.add('ativa');
                    });

                    miniaturas.appendChild(botao);
                });
            }
        }


        // ==========================================
        // 5. EXIBIR CATEGORIAS DO PROJETO
        // ==========================================

        if (categorias) {

            categorias.replaceChildren();

            (p.categorias || []).forEach(categoria => {

                const tag = document.createElement('span');

                tag.className = 'etiqueta-categoria';

                tag.textContent =
                    categoria.categoria ??
                    categoria.nome ??
                    categoria;

                categorias.appendChild(tag);
            });
        }

    } catch (erro) {

        // ==========================================
        // 6. TRATAMENTO DE ERROS
        // ==========================================

        console.error('Erro ao carregar projeto:', erro);

        if (titulo) {
            titulo.textContent = 'Não foi possível carregar o projeto';
        }

        if (descricao) {
            descricao.textContent = erro.message;
        }
    }
});
