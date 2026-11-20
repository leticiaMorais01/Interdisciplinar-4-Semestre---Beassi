document.addEventListener('DOMContentLoaded', () => {
  const container=document.getElementById('categoriasContainer');
  const botaoNova=document.getElementById('btnNovaCategoria');
  const campoNova=document.getElementById('novaCategoriaCampo');
  const nomeNova=document.getElementById('nomeNovaCategoria');
  const botaoAdicionar=document.getElementById('btnAdicionarCategoria');
  const mensagem=document.getElementById('mensagemCategoria');
  if(!container||!botaoNova||!campoNova||!nomeNova||!botaoAdicionar||!mensagem)return;
  botaoNova.addEventListener('click',()=>{campoNova.hidden=!campoNova.hidden;mensagem.textContent='';if(!campoNova.hidden)nomeNova.focus();});
  botaoAdicionar.addEventListener('click',async()=>{
    const nome=nomeNova.value.trim();if(!nome){mensagem.textContent='Digite o nome da categoria.';nomeNova.focus();return;}
    const duplicada=[...container.querySelectorAll('.categoria-item span')].some(el=>el.textContent.trim().toLocaleLowerCase('pt-BR')===nome.toLocaleLowerCase('pt-BR'));
    if(duplicada){mensagem.textContent='Essa categoria já existe.';nomeNova.focus();return;}
    if(!window.BeassiAPI){mensagem.textContent='Serviço de integração indisponível.';return;}
    botaoAdicionar.disabled=true;
    try{
      const criada=await BeassiAPI.post('/categoria',{categoria:nome});
      const id=criada.id_categoria??criada.id_categoria??criada.idCategoria??criada.id;if(id==null)throw new Error('A API não retornou o identificador da categoria.');
      const label=document.createElement('label');label.className='categoria-item';
      const input=document.createElement('input');input.type='checkbox';input.name='categorias[]';input.value=String(id);input.checked=true;
      const span=document.createElement('span');span.textContent=criada.categoria??nome;
      label.append(input,span);container.append(label);nomeNova.value='';mensagem.textContent='Categoria cadastrada.';campoNova.hidden=true;
    }catch(e){mensagem.textContent=e.message||'Não foi possível cadastrar a categoria.';}
    finally{botaoAdicionar.disabled=false;}
  });
});
