/* Atualiza somente a prévia visual. A persistência do progresso é feita pelo formulário/API. */
document.addEventListener('DOMContentLoaded', () => {
  const etapa=document.getElementById('etapaProjeto');const valor=document.getElementById('valorProgresso');const barra=document.getElementById('barraProgresso');const preenchimento=document.getElementById('preenchimentoProgresso');const texto=document.getElementById('textoProgresso');
  if(!etapa||!valor||!barra||!preenchimento||!texto)return;
  const percentuais={'nao-iniciado':0,'medicao':17,'desenvolvimento':33,'apresentacao':50,'revisao':67,'executivo':83,'concluido':100};
  const atualizar=()=>{const pct=percentuais[etapa.value]??0;const nome=etapa.options[etapa.selectedIndex]?.text??'';valor.textContent=`${pct}%`;preenchimento.style.width=`${pct}%`;barra.setAttribute('aria-valuenow',pct);texto.textContent=pct===100?'Prévia: projeto concluído.':pct===0?'Prévia: projeto ainda não iniciado.':`Prévia: ${nome} — ${pct}% de conclusão.`;};
  etapa.addEventListener('change',atualizar);atualizar();
});
