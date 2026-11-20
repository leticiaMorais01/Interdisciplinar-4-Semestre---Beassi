
document.addEventListener('DOMContentLoaded', async () => {
    const lista = document.getElementById('listaMeusProjetos');

    if (!lista || !window.BeassiAPI) return;

    const nomeCliente = document.getElementById('nomeCliente');
    const emailCliente = document.getElementById('emailCliente');
    const contador = document.getElementById('quantidadeProjetos');

    // Carrega e exibe as avaliações enviadas pelo cliente
    async function carregarMinhasAvaliacoes() {
        const listaAvaliacoes = document.getElementById('listaMinhasAvaliacoes');

        if (!listaAvaliacoes) return;

        try {
            const avaliacoes = await BeassiAPI.get('/Feedback/meus');

            if (!Array.isArray(avaliacoes) || avaliacoes.length === 0) {
                listaAvaliacoes.innerHTML = `
                    <div class="cliente-vazio">
                        <h3>Você ainda não enviou avaliações</h3>
                        <p>Quando você compartilhar sua experiência, poderá acompanhar a análise por aqui.</p>
                    </div>
                `;
                return;
            }

            const mensagensStatus = {
                PENDENTE: 'Sua avaliação está aguardando análise.',
                APROVADO: 'Sua avaliação foi aprovada e poderá aparecer no site.',
                REJEITADO: 'Sua avaliação não foi aprovada para publicação.'
            };

            listaAvaliacoes.innerHTML = avaliacoes.map(feedback => {
                const status = String(feedback.status_aprovacao || 'PENDENTE').toUpperCase();

                const mensagem = mensagensStatus[status] ||
                    'Não foi possível identificar o status da avaliação.';

                const nomeProjeto = feedback.projeto?.titulo || 'Projeto';

                const data = feedback.data_envio
                    ? new Date(feedback.data_envio).toLocaleDateString('pt-BR')
                    : 'Data não informada';

                const estrelas = '★'.repeat(
                    Math.max(0, Math.min(5, Number(feedback.nota) || 0))
                );

                return `
                    <article class="cliente-avaliacao-card">

                        <div class="cliente-avaliacao-topo">
                            <div>
                                <span class="cliente-label">PROJETO</span>
                                <h3>${escaparHTML(nomeProjeto)}</h3>
                            </div>

                            <span class="cliente-avaliacao-status status-${escaparHTML(status.toLowerCase())}">
                                ${escaparHTML(status)}
                            </span>
                        </div>

                        <div class="cliente-avaliacao-estrelas"
                             aria-label="Nota ${Number(feedback.nota) || 0} de 5">
                            ${estrelas}
                        </div>

                        <p class="cliente-avaliacao-comentario">
                            "${escaparHTML(feedback.comentario || 'Sem comentário.')}"
                        </p>

                        <p class="cliente-avaliacao-data">
                            Enviada em: ${escaparHTML(data)}
                        </p>

                        <div class="cliente-avaliacao-mensagem">
                            ${escaparHTML(mensagem)}
                        </div>

                    </article>
                `;
            }).join('');

        } catch (erro) {
            console.error('Erro ao carregar avaliações:', erro);

            listaAvaliacoes.innerHTML = `
                <p role="alert">
                    Não foi possível carregar suas avaliações no momento.
                </p>
            `;
        }
    }

    // Converte valores para texto seguro no HTML
    const escaparHTML = valor => String(valor ?? '').replace(/[&<>"']/g, caractere => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[caractere]));

    // Monta o caminho completo da imagem
    const caminhoCompletoImagem = caminho => {
        if (!caminho) return '';

        return caminho.startsWith('/')
            ? `${window.BEASSI_API_BASE_URL || 'http://localhost:8080'}${caminho}`
            : caminho;
    };

    try {
        // Recupera a sessão do cliente
        const sessao = await BeassiAPI.get('/Login/sessao');

        carregarMinhasAvaliacoes();


        const btnInativarConta = document.getElementById('btnInativarConta');

        if (btnInativarConta) {
            btnInativarConta.addEventListener('click', async () => {

                const confirmar = window.confirm(
                    'Tem certeza de que deseja inativar sua conta?\n\n' +
                    'Você perderá imediatamente o acesso ao sistema. ' +
                    'Seus projetos e avaliações serão preservados.\n\n' +
                    'IMPORTANTE: Caso queira voltar a acessar sua conta futuramente, ' +
                    'será necessário entrar em contato com o administrador para solicitar a reativação.\n\n' +
                    'WhatsApp: (17) 99660-9696\n' +
                    'Instagram: @beassi.arq\n\n' +
                    'Deseja realmente continuar?'
                );

                if (!confirmar) return;

                btnInativarConta.disabled = true;
                btnInativarConta.textContent = 'Inativando...';

                try {
                    await BeassiAPI.post('/Cliente/minha-conta/inativar');

                    window.location.href = 'login.html';

                } catch (erro) {
                    console.error('Erro ao inativar conta:', erro);

                    btnInativarConta.disabled = false;
                    btnInativarConta.innerHTML =
                        '<i class="fa-solid fa-user-slash" aria-hidden="true"></i> Inativar minha conta';

                    beassiMensagem(
                        null,
                        erro.message || 'Não foi possível inativar sua conta. Tente novamente.',
                        true
                    );
                }
            });
        }


        // Carrega as informações reais da conta
        try {
            const cliente = await BeassiAPI.get(
                `/Cliente/${sessao.idUsuario}`
            );

            if (nomeCliente) {
                nomeCliente.textContent = `Olá, ${cliente.nome || 'cliente'}!`;
            }

            if (emailCliente) {
                emailCliente.textContent = cliente.email || '';
            }

        } catch (erro) {
            if (nomeCliente) nomeCliente.textContent = 'Olá!';

            if (emailCliente) {
                emailCliente.textContent = 'Não foi possível carregar seus dados.';
            }

            console.error('Erro ao carregar dados do cliente:', erro);
        }

        // Carrega os projetos vinculados ao cliente
        const payload = await BeassiAPI.get(
            `/funcoes/clientes/${sessao.idUsuario}/projetos`
        );

        const projetos = Array.isArray(payload)
            ? payload
            : (payload?.content || payload?.items || []);

        if (contador) {
            contador.textContent = `${projetos.length} ${
                projetos.length === 1 ? 'projeto' : 'projetos'
            }`;
        }

        if (!projetos.length) {
            lista.innerHTML = `
                <div class="cliente-vazio">
                    <h3>Nenhum projeto disponível no momento</h3>
                    <p>Quando houver um projeto vinculado à sua conta, ele aparecerá nesta área.</p>
                    <a href="index.html#contato" class="cliente-contato">
                        ENTRAR EM CONTATO
                    </a>
                </div>
            `;
            return;
        }

        // Busca as imagens cadastradas
        let imagens = [];

        try {
            const respostaImagens = await BeassiAPI.get('/imagem');

            imagens = Array.isArray(respostaImagens)
                ? respostaImagens
                : (respostaImagens?.content || respostaImagens?.items || []);

        } catch (erro) {
            console.warn('Não foi possível carregar as imagens dos projetos:', erro);
        }

        // Relaciona as imagens aos respectivos projetos
        const imagensPorProjeto = new Map();

        imagens.forEach(imagem => {
            const idProjeto = Number(imagem.projeto?.id_projeto);

            if (!idProjeto) return;

            if (!imagensPorProjeto.has(idProjeto)) {
                imagensPorProjeto.set(idProjeto, []);
            }

            imagensPorProjeto.get(idProjeto).push(imagem);
        });

        // Monta os cards dos projetos
        lista.innerHTML = projetos.map(p => {
            const id = p.id_projeto ?? p.idProjeto ?? p.id;

            const pct = Number(
                p.porcentagem_conclusao ??
                p.porcentagemConclusao ??
                p.percentualConclusao ??
                0
            );

            const etapa = p.etapa_atual ?? p.etapaAtual ?? 'Não iniciado';

            // Recupera a primeira imagem do projeto
            const imagemProjeto = imagensPorProjeto.get(Number(id))?.[0];
            const caminhoImagem = imagemProjeto?.caminho ?? '';
            const imagem = caminhoCompletoImagem(caminhoImagem);

            return `
                <article class="cliente-projeto-card">

                    <div class="cliente-projeto-imagem">
                        ${imagem ? `
                            <a
                                href="projeto_individual.html?projeto=${encodeURIComponent(id)}"
                                class="cliente-projeto-imagem-link"
                                aria-label="Ver detalhes do projeto ${escaparHTML(p.titulo)}"
                            >
                                <img
                                    src="${escaparHTML(imagem)}"
                                    alt="Imagem do projeto ${escaparHTML(p.titulo)}"
                                    onerror="this.style.display='none'"
                                >
                            </a>
                        ` : `
                            <div class="cliente-projeto-sem-imagem">
                                Imagem não disponível
                            </div>
                        `}
                    </div>

                    <div class="cliente-projeto-conteudo">

                        <div class="cliente-projeto-topo">
                            <div>
                                <span class="cliente-label">
                                    ${escaparHTML(p.tipo ?? 'PROJETO')}
                                </span>

                                <h3>${escaparHTML(p.titulo ?? 'Projeto')}</h3>
                            </div>

                            <span class="cliente-projeto-status">
                                ${pct === 100 ? 'Concluído' :
                                  pct === 0 ? 'Não iniciado' :
                                  'Em andamento'}
                            </span>
                        </div>

                        <p class="cliente-projeto-etapa">
                            Etapa atual: <strong>${escaparHTML(etapa)}</strong>
                        </p>

                        <div class="cliente-progresso-cabecalho">
                            <span>Progresso do projeto</span>
                            <strong>${pct}%</strong>
                        </div>

                        <div class="cliente-progresso-barra"
                             role="progressbar"
                             aria-valuemin="0"
                             aria-valuemax="100"
                             aria-valuenow="${pct}">

                            <div class="cliente-progresso-preenchimento"
                                 style="width: ${pct}%">
                            </div>
                        </div>

                        <p class="cliente-projeto-observacao">
                            O escritório atualizará o andamento conforme o desenvolvimento do projeto.
                        </p>

                    </div>
                </article>
            `;
        }).join('');

    } catch (erro) {
        lista.innerHTML = `
            <p role="alert">
                ${escaparHTML(erro.message)}
                Verifique se você está conectado e se o Back-End está em execução.
            </p>
        `;

        if (nomeCliente) nomeCliente.textContent = 'Olá!';
        if (emailCliente) emailCliente.textContent = '';
    }
});
