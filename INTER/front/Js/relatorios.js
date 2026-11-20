
/* RELATÓRIOS E DASHBOARDS BEASSI
   O navegador conversa com a API Java;
   não executa Views, Procedures ou Functions diretamente.
*/

(() => {

  // =====================================================
  // CONFIGURAÇÃO DOS RELATÓRIOS EXISTENTES
  // =====================================================

  const configuracao = {
    projetos: {
      endpoint: '/relatorios/projetos',
      alvo: 'resultadoProjetos'
    },
    clientes: {
      endpoint: '/relatorios/clientes',
      alvo: 'resultadoClientes'
    },
    feedbacks: {
      endpoint: '/relatorios/feedbacks',
      alvo: 'resultadoFeedbacks'
    }
  };


  // =====================================================
  // FUNÇÕES AUXILIARES
  // =====================================================

  const escapar = valor => String(valor ?? '').replace(/[&<>"']/g, caractere => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[caractere]));

  function obterLinhas(resposta) {
    if (Array.isArray(resposta)) return resposta;

    return resposta?.content ||
           resposta?.items ||
           resposta?.data ||
           resposta?.resultados ||
           [];
  }

  function formatarData(valor, coluna) {

    if (valor == null || valor === '') return valor;

    const nomeColuna = coluna.toLowerCase();

    if (!nomeColuna.includes('data')) return valor;

    const data = String(valor);

    const correspondencia = data.match(
      /^(\d{4})-(\d{2})-(\d{2})(?:T(\d{2}):(\d{2})(?::(\d{2}))?)?/
    );

    if (!correspondencia) return valor;

    const [, ano, mes, dia] = correspondencia;

    return `${dia}/${mes}/${ano}`;
  }

  function formatarNumero(valor, casas = 2) {

    if (valor == null || valor === '') return '—';

    return Number(valor).toLocaleString('pt-BR', {
      minimumFractionDigits: casas,
      maximumFractionDigits: casas
    });
  }


  // =====================================================
  // MONTAGEM DAS TABELAS DOS RELATÓRIOS EXISTENTES
  // =====================================================

  function montarTabela(linhas) {

    if (!linhas.length) {
      return '<p class="relatorio-vazio">Nenhum registro encontrado.</p>';
    }

    const colunas = Object.keys(linhas[0]);

    if (!colunas.length) {
      return '<p class="relatorio-vazio">A consulta não retornou colunas para exibição.</p>';
    }

    const cabecalho = colunas
      .map(coluna => `<th>${escapar(coluna)}</th>`)
      .join('');

    const corpo = linhas.map(linha => {

      const celulas = colunas.map(coluna => {

        const valor = formatarData(linha[coluna], coluna);

        return `<td>${escapar(valor)}</td>`;

      }).join('');

      return `<tr>${celulas}</tr>`;

    }).join('');

    return `
      <div class="relatorio-tabela-wrap">
        <table class="relatorio-tabela">
          <thead>
            <tr>${cabecalho}</tr>
          </thead>
          <tbody>
            ${corpo}
          </tbody>
        </table>
      </div>
    `;
  }


  // =====================================================
  // GERAR RELATÓRIOS EXISTENTES
  // =====================================================

  async function gerar(tipo) {

    const item = configuracao[tipo];
    const alvo = document.getElementById(item.alvo);
    const status = document.getElementById('statusRelatorios');
    const botao = document.querySelector(`[data-relatorio="${tipo}"]`);

    const textoOriginal = botao.innerHTML;

    botao.disabled = true;
    botao.innerHTML = 'Carregando...';

    alvo.innerHTML = '<p class="relatorio-placeholder">Consultando informações...</p>';

    status.textContent = `Carregando relatório de ${tipo}...`;

    try {

      const resposta = await BeassiAPI.get(item.endpoint);
      const linhas = obterLinhas(resposta);

      alvo.innerHTML = montarTabela(linhas);

      status.textContent = `Relatório de ${tipo} atualizado.`;

    } catch (erro) {

      alvo.innerHTML = `
        <p class="relatorio-erro">
          ${escapar(erro.message || 'Não foi possível carregar este relatório.')}
        </p>
      `;

      status.textContent = `Não foi possível carregar o relatório de ${tipo}.`;

    } finally {

      botao.disabled = false;
      botao.innerHTML = textoOriginal;

    }
  }


  // =====================================================
  // DASHBOARD 01 - RESUMO DE FEEDBACKS
  // Utiliza fc_ResumoFeedbackProjetos()
  // =====================================================

  async function gerarDashboardResumo() {

    const alvo = document.getElementById('resultadoDashboardResumo');
    const botao = document.getElementById('gerarDashboardResumo');
    const status = document.getElementById('statusRelatorios');

    const textoOriginal = botao.innerHTML;

    botao.disabled = true;
    botao.innerHTML = 'Carregando...';

    alvo.innerHTML = '<p class="relatorio-placeholder">Consultando feedbacks...</p>';

    status.textContent = 'Carregando resumo de feedbacks...';

    try {

      const resposta = await BeassiAPI.get(
        '/funcoes/projetos/resumo-feedbacks'
      );

      const linhas = obterLinhas(resposta);

      if (!linhas.length) {

        alvo.innerHTML = `
          <p class="relatorio-vazio">
            Ainda não existem feedbacks cadastrados.
          </p>
        `;

        status.textContent = 'Resumo de feedbacks atualizado.';
        return;
      }

      // Calcula o total de feedbacks.
      const totalFeedbacks = linhas.reduce(
        (total, item) => total + Number(item.total_feedbacks || 0),
        0
      );

      // Calcula a média geral ponderada pela quantidade de feedbacks.
      const somaNotas = linhas.reduce(
        (total, item) =>
          total +
          (Number(item.media_notas || 0) *
           Number(item.total_feedbacks || 0)),
        0
      );

      const mediaGeral = totalFeedbacks > 0
        ? somaNotas / totalFeedbacks
        : null;

      const tabela = linhas.map(item => `
        <tr>
          <td>${escapar(item.titulo)}</td>
          <td>${escapar(item.total_feedbacks)}</td>
          <td>${formatarNumero(item.media_notas)}</td>
        </tr>
      `).join('');

      alvo.innerHTML = `
        <div class="relatorio-indicadores">

          <div class="relatorio-indicador">
            <strong>${totalFeedbacks}</strong>
            <span>Total de feedbacks</span>
          </div>

          <div class="relatorio-indicador">
            <strong>${formatarNumero(mediaGeral)}</strong>
            <span>Média geral das avaliações</span>
          </div>

        </div>

        <h3 class="dashboard-subtitulo">Resumo por projeto</h3>

        <div class="relatorio-tabela-wrap">
          <table class="relatorio-tabela">
            <thead>
              <tr>
                <th>Projeto</th>
                <th>Feedbacks</th>
                <th>Média</th>
              </tr>
            </thead>

            <tbody>
              ${tabela}
            </tbody>
          </table>
        </div>
      `;

      status.textContent = 'Dashboard de resumo atualizado.';

    } catch (erro) {

      alvo.innerHTML = `
        <p class="relatorio-erro">
          ${escapar(erro.message || 'Não foi possível carregar o resumo de feedbacks.')}
        </p>
      `;

      status.textContent = 'Erro ao carregar o dashboard de resumo.';

    } finally {

      botao.disabled = false;
      botao.innerHTML = textoOriginal;

    }
  }


  // =====================================================
  // DASHBOARD 02 - SATISFAÇÃO DOS PROJETOS
  // Utiliza fc_PercentualAvaliacoesPositivasProjeto()
  // =====================================================

  
async function gerarDashboardSatisfacao() {

    const alvo = document.getElementById('resultadoDashboardSatisfacao');
    const botao = document.getElementById('gerarDashboardSatisfacao');
    const status = document.getElementById('statusRelatorios');

    const textoOriginal = botao.innerHTML;

    botao.disabled = true;
    botao.innerHTML = 'Carregando...';

    alvo.innerHTML = '<p class="relatorio-placeholder">Consultando avaliações dos projetos...</p>';

    status.textContent = 'Carregando satisfação dos projetos...';

    try {

        // Busca todos os projetos cadastrados.
        const respostaProjetos = await BeassiAPI.get('/projeto');
        const projetos = obterLinhas(respostaProjetos);

        if (!projetos.length) {
            alvo.innerHTML = `
                <p class="relatorio-vazio">
                    Nenhum projeto cadastrado.
                </p>
            `;

            status.textContent = 'Nenhum projeto encontrado.';
            return;
        }

        // Busca o resumo dos projetos que possuem feedbacks.
        const respostaResumo = await BeassiAPI.get(
            '/funcoes/projetos/resumo-feedbacks'
        );

        const resumos = obterLinhas(respostaResumo);

        // Organiza os resumos pelo ID do projeto.
        const mapaResumos = new Map(
            resumos.map(item => [
                Number(item.id_projeto),
                item
            ])
        );

        // Monta os dados da tabela.
        const resultados = await Promise.all(

            projetos.map(async projeto => {

                const resumo = mapaResumos.get(
                    Number(projeto.id_projeto)
                );

                // Projeto sem avaliações.
                if (!resumo || Number(resumo.total_feedbacks) === 0) {

                    return {
                        titulo: projeto.titulo,
                        total: 0,
                        positivas: 0,
                        percentual: null
                    };
                }

                const total = Number(resumo.total_feedbacks);

                // Consulta a função de percentual já criada.
                const percentual = Number(
                    await BeassiAPI.get(
                        `/funcoes/projetos/${projeto.id_projeto}/percentual-positivas`
                    )
                );

                // Calcula a quantidade de avaliações positivas.
                const positivas = Math.round(
                    total * percentual / 100
                );

                return {
                    titulo: projeto.titulo,
                    total: total,
                    positivas: positivas,
                    percentual: percentual
                };

            })

        );

        // Monta as linhas da tabela.
        const linhas = resultados.map(item => {

            const percentualFormatado =
                item.percentual === null
                    ? 'Sem avaliações'
                    : Number(item.percentual).toLocaleString('pt-BR', {
                        minimumFractionDigits: 0,
                        maximumFractionDigits: 2
                    }) + '%';

            return `
                <tr>
                    <td>${escapar(item.titulo)}</td>
                    <td>${item.total}</td>
                    <td>${item.positivas}</td>
                    <td>${percentualFormatado}</td>
                </tr>
            `;

        }).join('');

        // Exibe a tabela.
        alvo.innerHTML = `
            <div class="relatorio-tabela-wrap">
                <table class="relatorio-tabela">

                    <thead>
                        <tr>
                            <th>Projeto</th>
                            <th>Avaliações feitas pelo cliente</th>
                            <th>Positivas</th>
                            <th>Percentual</th>
                        </tr>
                    </thead>

                    <tbody>
                        ${linhas}
                    </tbody>

                </table>
            </div>
        `;

        status.textContent = 'Dashboard de satisfação atualizado.';

    } catch (erro) {

        alvo.innerHTML = `
            <p class="relatorio-erro">
                ${escapar(erro.message || 'Não foi possível carregar a satisfação dos projetos.')}
            </p>
        `;

        status.textContent = 'Erro ao carregar o dashboard de satisfação.';

    } finally {

        botao.disabled = false;
        botao.innerHTML = textoOriginal;

    }
}



  // =====================================================
  // EVENTOS DOS BOTÕES
  // =====================================================

  document.querySelectorAll('[data-relatorio]').forEach(botao => {

    botao.addEventListener('click', () => {
      gerar(botao.dataset.relatorio);
    });

  });

  document.getElementById('gerarDashboardResumo')
    ?.addEventListener('click', gerarDashboardResumo);

  document.getElementById('gerarDashboardSatisfacao')
    ?.addEventListener('click', gerarDashboardSatisfacao);

})();
