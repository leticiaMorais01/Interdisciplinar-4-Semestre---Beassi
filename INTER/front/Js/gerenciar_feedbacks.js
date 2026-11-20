
document.addEventListener('DOMContentLoaded', async () => {
    const lista = document.getElementById('listaFeedbacksAdmin');

    if (!lista || !window.BeassiAPI) return;

    const filtro = document.getElementById('filtroStatus');
    const vazio = document.getElementById('feedbackAdminVazio');

    const esc = valor =>
        String(valor ?? '').replace(/[&<>"']/g, caractere => ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;'
        }[caractere]));

    // Atualiza os botões conforme o status atual
    const atualizarBotoes = (item, status) => {
        const aprovar = item.querySelector('.feedback-aprovar');
        const rejeitar = item.querySelector('.feedback-rejeitar');

        aprovar.classList.toggle('selecionado', status === 'aprovado');
        rejeitar.classList.toggle('selecionado', status === 'rejeitado');

        aprovar.disabled = status === 'aprovado';
        rejeitar.disabled = status === 'rejeitado';
    };

    // Atualiza indicadores e filtro
    const atualizar = () => {
        const itens = [...lista.querySelectorAll('.feedback-admin-item')];

        const totais = {
            pendente: 'totalPendentes',
            aprovado: 'totalAprovados',
            rejeitado: 'totalRejeitados'
        };

        Object.entries(totais).forEach(([status, id]) => {
            const elemento = document.getElementById(id);

            if (elemento) {
                elemento.textContent = itens.filter(
                    item => item.dataset.status === status
                ).length;
            }
        });

        let visiveis = 0;

        itens.forEach(item => {
            item.hidden =
                filtro && filtro.value !== 'todos' &&
                item.dataset.status !== filtro.value;

            if (!item.hidden) visiveis++;
        });

        if (vazio) {
            vazio.hidden = visiveis > 0;
        }
    };

    try {
        const payload = await BeassiAPI.get('/Feedback');

        const items = Array.isArray(payload)
            ? payload
            : (payload?.content || payload?.items || []);

        lista.innerHTML = items.map(feedback => {
            const id =
                feedback.id_feedback ??
                feedback.idFeedback ??
                feedback.id;

            const status = String(
                feedback.status_aprovacao ??
                feedback.statusAprovacao ??
                feedback.status ??
                'PENDENTE'
            ).trim().toLowerCase();

            const nota = Number(feedback.nota ?? 0);

            const nome = (feedback.exibir_nome ?? feedback.exibirNome)
                ? (feedback.nomeCliente ?? feedback.cliente?.nome ?? 'Cliente')
                : 'Cliente BEASSI';

            const data =
                feedback.data_envio ??
                feedback.dataEnvioFormatada ??
                feedback.dataEnvio ??
                '';

            return `
                <article
                    class="feedback-admin-item"
                    data-id="${esc(id)}"
                    data-status="${esc(status)}"
                >
                    <div class="feedback-admin-item-topo">
                        <div>
                            <span class="feedback-admin-autor">
                                ${esc(nome)}
                            </span>

                            <span class="feedback-admin-data">
                                ${esc(data)}
                            </span>
                        </div>

                        <span class="feedback-status ${esc(status)}">
                            ${esc(status.toUpperCase())}
                        </span>
                    </div>

                    <div
                        class="feedback-admin-estrelas"
                        aria-label="${nota} de 5 estrelas"
                    >
                        ${'★'.repeat(Math.max(0, Math.min(5, nota)))}
                        ${'☆'.repeat(5 - Math.max(0, Math.min(5, nota)))}
                    </div>

                    <p class="feedback-admin-mensagem">
                        ${esc(feedback.comentario ?? '')}
                    </p>

                    <div class="feedback-admin-acoes">
                        <button type="button" class="feedback-aprovar">
                            Aprovar
                        </button>

                        <button type="button" class="feedback-rejeitar">
                            Rejeitar
                        </button>
                    </div>
                </article>
            `;
        }).join('') || '<p>Nenhuma avaliação recebida.</p>';

        // Define o estado inicial dos botões
        lista.querySelectorAll('.feedback-admin-item').forEach(item => {
            atualizarBotoes(item, item.dataset.status);
        });

        atualizar();

    } catch (erro) {
        lista.innerHTML = `
            <p role="alert">
                Não foi possível carregar as avaliações:
                ${esc(erro.message)}
            </p>
        `;
    }

    filtro?.addEventListener('change', atualizar);

    // Aprovar ou rejeitar feedback
    lista.addEventListener('click', async evento => {
        const btn = evento.target.closest('button');

        if (!btn) return;

        const item = btn.closest('.feedback-admin-item');

        if (!item) return;

        const novoStatus = btn.classList.contains('feedback-aprovar')
            ? 'APROVADO'
            : btn.classList.contains('feedback-rejeitar')
                ? 'REJEITADO'
                : null;

        if (!novoStatus) return;

        const botoes = item.querySelectorAll('button');

        // Evita cliques repetidos durante a requisição
        botoes.forEach(botao => botao.disabled = true);

        try {
            await BeassiAPI.patch(
                `/Feedback/${encodeURIComponent(item.dataset.id)}/status?status=${encodeURIComponent(novoStatus)}`,
                null
            );

            const status = novoStatus.toLowerCase();

            item.dataset.status = status;

            const tag = item.querySelector('.feedback-status');

            tag.textContent = novoStatus;
            tag.className = `feedback-status ${status}`;

            atualizarBotoes(item, status);

            atualizar();

        } catch (erro) {
            // Restaura os botões conforme o status anterior
            atualizarBotoes(item, item.dataset.status);

            alert(erro.message);
        }
    });
});
