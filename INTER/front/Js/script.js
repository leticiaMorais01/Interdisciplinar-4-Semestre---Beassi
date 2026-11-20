/* BEASSI Arquitetura - interações do Front-End.
   Funcionalidades que dependem do banco/API serão integradas ao Back-End posteriormente. */

function trocarImagem(elemento) {
  const imagemPrincipal = document.getElementById('imagemPrincipal');
  if (!imagemPrincipal || !elemento) return;

  imagemPrincipal.src = elemento.src;
  imagemPrincipal.alt = elemento.alt || 'Imagem do projeto';

  document.querySelectorAll('.miniaturas img').forEach(img => {
    img.classList.remove('ativa');
  });
  elemento.classList.add('ativa');
}

function adicionarFiltrosDeCores() {
  if (document.getElementById('beassi-filtros-svg')) return;

  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  svg.id = 'beassi-filtros-svg';
  svg.setAttribute('aria-hidden', 'true');
  svg.style.cssText = 'position:absolute;width:0;height:0;overflow:hidden';
  svg.innerHTML = `
    <filter id="beassi-protanopia">
      <feColorMatrix type="matrix" values="0.567 0.433 0 0 0  0.558 0.442 0 0 0  0 0.242 0.758 0 0  0 0 0 1 0" />
    </filter>
    <filter id="beassi-deuteranopia">
      <feColorMatrix type="matrix" values="0.625 0.375 0 0 0  0.7 0.3 0 0 0  0 0.3 0.7 0 0  0 0 0 1 0" />
    </filter>
    <filter id="beassi-tritanopia">
      <feColorMatrix type="matrix" values="0.95 0.05 0 0 0  0 0.433 0.567 0 0  0 0.475 0.525 0 0  0 0 0 1 0" />
    </filter>`;
  document.body.prepend(svg);
}

function aplicarEstilosAltoContraste() {
  if (document.getElementById('beassi-alto-contraste-estilos')) return;

  const estilo = document.createElement('style');
  estilo.id = 'beassi-alto-contraste-estilos';
  estilo.textContent = `
    body.alto-contraste {
      background: #000 !important;
      background-image: none !important;
      color: #fff !important;
    }

    /* Remove fundos claros e gradientes de todos os componentes,
       evitando texto amarelo sobre cartões brancos. */
    body.alto-contraste *:not(img):not(svg):not(path) {
      background: transparent !important;
      color: #fff !important;
      border-color: #ff0 !important;
      box-shadow: none !important;
    }

    body.alto-contraste .accessibility-panel {
      background: #000 !important;
      border: 2px solid #ff0 !important;
    }

    body.alto-contraste h1,
    body.alto-contraste h2,
    body.alto-contraste h3,
    body.alto-contraste h4,
    body.alto-contraste a,
    body.alto-contraste .destaque,
    body.alto-contraste .etiqueta-categoria,
    body.alto-contraste label {
      color: #ff0 !important;
    }

    body.alto-contraste p,
    body.alto-contraste span,
    body.alto-contraste li,
    body.alto-contraste small,
    body.alto-contraste strong,
    body.alto-contraste td,
    body.alto-contraste th {
      color: #fff !important;
    }

    body.alto-contraste input:not([type=\"checkbox\"]):not([type=\"radio\"]),
    body.alto-contraste textarea,
    body.alto-contraste select {
      background: #000 !important;
      color: #fff !important;
      border: 2px solid #ff0 !important;
    }

    body.alto-contraste input::placeholder,
    body.alto-contraste textarea::placeholder {
      color: #ddd !important;
      opacity: 1 !important;
    }

    body.alto-contraste button,
    body.alto-contraste .btn,
    body.alto-contraste [type=\"submit\"] {
      background: #ff0 !important;
      color: #000 !important;
      border: 2px solid #ff0 !important;
    }

    body.alto-contraste button:hover,
    body.alto-contraste a:focus-visible,
    body.alto-contraste button:focus-visible,
    body.alto-contraste input:focus-visible,
    body.alto-contraste select:focus-visible,
    body.alto-contraste textarea:focus-visible {
      outline: 3px solid #fff !important;
      outline-offset: 3px !important;
    }

    body.alto-contraste .accessibility-toggle {
      background: #ff0 !important;
      color: #000 !important;
      border: 2px solid #fff !important;
    }

    body.alto-contraste img {
      filter: none !important;
    }
  `;
  document.head.appendChild(estilo);
}

function configurarAcessibilidade() {
  if (document.querySelector('.accessibility-toggle')) return;

  const alvo = document.querySelector('main, #conteudo-principal, .container, .cadastro-box, .login-box');
  if (alvo && !alvo.id) alvo.id = 'conteudo-principal';

  const skip = document.createElement('a');
  skip.className = 'skip-link';
  skip.href = '#conteudo-principal';
  skip.textContent = 'Pular para o conteúdo';
  document.body.prepend(skip);

  const botao = document.createElement('button');
  botao.className = 'accessibility-toggle';
  botao.type = 'button';
  botao.setAttribute('aria-label', 'Abrir opções de acessibilidade');
  botao.setAttribute('aria-expanded', 'false');
  botao.innerHTML = '<i class="fa-solid fa-universal-access" aria-hidden="true"></i><span class="sr-only">Acessibilidade</span>';

  const painel = document.createElement('section');
  painel.className = 'accessibility-panel';
  painel.setAttribute('aria-label', 'Opções de acessibilidade');
  painel.setAttribute('aria-hidden', 'true');
  painel.innerHTML = `
    <h2>Acessibilidade</h2>
    <p>Escolha os ajustes que facilitam a navegação pelo site.</p>
    <button type="button" data-access="contraste" aria-pressed="false">Ativar alto contraste</button>
    <button type="button" data-access="fonte" aria-pressed="false">Aumentar tamanho do texto</button>
    <label for="beassiFiltroCores">Ajuste de visualização de cores</label>
    <select id="beassiFiltroCores">
      <option value="normal">Padrão</option>
      <option value="protanopia">Protanopia</option>
      <option value="deuteranopia">Deuteranopia</option>
      <option value="tritanopia">Tritanopia</option>
    </select>
    <button type="button" class="accessibility-close">Fechar opções</button>`;

  document.body.append(botao, painel);
  adicionarFiltrosDeCores();
  aplicarEstilosAltoContraste();

  const fecharPainel = () => {
    painel.classList.remove('aberto');
    painel.setAttribute('aria-hidden', 'true');
    botao.setAttribute('aria-expanded', 'false');
    botao.focus();
  };

  botao.addEventListener('click', () => {
    const aberto = !painel.classList.contains('aberto');
    painel.classList.toggle('aberto', aberto);
    painel.setAttribute('aria-hidden', String(!aberto));
    botao.setAttribute('aria-expanded', String(aberto));
    if (aberto) painel.querySelector('button')?.focus();
  });

  painel.querySelector('[data-access="contraste"]').addEventListener('click', event => {
    const ativo = document.body.classList.toggle('alto-contraste');
    event.currentTarget.setAttribute('aria-pressed', String(ativo));
    event.currentTarget.textContent = ativo ? 'Desativar alto contraste' : 'Ativar alto contraste';
  });

  painel.querySelector('[data-access="fonte"]').addEventListener('click', event => {
    const ativo = document.body.classList.toggle('fonte-aumentada');
    event.currentTarget.setAttribute('aria-pressed', String(ativo));
    event.currentTarget.textContent = ativo ? 'Restaurar tamanho do texto' : 'Aumentar tamanho do texto';
  });

  painel.querySelector('#beassiFiltroCores').addEventListener('change', event => {
    document.body.classList.remove('filtro-protanopia', 'filtro-deuteranopia', 'filtro-tritanopia');
    if (event.target.value !== 'normal') {
      document.body.classList.add(`filtro-${event.target.value}`);
    }
  });

  painel.querySelector('.accessibility-close').addEventListener('click', fecharPainel);

  document.addEventListener('keydown', event => {
    if (event.key === 'Escape' && painel.classList.contains('aberto')) fecharPainel();
  });

  document.addEventListener('click', event => {
    if (painel.classList.contains('aberto') &&
        !painel.contains(event.target) && !botao.contains(event.target)) {
      painel.classList.remove('aberto');
      painel.setAttribute('aria-hidden', 'true');
      botao.setAttribute('aria-expanded', 'false');
    }
  });
}


function configurarFiltrosProjetos() {

    const busca = document.getElementById('buscaProjetos');
    const filtroTipo = document.getElementById('filtroTipoProjeto');
    const filtroCategorias = document.getElementById('filtroCategorias');

    const cards = [...document.querySelectorAll('[data-projeto-card]')];

    if (!busca || !filtroTipo || !filtroCategorias || !cards.length) return;

    const vazia = document.getElementById('nenhumProjeto');

    // Gera a lista de categorias com base nos projetos
    const categorias = new Map();

    cards.forEach(card => {

        const categoriasProjeto = (card.dataset.categorias || '')
            .split(' ')
            .filter(Boolean);

        categoriasProjeto.forEach(categoria => {

            if (!categorias.has(categoria)) {
                categorias.set(categoria, categoria);
            }

        });

    });

    // Exibe as categorias disponíveis
    filtroCategorias.innerHTML = '';

    if (categorias.size === 0) {

        filtroCategorias.innerHTML = `
            <p class="categorias-vazio">
                Nenhuma categoria disponível.
            </p>
        `;

    } else {

        categorias.forEach(categoria => {

            const label = document.createElement('label');

            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.value = categoria;

            const texto = document.createElement('span');
            texto.textContent = categoria
                .replace(/-/g, ' ')
                .replace(/\b\w/g, letra => letra.toUpperCase());

            label.append(checkbox, texto);
            filtroCategorias.appendChild(label);

        });

    }

    // Aplica todos os filtros em conjunto
    const filtrar = () => {

        const termo = busca.value.trim().toLocaleLowerCase('pt-BR');
        const tipo = filtroTipo.value;

        const selecionadas = [
            ...filtroCategorias.querySelectorAll('input:checked')
        ].map(input => input.value);

        let visiveis = 0;

        cards.forEach(card => {

            const texto = card.textContent.toLocaleLowerCase('pt-BR');
            const tipoProjeto = card.dataset.tipo;

            const categoriasProjeto = (card.dataset.categorias || '')
                .split(' ')
                .filter(Boolean);

            const correspondeBusca = texto.includes(termo);

            const correspondeTipo =
                tipo === 'todos' || tipoProjeto === tipo;

            // Se houver mais de uma categoria selecionada,
            // o projeto precisa possuir todas elas.
            const correspondeCategorias = selecionadas.every(categoria =>
                categoriasProjeto.includes(categoria)
            );

            const mostrar =
                correspondeBusca &&
                correspondeTipo &&
                correspondeCategorias;

            card.hidden = !mostrar;

            if (mostrar) {
                visiveis++;
            }

        });

        if (vazia) {
            vazia.hidden = visiveis !== 0;
        }

    };

    busca.addEventListener('input', filtrar);
    filtroTipo.addEventListener('change', filtrar);
    filtroCategorias.addEventListener('change', filtrar);

}


function configurarFormularios() {
  
 
document.querySelectorAll('form[data-contato-form]').forEach(form => {
    form.addEventListener('submit', event => {
        event.preventDefault();

        if (!form.reportValidity()) return;

        const dados = new FormData(form);

        const nome = String(dados.get('nome') || '').trim();
        const email = String(dados.get('email') || '').trim();
        const telefone = String(dados.get('telefone') || '').trim();
        const mensagem = String(dados.get('mensagem') || '').trim();

        const destinatario = 'beassiarquitetura@gmail.com';
        const assunto = `Contato pelo site - ${nome}`;

        const corpo = `
        Olá! Estou entrando em contato através do site da BEASSI Arquitetura.

        Nome: ${nome}
        E-mail: ${email}
        Telefone: ${telefone || 'Não informado'}

        Mensagem:
        ${mensagem}
                `.trim();

                const urlGmail =
                    `https://mail.google.com/mail/?view=cm&fs=1` +
                    `&to=${encodeURIComponent(destinatario)}` +
                    `&su=${encodeURIComponent(assunto)}` +
                    `&body=${encodeURIComponent(corpo)}`;

                window.open(urlGmail, '_blank');

                const feedback = form.querySelector('.form-feedback');

                beassiMensagem(
                    feedback,
                    'Seu e-mail foi preparado! Confira os dados no Gmail e clique em Enviar.'
        );
    });
});



document.querySelectorAll('input[type="file"][accept*="image"]').forEach(input => {

    input.addEventListener('change', () => {

        const novosArquivos = Array.from(input.files || []);

        if (!novosArquivos.length) return;

        const arquivosAnteriores = input._arquivosSelecionados || [];

        const todosArquivos = [...arquivosAnteriores, ...novosArquivos];

        // Evita arquivos duplicados
        input._arquivosSelecionados = todosArquivos.filter(
            (arquivo, indice, lista) =>
                lista.findIndex(a =>
                    a.name === arquivo.name &&
                    a.size === arquivo.size &&
                    a.lastModified === arquivo.lastModified
                ) === indice
        );

        atualizarPreviewImagens(input);
    });

});

function atualizarPreviewImagens(input) {

    const transferencia = new DataTransfer();

    input._arquivosSelecionados.forEach(arquivo => {
        transferencia.items.add(arquivo);
    });

    input.files = transferencia.files;

    let area = input.nextElementSibling;

    if (!area || !area.classList.contains('preview-nova-imagem')) {
        area = document.createElement('div');
        area.className = 'preview-nova-imagem';
        input.insertAdjacentElement('afterend', area);
    }

    if (input._arquivosSelecionados.length === 0) {
        area.remove();
        return;
    }

    area.innerHTML = `
        <p>Imagens selecionadas: ${input._arquivosSelecionados.length}</p>
        <div class="lista-preview-imagens"></div>
        <button type="button" class="remover-nova-imagem">
            <i class="fa-solid fa-trash-can"></i>
            Remover todas as imagens
        </button>
    `;

    const lista = area.querySelector('.lista-preview-imagens');

    input._arquivosSelecionados.forEach((arquivo, indice) => {

        const item = document.createElement('div');
        item.className = 'item-preview-imagem';

        const img = document.createElement('img');
        img.src = URL.createObjectURL(arquivo);
        img.alt = arquivo.name;

        img.style.width = '130px';
        img.style.height = '110px';
        img.style.objectFit = 'cover';
        img.style.borderRadius = '10px';
        img.style.margin = '5px';

        const botaoRemover = document.createElement('button');
        botaoRemover.type = 'button';
        botaoRemover.textContent = 'Remover';
        botaoRemover.className = 'remover-imagem-individual';

        botaoRemover.addEventListener('click', () => {

            input._arquivosSelecionados.splice(indice, 1);

            atualizarPreviewImagens(input);

        });

        item.append(img, botaoRemover);
        lista.appendChild(item);

    });

    // Remover todas as imagens
    area.querySelector('.remover-nova-imagem').addEventListener('click', () => {

        input._arquivosSelecionados = [];
        input.value = '';

        atualizarPreviewImagens(input);

    });

}

  

  
  document.querySelectorAll('form[data-validar-senha]').forEach(form => {
    form.addEventListener('submit', event => {
      const senha = form.querySelector('[name="senha"]');
      const confirmar = form.querySelector('[name="confirmarSenha"]');
      if (senha && confirmar && senha.value !== confirmar.value) {
        event.preventDefault();
        confirmar.setCustomValidity('As senhas não coincidem.');
        confirmar.reportValidity();
      } else if (confirmar) {
        confirmar.setCustomValidity('');
      }
    });
    const confirmar = form.querySelector('[name="confirmarSenha"]');
    if (confirmar) confirmar.addEventListener('input', () => confirmar.setCustomValidity(''));
  });
}


// MOSTRAR E OCULTAR SENHA
function configurarVisualizacaoSenha() {
    document.querySelectorAll('[data-toggle-senha]').forEach(botao => {

        botao.addEventListener('click', () => {
            const container = botao.closest('.campo-senha');
            const campo = container.querySelector('input');
            const icone = botao.querySelector('i');

            const mostrar = campo.type === 'password';

            campo.type = mostrar ? 'text' : 'password';

            botao.setAttribute('aria-pressed', String(mostrar));

            botao.setAttribute(
                'aria-label',
                mostrar ? 'Ocultar senha' : 'Mostrar senha'
            );

            icone.classList.toggle('fa-eye', !mostrar);
            icone.classList.toggle('fa-eye-slash', mostrar);
        });

    });
}

// VALIDAR QUANTIDADE DE DÍGITOS DO TELEFONE
function configurarValidacaoTelefone() {
    document.querySelectorAll('input[name="telefone"]').forEach(telefone => {

        telefone.addEventListener('input', () => {
            const numeros = telefone.value.replace(/\D/g, '');

            telefone.setCustomValidity('');

            if (numeros.length !== 10 && numeros.length !== 11) {
                telefone.setCustomValidity(
                    'Informe um telefone válido com DDD (10 ou 11 dígitos).'
                );
            }
        });
    });
}




document.addEventListener('DOMContentLoaded', () => {
  configurarAcessibilidade();
  configurarFiltrosProjetos();
  configurarFormularios();
  configurarVisualizacaoSenha();
  configurarValidacaoTelefone();
});

