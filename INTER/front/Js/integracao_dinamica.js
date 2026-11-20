/* BEASSI — integração dinâmica do Front-End.
   Os caminhos abaixo formam o contrato REST esperado do Spring Boot; alinhar nomes/DTOs
   com o Back-End antes de executar a integração real. Nenhum dado de exemplo é usado como fallback. */
(() => {
  const $ = (selector, scope = document) => scope.querySelector(selector);
  const $$ = (selector, scope = document) => [...scope.querySelectorAll(selector)];
  const esc = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  const show = (form, message, error = false) => {
    let target = form.querySelector('[data-api-message]');
    if (!target) { target = document.createElement('p'); target.dataset.apiMessage = ''; target.setAttribute('role','status'); form.append(target); }
    beassiMensagem(target, message, error);
  };
  const withSubmit = (form, handler) => {
    if (!form || form.dataset.apiBound) return;
    form.dataset.apiBound = 'true';
    form.addEventListener('submit', async event => {
      event.preventDefault();
      if (!form.reportValidity()) return;
      const button = $('button[type="submit"]', form); if (button) button.disabled = true;
      try { await handler(form); } catch (error) { show(form, error.message || 'Ocorreu um erro. Tente novamente.', true); }
      finally { if (button) button.disabled = false; }
    });
  };
  const formObject = form => Object.fromEntries(new FormData(form).entries());

  async function carregarClientes(select) {
    const selected = select.dataset.selected || select.value;
    select.innerHTML = '<option value="">Selecione um cliente</option>'; 
    const clientes = beassiArray(await BeassiAPI.get('/Cliente'));
    clientes.sort((a, b) =>
    String(a.nome ?? '').localeCompare(
        String(b.nome ?? ''),
        'pt-BR'
        )
    );
    clientes.forEach(cliente => {
      const id = cliente.id_usuario ?? cliente.idUsuario ?? cliente.id ?? cliente.usuarioId;
      const nome = cliente.nome;
      if (id == null || !nome) return;
      const option = new Option(nome, String(id));
      if (String(id) === String(selected)) option.selected = true;
      select.add(option);
    });
    if (!clientes.length) select.options[0].textContent = 'Nenhum cliente ativo cadastrado';
  }
  async function carregarCategorias(container) {
    const estaEditando = !!container.closest('#formEditarProjeto');

    const endpoint = estaEditando
        ? '/categoria'
        : '/categoria/ativas';

    const categorias = beassiArray(await BeassiAPI.get(endpoint));
    categorias.sort((a, b) =>
        String(a.categoria ?? a.nome ?? '').localeCompare(
            String(b.categoria ?? b.nome ?? ''),
            'pt-BR'
        )
    );
    const marcadas = new Set($$('input[name="categorias[]"]:checked', container).map(i => i.value));
    container.replaceChildren();
    categorias.forEach(cat => {
      const id = cat.id_categoria ?? cat.idCategoria ?? cat.id;
      const nome = cat.categoria ?? cat.nome;
      if (id == null || !nome) return;
      const label = document.createElement('label'); label.className = 'categoria-item';
      const input = document.createElement('input'); input.type='checkbox'; input.name='categorias[]'; input.value=String(id); input.dataset.nome=nome; input.checked=marcadas.has(String(id)); input.dataset.ativo = String(cat.ativo === true);
      const span = document.createElement('span'); span.textContent=nome;
      label.append(input,span); container.append(label);
    });
  }
  async function carregarProjetoEdicao() {
    const form = $('#formEditarProjeto'); if (!form) return;
    const id = beassiIdDaUrl('id'); if (!id) { show(form,'Identificador do projeto não informado na URL.',true); return; }
    const projeto = await BeassiAPI.get(`/projeto/${encodeURIComponent(id)}`);
    const imagensProjeto = beassiArray(await BeassiAPI.get('/imagem')).filter(i => Number(i.projeto?.id_projeto) === Number(id));
    const associacoesProjeto = beassiArray(await BeassiAPI.get('/projetoCategoria')).filter(a => Number(a.projeto?.id_projeto ?? a.id?.projetoId) === Number(id));
    form.elements.titulo.value = projeto.titulo ?? '';
    form.elements.descricao.value = projeto.descricao ?? '';
    if (form.elements.tipoProjeto) { form.elements.tipoProjeto.value = (projeto.tipoResidencial ? 'RESIDENCIAL' : 'COMERCIAL'); const sub=projeto.tipoResidencial?.id_tipo_residencial ?? projeto.tipoComercial?.id_tipo_comercial ?? ''; const sel=$('#subtipoProjeto'); if(sel) sel.dataset.selected=String(sub); }
    const cliente = $('#clienteProjeto'); if (cliente) { cliente.dataset.selected = String(projeto.cliente?.id_usuario ?? ''); cliente.value = cliente.dataset.selected; }
    if (form.elements.etapaProjeto) { const mapa={'Não iniciado':'nao-iniciado','Não Iniciado':'nao-iniciado','Medição':'medicao','Desenvolvimento do projeto':'desenvolvimento','Apresentação ao cliente':'apresentacao','Revisões':'revisao','Projeto Executivo':'executivo','Concluído':'concluido'}; form.elements.etapaProjeto.value = mapa[projeto.etapa_atual] ?? 'nao-iniciado'; }
    form.dataset.statusPublicacao = String(projeto.status_publicacao === true);
    if(form.elements.statusPublicacao) form.elements.statusPublicacao.checked = projeto.status_publicacao === true;
    const preview = $('.preview-imagem');

if (preview) {
    preview.replaceChildren();

    imagensProjeto.forEach(imagem => {
        const caminho = imagem.caminho;

        if (!caminho) return;

        const url = caminho.startsWith('/')
            ? `${window.BEASSI_API_BASE_URL || 'http://localhost:8080'}${caminho}`
            : caminho;

        const item = document.createElement('div');
        item.className = 'imagem-atual-item';

        const img = document.createElement('img');
        img.src = url;
        img.alt = 'Imagem do projeto';

        const botoes = document.createElement('div');
        botoes.className = 'imagem-atual-acoes';

        const trocar = document.createElement('label');
        trocar.className = 'btn-trocar-imagem';
        trocar.innerHTML = '<i class="fa-solid fa-camera"></i> Trocar imagem';

        const arquivo = document.createElement('input');
        arquivo.type = 'file';
        arquivo.accept = 'image/*';
        arquivo.hidden = true;

        arquivo.addEventListener('change', async () => {
            const novoArquivo = arquivo.files[0];

            if (!novoArquivo) return;

            const upload = new FormData();
            upload.append('arquivo', novoArquivo);
            upload.append('projetoId', String(id));

            try {
                const atualizada = await BeassiAPI.put(
                    `/imagem/${imagem.id_imagem}`,
                    upload
                );

                imagem.caminho = atualizada.caminho;

                img.src = imagem.caminho.startsWith('/')
                    ? `${window.BEASSI_API_BASE_URL || 'http://localhost:8080'}${imagem.caminho}`
                    : imagem.caminho;

                alert('Imagem substituída com sucesso!');
            } catch (erro) {
                alert('Não foi possível substituir a imagem.');
            }
        });

        trocar.appendChild(arquivo);

        const remover = document.createElement('button');
        remover.type = 'button';
        remover.className = 'btn-remover-imagem';
        remover.innerHTML = '<i class="fa-solid fa-trash-can"></i> Remover';

        remover.addEventListener('click', async () => {
            if (!confirm('Deseja realmente remover esta imagem do projeto?')) {
                return;
            }

            try {
                await BeassiAPI.delete(`/imagem/${imagem.id_imagem}`);

                item.remove();

                alert('Imagem removida com sucesso!');
            } catch (erro) {
                alert('Não foi possível remover a imagem.');
            }
        });

        botoes.append(trocar, remover);
        item.append(img, botoes);
        preview.appendChild(item);
    });
}
    const categorias = new Set(associacoesProjeto.map(a => String(a.categoria?.id_categoria ?? a.id?.categoriaId ?? '')));
    form.dataset.categoriasSelecionadas = JSON.stringify([...categorias]);
  }
  async function bindProjectForm(form, editing = false, usuarioSessao = null) {
    if (!form) return;
    withSubmit(form, async current => {
      const data = new FormData(current), id = beassiIdDaUrl('id');
      // Valida se foi selecionada pelo menos uma imagem
      const campoImagemValidacao = current.querySelector('#imagem');

      const imagensSelecionadas = campoImagemValidacao?._arquivosSelecionados?.length
          ? campoImagemValidacao._arquivosSelecionados
          : Array.from(data.getAll('imagem')).filter(
              imagem => imagem instanceof File && imagem.size > 0
          );

      if (editing) {
          const imagensExistentes = current.querySelectorAll(
              '.preview-imagem .imagem-atual-item'
          ).length;

          const totalImagens = imagensExistentes + imagensSelecionadas.length;

          if (totalImagens === 0) {
              throw new Error(
                  'O projeto precisa ter pelo menos uma imagem para ser salvo.'
              );
          }
      } else if (imagensSelecionadas.length === 0) {
          throw new Error(
              'Selecione pelo menos uma imagem para cadastrar o projeto.'
          );
      }

      // Valida se pelo menos uma categoria foi selecionada
      const categoriasSelecionadas = data.getAll('categorias[]')
          .map(Number)
          .filter(Boolean);

      if (categoriasSelecionadas.length === 0) {
          throw new Error('Selecione pelo menos uma categoria para o projeto.');
      }
      const etapas = {'nao-iniciado':['nao-iniciado',0],medicao:['medicao',17],desenvolvimento:['desenvolvimento',33],apresentacao:['apresentacao',50],revisao:['revisao',67],executivo:['executivo',83],concluido:['concluido',100]};
      const etapa = etapas[data.get('etapaProjeto')] || etapas['nao-iniciado'];
      const tipo = String(data.get('tipoProjeto') || '').toUpperCase(), subtipo = Number(data.get('subtipoProjeto')), clienteId = Number(data.get('clienteId'));
      if (!subtipo) throw new Error('Selecione o tipo específico do projeto.');
      if (!usuarioSessao?.idUsuario) throw new Error('Não foi possível identificar o administrador da sessão. Entre novamente.');
      const payload = {titulo:data.get('titulo'),descricao:data.get('descricao'),etapa_atual:etapa[0],porcentagem_conclusao:etapa[1],status_publicacao:!!data.get('statusPublicacao'),cliente: clienteId ? {id_usuario:clienteId} : null, administrador:{id_usuario:Number(usuarioSessao.idUsuario)},tipoResidencial:tipo==='RESIDENCIAL'?{id_tipo_residencial:subtipo}:null,tipoComercial:tipo==='COMERCIAL'?{id_tipo_comercial:subtipo}:null};
      const result = editing ? await BeassiAPI.put(`/projeto/${encodeURIComponent(id)}`,payload) : await BeassiAPI.post('/projeto',payload);
      const projetoId = result?.id_projeto ?? result?.idProjeto ?? id;
      const selecionadas = data.getAll('categorias[]').map(Number).filter(Boolean);
      if (projetoId) {
        const existentes = editing ? beassiArray(await BeassiAPI.get('/projetoCategoria')).filter(a=>Number(a.projeto?.id_projeto??a.id?.projetoId)===Number(projetoId)) : [];
        for (const a of existentes) { const cid=Number(a.categoria?.id_categoria??a.id?.categoriaId); if(!selecionadas.includes(cid)) await BeassiAPI.delete(`/projetoCategoria/${projetoId}/${cid}`); }
        const atuais=new Set(existentes.map(a=>Number(a.categoria?.id_categoria??a.id?.categoriaId)));
        for (const cid of selecionadas) if(!atuais.has(cid)) await BeassiAPI.post('/projetoCategoria',{projeto:{id_projeto:Number(projetoId)},categoria:{id_categoria:cid}});
      }
    
      const campoImagem = current.querySelector('#imagem');

      const imagens = campoImagem?._arquivosSelecionados?.length
          ? campoImagem._arquivosSelecionados
          : Array.from(data.getAll('imagem')).filter(
              imagem => imagem instanceof File && imagem.size > 0
          );

      if (projetoId && imagens.length > 0) {

          for (const imagem of imagens) {

              const upload = new FormData();
              upload.append('arquivo', imagem);
              upload.append('projetoId', String(projetoId));

              await BeassiAPI.post('/imagem', upload);
          }
      }

      if (!editing && projetoId) {

          // Cria a área de mensagem antes de utilizá-la
          show(current, 'Projeto cadastrado com sucesso!');

          const mensagem = current.querySelector('[data-api-message]');

          mensagem.innerHTML = `
              <div class="sucesso-cadastro-projeto">
                  <div class="sucesso-icone">✓</div>

                  <h3>Projeto cadastrado com sucesso!</h3>

                  <p>Seu projeto foi salvo. O que deseja fazer agora?</p>

                  <div class="sucesso-acoes">
                      <a href="gerenciar_projetos.html" class="btn-listagem">
                          <span>Ver listagem de projetos</span>
                      </a>

                      <a href="editar_projeto.html?id=${encodeURIComponent(projetoId)}" class="btn-editar-projeto">
                          <span>Visualizar / editar projeto</span>
                      </a>
                  </div>
              </div>
          `;

      } else {
          show(current, 'Projeto atualizado com sucesso.');

          setTimeout(() => {
              window.location.href = 'gerenciar_projetos.html';
          }, 1500);
      }


    });
  }

  async function init() {


    // Verifica a autenticação e o perfil antes de carregar páginas restritas.
    const paginaAtual = location.pathname.split('/').pop().toLowerCase();

    const paginasCliente = [
        'meus_projetos.html',
        'editar_perfil.html',
        'avaliar_projeto.html'
    ];

    const paginasAdmin = [
        'painel_admin.html',
        'editar_perfil_admin.html',
        'cadastrar_projeto.html',
        'editar_projeto.html',
        'gerenciar_clientes.html',
        'gerenciar_feedbacks.html',
        'gerenciar_projetos.html',
        'detalhes_cliente.html',
        'editar_cliente.html',
        'relatorios.html',
        'gerenciar_tipos_projeto.html',
        'gerenciar_categorias.html'
    ];

    const paginaRestrita =
        paginasCliente.includes(paginaAtual) ||
        paginasAdmin.includes(paginaAtual);

    let usuarioSessao = null;
    if (paginaRestrita) {
        try {
            const usuario = await BeassiAPI.get('/Login/sessao');
            usuarioSessao = usuario;

            if (
                paginasCliente.includes(paginaAtual) &&
                usuario.tipoUsuario !== 'CLIENTE'
            ) {
                location.replace(
                    usuario.tipoUsuario === 'ADMINISTRADOR'
                        ? 'painel_admin.html'
                        : 'login.html'
                );
                return;
            }

            if (
                paginasAdmin.includes(paginaAtual) &&
                usuario.tipoUsuario !== 'ADMINISTRADOR'
            ) {
                location.replace(
                    usuario.tipoUsuario === 'CLIENTE'
                        ? 'meus_projetos.html'
                        : 'login.html'
                );
                return;
            }

        } catch (error) {
            location.replace('login.html');
            return;
        }
    }


    // ATUALIZA O MENU CONFORME O USUÁRIO LOGADO
    const linkAreaUsuario = $('#link-area-usuario');
    const linkLogin = $('#link-login');
    const linkSair = $('#link-sair');
    const secaoMeusProjetos = $('#meusprojetos');

    if (linkAreaUsuario || linkLogin || linkSair) {
        let usuarioMenu = usuarioSessao;

        // No index, consulta a sessão mesmo sendo uma página pública.
        if (!usuarioMenu) {
            try {
                usuarioMenu = await BeassiAPI.get('/Login/sessao');
            } catch {
                usuarioMenu = null;
            }
        }

        if (usuarioMenu?.tipoUsuario === 'CLIENTE') {
            linkAreaUsuario.href = 'meus_projetos.html';
            linkAreaUsuario.textContent = 'ÁREA DO CLIENTE';
            linkAreaUsuario.hidden = false;

            linkLogin.hidden = true;
            linkSair.hidden = false;

            if (secaoMeusProjetos) {
                secaoMeusProjetos.hidden = true;
            }

        } else if (usuarioMenu?.tipoUsuario === 'ADMINISTRADOR') {
            linkAreaUsuario.href = 'painel_admin.html';
            linkAreaUsuario.textContent = 'ÁREA ADMINISTRATIVA';
            linkAreaUsuario.hidden = false;

            linkLogin.hidden = true;
            linkSair.hidden = false;

            if (secaoMeusProjetos) {
                secaoMeusProjetos.hidden = true;
            }

        } else {
            linkAreaUsuario.hidden = true;
            linkLogin.hidden = false;
            linkSair.hidden = true;

            if (secaoMeusProjetos) {
                secaoMeusProjetos.hidden = false;
            }
        }


    }


    // Combos sempre consultam o banco/API: sem nomes/IDs fixos no HTML.
    for (const select of $$('[data-carregar-clientes]')) {
      try { await carregarClientes(select); } catch (e) { select.innerHTML='<option value="">Não foi possível carregar clientes</option>'; }
    }
    const tipoSelect = $('#tipoProjeto');
    const subtipoSelect = $('#subtipoProjeto');


    async function atualizarSubtipos(tipo, selecionado = '') {
          if (!subtipoSelect) return;

          subtipoSelect.innerHTML = '<option value="">Carregando classificações...</option>';

          if (!tipo) {
              subtipoSelect.innerHTML = '<option value="">Selecione primeiro o tipo de projeto</option>';
              return;
          }

          const baseEndpoint = tipo.toUpperCase() === 'RESIDENCIAL'
              ? '/tipoResidencial'
              : '/tipoComercial';

          // Busca somente os tipos ativos
          let tipos = beassiArray(
              await BeassiAPI.get(`${baseEndpoint}/ativos`)
          );

          // Verifica se o tipo já associado ao projeto está inativo
          if (
              selecionado &&
              !tipos.some(t =>
                  String(t.id_tipo_residencial ?? t.id_tipo_comercial ?? t.id) === String(selecionado)
              )
          ) {
              // Busca todos apenas para recuperar o tipo já associado
              const todos = beassiArray(
                  await BeassiAPI.get(baseEndpoint)
              );

              const tipoAnterior = todos.find(t =>
                  String(t.id_tipo_residencial ?? t.id_tipo_comercial ?? t.id) === String(selecionado)
                  && t.ativo === false
              );

              if (tipoAnterior) {
                  tipos.push(tipoAnterior);
              }
          }

          tipos.sort((a, b) =>
              String(a.tipo ?? a.nome ?? '').localeCompare(
                  String(b.tipo ?? b.nome ?? ''),
                  'pt-BR'
              )
          );

          subtipoSelect.innerHTML = '<option value="">Selecione a classificação</option>';


          tipos.forEach(t => {
              const id = t.id_tipo_residencial ?? t.id_tipo_comercial ?? t.id;
              const nome = t.tipo ?? t.nome;

              if (id == null || !nome) return;

              const option = new Option(nome, String(id));

              if (String(id) === String(selecionado)) {
                  option.selected = true;
              }

              subtipoSelect.add(option);
          });
      }


    if (tipoSelect && subtipoSelect) {
      tipoSelect.addEventListener('change',()=>atualizarSubtipos(tipoSelect.value).catch(e=>{subtipoSelect.innerHTML='<option value="">Falha ao carregar classificações</option>';}));
      if (tipoSelect.value) { try { await atualizarSubtipos(tipoSelect.value, subtipoSelect.dataset.selected || ''); } catch {} }
    }
    for (const container of $$('[data-carregar-categorias]')) {
      try { await carregarCategorias(container); } catch (e) { container.textContent='Não foi possível carregar categorias.'; }
    }

    for (const select of $$('[data-carregar-projetos-cliente]')) {
        try {
            const projetos = beassiArray(
                await BeassiAPI.get(
                    `/funcoes/clientes/${usuarioSessao.idUsuario}/projetos`
                )
            );

            select.innerHTML = `
                <option value="">
                    Selecione um projeto vinculado à sua conta
                </option>
            `;

            projetos.forEach(projeto => {
                const id = projeto.id_projeto ?? projeto.idProjeto ?? projeto.id;

                if (id == null) return;

                select.add(
                    new Option(projeto.titulo, String(id))
                );
            });

            if (projetos.length === 0) {
                select.innerHTML = `
                    <option value="">
                        Você não possui projetos cadastrados para enviar feedback.
                    </option>
                `;
            }

        } catch (erro) {
            console.error('Erro ao carregar projetos para feedback:', erro);

            select.innerHTML = `
                <option value="">
                    Não foi possível carregar seus projetos. Tente novamente.
                </option>
            `;
        }
    }

    const formCadastro = $('#formCadastroProjeto'); if (formCadastro) await bindProjectForm(formCadastro,false,usuarioSessao);
    const formEdicao = $('#formEditarProjeto');
    if (formEdicao) {
      try {
        await carregarProjetoEdicao();
        if (tipoSelect && subtipoSelect) {
            await atualizarSubtipos(
                tipoSelect.value,
                subtipoSelect.dataset.selected || ''
            );
        }
        const idsCategorias = new Set(
            JSON.parse(formEdicao.dataset.categoriasSelecionadas || '[]')
        );

        $$('#categoriasContainer input[name="categorias[]"]').forEach(input => {

            input.checked = idsCategorias.has(String(input.value));

            // Categoria inativa só pode permanecer selecionada
            // se já estava associada anteriormente ao projeto.
            if (input.dataset.ativo === 'false' && !input.checked) {
                input.disabled = true;
            }

        });
      } catch (e) { show(formEdicao, e.message, true); }
      await bindProjectForm(formEdicao,true,usuarioSessao);
    }

    withSubmit($('form[data-cadastro-cliente]'), async form => {
  const dados = formObject(form);

  if (dados.senha !== dados.confirmarSenha) {
    throw new Error('As senhas não coincidem.');
  }

  const cliente = {
    email: dados.email,
    senha: dados.senha,
    nome: dados.nome,
    cpf: dados.cpf,
    telefone: dados.telefone
  };

  await BeassiAPI.post('/Cliente', cliente);

  // O cadastro foi confirmado pelo servidor; segue para o login com aviso de sucesso.
  location.href = 'login.html?cadastro=sucesso';
});
    const login = $('form[data-login]');

withSubmit(login, async form => {

    const resposta = await BeassiAPI.post('/Login', formObject(form));

    if (resposta.tipoUsuario === 'CLIENTE') {
        location.href = 'meus_projetos.html';
        return;
    }

    if (resposta.tipoUsuario === 'ADMINISTRADOR') {
        location.href = 'painel_admin.html';
        return;
    }

    throw new Error('Tipo de usuário não reconhecido.');

});
    withSubmit($('#formSolicitarRecuperacao'), async form => {
  await BeassiAPI.post('/recuperacao-senha/solicitar', {
    email: form.elements.email.value
  });

  show(form, 'Se o endereço estiver cadastrado, você receberá as instruções para redefinir a senha.');
  form.reset();
});
    
withSubmit($('#formNovaSenha'), async form => {
    const senha = form.elements.senha.value;
    const confirmar = form.elements.confirmarSenha.value;

    if (senha !== confirmar) {
        throw new Error('As senhas não coincidem.');
    }

    const token = new URLSearchParams(location.search).get('token');

    if (!token) {
        throw new Error('Link de recuperação inválido ou incompleto.');
    }

    await BeassiAPI.post('/recuperacao-senha/redefinir', {
        token,
        novaSenha: senha
    });

    show(form, 'Senha redefinida com sucesso! Redirecionando para o login...');

    setTimeout(() => {
        window.location.href = 'login.html';
    }, 2500);
});


    withSubmit($('#formEditarCliente'), async form => { await BeassiAPI.put(`/Cliente/${usuarioSessao.idUsuario}`, formObject(form)); show(form,'Perfil atualizado com sucesso.'); });
    withSubmit($('#formEditarAdministrador'), async form => { await BeassiAPI.put(`/Administrador/${usuarioSessao.idUsuario}`, {email:form.elements.email.value,registro_cau:form.elements.registroCau.value}); show(form,'Perfil do administrador atualizado.'); });
    withSubmit($('#formFeedbackCliente'), async form => {
      const d=formObject(form); 
      d.exibir_nome = d.exibirNome === 'true' || d.exibir_nome === 'true'; 
      d.nota=Number(d.nota);
      
      d.comentario = d.mensagem;
      delete d.mensagem;

      const sessao=await BeassiAPI.get('/Login/sessao');

      d.cliente={id_usuario:Number(sessao.idUsuario)}; 
      d.projeto={id_projeto:Number(d.projetoId)}; 
      
      delete d.projetoId; 
      delete d.exibirNome;

      await BeassiAPI.post('/Feedback', d); 
      
      show(form,'Avaliação enviada para análise.'); 
      form.reset();
    });

    // Dados pessoais e áreas autenticadas carregam apenas após sessão válida.
    if ($('#formEditarCliente')) { try { const u=await BeassiAPI.get(`/Cliente/${usuarioSessao.idUsuario}`); Object.entries({nome:u.nome,email:u.email,telefone:u.telefone,cpf:u.cpf}).forEach(([k,v])=>{const el=$(`#formEditarCliente [name="${k}"]`); if(el) el.value=v??'';}); } catch {} }
    if ($('#formEditarAdministrador')) { try { const u=await BeassiAPI.get(`/Administrador/${usuarioSessao.idUsuario}`); const f=$('#formEditarAdministrador'); if(f.elements.email)f.elements.email.value=u.email??''; if(f.elements.registroCau)f.elements.registroCau.value=u.registro_cau??u.registroCau??''; } catch {} }

    
    const tabela = $('#tabelaClientes');

    if (tabela) {
        try {
            const clientes = beassiArray(await BeassiAPI.get('/Cliente'));

            const clientesComProjetos = await Promise.all(clientes.map(async c => {
                const id = c.id_usuario ?? c.idUsuario ?? c.id;

                try {
                    const projetos = beassiArray(
                        await BeassiAPI.get(`/funcoes/clientes/${encodeURIComponent(id)}/projetos`)
                    );

                    return { ...c, quantidadeProjetos: projetos.length };
                } catch {
                    return { ...c, quantidadeProjetos: 0 };
                }
            }));

            tabela.innerHTML = clientesComProjetos.map(c => {
                const id = c.id_usuario ?? c.idUsuario ?? c.id;
                const qtd = Number(c.quantidadeProjetos || 0);
                const ativo = c.status === true;

                return `
                    <tr data-projetos="${qtd}">
                        <td>
                            <strong>${esc(c.nome)}</strong>
                            <small>Cliente #${esc(id)}</small>
                        </td>

                        <td>
                            ${esc(c.email)}
                            <small>${esc(c.telefone)}</small>
                        </td>

                        <td>${qtd} projeto(s)</td>

                        <td>
                            <span data-cliente-status class="${ativo ? 'conta-ativa' : 'conta-inativa'}">
                                ${ativo ? 'Conta ativa' : 'Conta inativa'}
                            </span>
                        </td>

                        <td>
                            <span class="cliente-situacao ${qtd ? 'com-projeto' : 'sem-projeto'}">
                                ${qtd ? 'Com projetos' : 'Sem projetos'}
                            </span>
                        </td>

                        <td class="cliente-acoes">
                            <a href="detalhes_cliente.html?id=${encodeURIComponent(id)}">Ver detalhes</a>
                            <a href="editar_cliente.html?id=${encodeURIComponent(id)}">Editar</a>

                            ${!ativo ? `
                                <button
                                    type="button"
                                    class="btn-reativar-cliente"
                                    data-reativar-cliente="${esc(id)}"
                                >
                                    Reativar
                                </button>
                            ` : ''}
                        </td>
                    </tr>
                `;
            }).join('') || '<tr><td colspan="6">Nenhum cliente cadastrado.</td></tr>';

            $('#qtdClientes').textContent = clientesComProjetos.length;
            $('#qtdAtivos').textContent = clientesComProjetos.filter(c => c.quantidadeProjetos > 0).length;
            $('#qtdSemProjeto').textContent = clientesComProjetos.filter(c => c.quantidadeProjetos === 0).length;

            document.dispatchEvent(new Event('beassi:clientes-carregados'));

        } catch (e) {
            tabela.innerHTML = `<tr><td colspan="6">${esc(e.message)} — verifique se o Back-End está em execução.</td></tr>`;
        }
    }


    // REATIVAR CONTA DE CLIENTE
    const tabelaClientes = $('#tabelaClientes');

    if (tabelaClientes && !tabelaClientes.dataset.reativacaoConfigurada) {
        tabelaClientes.dataset.reativacaoConfigurada = 'true';

        tabelaClientes.addEventListener('click', async (event) => {
            const botao = event.target.closest('[data-reativar-cliente]');

            if (!botao) return;

            const id = botao.dataset.reativarCliente;

            const confirmar = window.confirm(
                'Deseja realmente reativar a conta deste cliente?\n\n' +
                'O cliente poderá voltar a acessar o sistema.'
            );

            if (!confirmar) return;

            botao.disabled = true;
            botao.textContent = 'Reativando...';

            try {
                await BeassiAPI.patch(
                    `/Cliente/${encodeURIComponent(id)}/reativar`
                );

                const linha = botao.closest('tr');
                const status = linha?.querySelector('[data-cliente-status]');

                if (status) {
                    status.textContent = 'Conta ativa';
                    status.classList.remove('conta-inativa');
                    status.classList.add('conta-ativa');
                }

                botao.remove();

                beassiMensagem(
                    null,
                    'Conta do cliente reativada com sucesso.'
                );

            } catch (erro) {
                console.error('Erro ao reativar cliente:', erro);

                botao.disabled = false;
                botao.textContent = 'Reativar';

                beassiMensagem(
                    null,
                    erro.message || 'Não foi possível reativar a conta.',
                    true
                );
            }
        });
    }



    // Detalhes e edição do cliente usam sempre o ID real da URL, nunca IDs demonstrativos.
    const idCliente=beassiIdDaUrl('id');
    if (idCliente && location.pathname.endsWith('detalhes_cliente.html')) {
      try {
        const c=await BeassiAPI.get(`/Cliente/${encodeURIComponent(idCliente)}`);
        document.querySelectorAll('[data-cliente-campo]').forEach(el=>{const key=el.dataset.clienteCampo; el.textContent=key==='identificador'?`CLIENTE #${c.id_usuario??c.idUsuario??c.id}`:(c[key]??'—');});
        const status=document.querySelector('[data-cliente-status]'); if(status)status.textContent=c.status?'Cadastro ativo':'Cadastro inativo';
        const edit=document.querySelector('[data-link-editar-cliente]'); if(edit)edit.href=`editar_cliente.html?id=${encodeURIComponent(idCliente)}`;
        const projetos=document.querySelector('[data-lista-projetos-cliente]');
        if(projetos){const arr=beassiArray(await BeassiAPI.get(`/funcoes/clientes/${encodeURIComponent(idCliente)}/projetos`));projetos.innerHTML=arr.map(p=>`<div class="perfil-projeto-item"><div><strong>${esc(p.titulo)}</strong><span>Projeto · ${esc(p.etapa_atual??p.etapaAtual??'')}</span></div><a href="editar_projeto.html?id=${encodeURIComponent(p.id_projeto??p.idProjeto??p.id)}">Consultar projeto</a></div>`).join('')||'<p>Nenhum projeto vinculado.</p>';}
        const statusBtn=$('#btnStatusCliente'); if(statusBtn){statusBtn.textContent=c.status?'Cadastro ativo':'Cadastro inativo';statusBtn.disabled=true;}
      } catch(e) { const el=document.querySelector('[data-cliente-status]'); if(el)el.textContent=e.message; }
    }
    if (idCliente && location.pathname.endsWith('editar_cliente.html')) {
      const f=$('#formEditarAdmin'); if(f) try { const c=await BeassiAPI.get(`/Cliente/${encodeURIComponent(idCliente)}`); ['nome','email','telefone','cpf'].forEach(k=>{if(f.elements[k])f.elements[k].value=c[k]??'';}); } catch {}
      withSubmit(f, async form=>{await BeassiAPI.put(`/Cliente/${encodeURIComponent(idCliente)}`,formObject(form));show(form,'Dados do cliente atualizados.');});
    }

    // Logout real invalida sessão no servidor.
    $$('a[href="login.html"]').filter(a=>/sair/i.test(a.textContent)).forEach(a=>a.addEventListener('click',async e=>{e.preventDefault();try{await BeassiAPI.post('/Login/logout',{});
    }finally{location.href='index.html';}}));
  }
  document.addEventListener('DOMContentLoaded', () => init().catch(error => console.error('BEASSI:', error)));
})();

// Portfólio público e painel de projetos: os cards são construídos a partir dos registros da API.
document.addEventListener('DOMContentLoaded', async () => {
  const escapeHtml = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  let imagensPorProjeto = new Map(), categoriasPorProjeto = new Map();
  const card = p => {
    const id=p.id_projeto??p.idProjeto??p.id; const caminhoImagem=imagensPorProjeto.get(Number(id))?.[0]?.caminho??''; const img=caminhoImagem.startsWith('/')?`${window.BEASSI_API_BASE_URL||'http://localhost:8080'}${caminhoImagem}`:caminhoImagem; const tipo=String(p.tipoResidencial?'residencial':p.tipoComercial?'comercial':'').toLowerCase();
    return `<article class="projeto" data-projeto-card data-tipo="${escapeHtml(tipo)}" data-categorias="${escapeHtml((categoriasPorProjeto.get(Number(id))||[]).map(c=>String(c.categoria??c.nome??c).toLowerCase().replace(/\s+/g,'-')).join(' '))}"><a href="projeto_individual.html?projeto=${encodeURIComponent(id)}" class="projeto-link">${img?`<img src="${escapeHtml(img)}" alt="${escapeHtml(p.titulo)}">`:''}<div class="projeto-titulo">${escapeHtml(p.titulo)}</div></a></article>`;
  };
  try {
    const [imagens, associacoes] = await Promise.all([BeassiAPI.get('/imagem'), BeassiAPI.get('/projetoCategoria')]);
    imagensPorProjeto = new Map(); (Array.isArray(imagens)?imagens:[]).forEach(i=>{const pid=Number(i.projeto?.id_projeto);if(!imagensPorProjeto.has(pid))imagensPorProjeto.set(pid,[]);imagensPorProjeto.get(pid).push(i);});
    categoriasPorProjeto = new Map(); (Array.isArray(associacoes)?associacoes:[]).forEach(a=>{const pid=Number(a.projeto?.id_projeto);if(!categoriasPorProjeto.has(pid))categoriasPorProjeto.set(pid,[]);if(a.categoria)categoriasPorProjeto.get(pid).push(a.categoria);});
  } catch (e) { console.warn('Não foi possível carregar imagens e categorias do portfólio.',e); }
  const lista=document.querySelector('[data-lista-projetos]');
  if (lista) {
    try { 
    const todosProjetos = beassiArray(await BeassiAPI.get('/projeto'));

    console.table(todosProjetos.map(p => ({
        id: p.id_projeto ?? p.idProjeto ?? p.id,
        titulo: p.titulo,
        ativo: p.ativo,
        statusPublicacao: p.status_publicacao
    })));

    const data = todosProjetos.filter(p =>
        p.ativo === true && p.status_publicacao === true
    );
    lista.innerHTML=data.map(card).join('')||'<p>Nenhum projeto publicado no momento.</p>'; if(typeof window.configurarFiltrosProjetos==='function') window.configurarFiltrosProjetos(); }
    catch(e) { lista.innerHTML=`<p role="alert">Não foi possível carregar os projetos: ${escapeHtml(e.message)}</p>`; }
  }
  for (const bloco of document.querySelectorAll('[data-projetos-home]')) {
    try { const tipo=bloco.dataset.projetosHome; 
      const data = beassiArray(await BeassiAPI.get('/projeto'))
    .filter(p =>
        p.ativo === true &&
        p.status_publicacao === true &&
        (
            tipo.toUpperCase() === 'TODOS' ||
            (tipo.toUpperCase() === 'RESIDENCIAL' && p.tipoResidencial) ||
            (tipo.toUpperCase() === 'COMERCIAL' && p.tipoComercial)
        )
    );
       bloco.innerHTML=data.slice(0,3).map(card).join('')||'<p>Nenhum projeto publicado nesta categoria.</p>'; }
    catch(e) { bloco.innerHTML='<p>Não foi possível carregar os projetos neste momento.</p>'; }
  }
  const adminLista=document.getElementById('listaAdminProjetos');

if (adminLista) {
    try {
        const data = beassiArray(await BeassiAPI.get('/projeto'))
            .filter(p => p.ativo === true);

        adminLista.innerHTML = data.map(p => {
            const id = p.id_projeto ?? p.idProjeto ?? p.id;

            const caminhoImagem =
                imagensPorProjeto.get(Number(id))?.[0]?.caminho ?? '';

            const img = caminhoImagem.startsWith('/')
                ? `${window.BEASSI_API_BASE_URL || 'http://localhost:8080'}${caminhoImagem}`
                : caminhoImagem;

            return `
                <article class="admin-projeto"
                    data-nome="${escapeHtml(p.titulo)}"
                    data-tipo="${escapeHtml(String(
                        p.tipoResidencial ? 'residencial' :
                        p.tipoComercial ? 'comercial' : ''
                    ).toLowerCase())}"
                    data-publicacao="${p.status_publicacao === true ? 'publicos' : 'nao-publicos'}">

                    <a class="admin-projeto-link"
                       href="projeto_individual.html?projeto=${encodeURIComponent(id)}">

                        ${img ? `
                            <img src="${escapeHtml(img)}"
                                 alt="${escapeHtml(p.titulo)}">
                        ` : ''}

                        <div class="admin-projeto-info">
                            <span class="admin-projeto-tipo">
                                ${escapeHtml(p.tipoResidencial ? 'Residencial' : 'Comercial')}
                            </span>

                            <h2>${escapeHtml(p.titulo)}</h2>

                            <p>${escapeHtml(p.descricao ?? '')}</p>
                        </div>
                    </a>

                    <div class="admin-projeto-botoes">
                        <a class="admin-btn-editar"
                           href="editar_projeto.html?id=${encodeURIComponent(id)}">
                            Editar
                        </a>

                        <button class="admin-btn-excluir"
                                type="button"
                                data-excluir-projeto="${escapeHtml(id)}">
                            Excluir
                        </button>
                    </div>
                </article>
            `;
        }).join('') || '<p>Nenhum projeto cadastrado.</p>';

        adminLista.addEventListener('click', async event => {
            const btn = event.target.closest('[data-excluir-projeto]');
            if (!btn) return;

            if (!confirm('Deseja realmente excluir este projeto?')) return;

            try {
                await BeassiAPI.delete(`/projeto/${encodeURIComponent(btn.dataset.excluirProjeto)}`);
                btn.closest('.admin-projeto').remove();
            } catch (e) {
                beassiMensagem(null, e.message || 'Não foi possível atualizar o cadastro.', true);
            }
        });

        const busca = document.getElementById('buscaAdminProjetos');
        const tipo = document.getElementById('tipoAdminProjetos');
        const publicacao = document.getElementById('statusPublicacaoAdmin');
        const vazio = document.getElementById('semProjetosAdmin');

        const filtrar = () => {
            const termo = (busca?.value || '')
                .trim()
                .toLocaleLowerCase('pt-BR');

            const filtroTipo = tipo?.value || 'todos';
            const filtroPublicacao = publicacao?.value || 'todos';

            let qtd = 0;

            adminLista.querySelectorAll('.admin-projeto').forEach(card => {

                const correspondeNome = card.dataset.nome
                    .toLocaleLowerCase('pt-BR')
                    .includes(termo);

                const correspondeTipo =
                    filtroTipo === 'todos' ||
                    card.dataset.tipo === filtroTipo;

                const correspondePublicacao =
                    filtroPublicacao === 'todos' ||
                    card.dataset.publicacao === filtroPublicacao;

                const exibir =
                    correspondeNome &&
                    correspondeTipo &&
                    correspondePublicacao;

                card.hidden = !exibir;

                if (exibir) qtd++;
            });

            if (vazio) {
                vazio.hidden = qtd > 0;
            }
        };

        busca?.addEventListener('input', filtrar);
        tipo?.addEventListener('change', filtrar);
        publicacao?.addEventListener('change', filtrar);

        filtrar();

        busca?.addEventListener('input', filtrar);
        tipo?.addEventListener('change', filtrar);
        filtrar();

        document.dispatchEvent(new Event('beassi:projetos-carregados'));

    } catch (e) {
        adminLista.innerHTML = `<p role="alert">Não foi possível carregar os projetos: ${escapeHtml(e.message)}</p>`;
    }
}
});


document.addEventListener('DOMContentLoaded', async () => {
  const lista=document.getElementById('listaFeedbacks'); if(!lista||!window.BeassiAPI)return;
  const vazio=document.getElementById('feedbackVazio');
  const esc=v=>String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  try {
    const payload=await BeassiAPI.get('/Feedback');
    const items=(Array.isArray(payload)?payload:(payload?.content||payload?.items||[])).filter(f=>String(f.status_aprovacao??f.statusAprovacao??'').toUpperCase()==='APROVADO');
    lista.innerHTML=items.map(f=>{const n=Math.max(0,Math.min(5,Number(f.nota??0)));const nome=(f.exibir_nome??f.exibirNome)?(f.nomeCliente??f.cliente?.nome??'Cliente'):'Cliente BEASSI';return `<article class="feedback-card"><strong>${esc(nome)}</strong><div class="feedback-estrelas" aria-label="${n} de 5 estrelas">${'★'.repeat(n)}${'☆'.repeat(5-n)}</div><p>${esc(f.comentario)}</p></article>`;}).join('');
    if(!items.length&&vazio){vazio.hidden=false;lista.append(vazio);} else if(vazio)vazio.hidden=true;
  } catch { if(vazio){vazio.hidden=false;vazio.textContent='As avaliações estarão disponíveis em breve.';} }
}); 
