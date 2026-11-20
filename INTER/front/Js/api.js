/* BEASSI — camada única de comunicação com o Back-End.
   Ajuste API_BASE_URL para o endereço da aplicação Spring Boot.
   Recomendação: autenticação por cookie HttpOnly/Secure/SameSite; não guardar senha/token no localStorage. */
const BEASSI_API_BASE_URL = window.BEASSI_API_BASE_URL || 'http://localhost:8080';

const BeassiAPI = {
  async request(path, options = {}) {
    const headers = new Headers(options.headers || {});
    const isFormData = options.body instanceof FormData;
    if (!isFormData && options.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json');
    headers.set('Accept', 'application/json');
    const response = await fetch(`${BEASSI_API_BASE_URL}${path}`, {
      ...options, headers, credentials: 'include'
    });
    const raw = await response.text();
    let data = null;
    if (raw) { try { data = JSON.parse(raw); } catch { data = raw; } }
    if (!response.ok) {
      const extrairMensagem = valor => {
        if (!valor) return '';
        if (typeof valor === 'string') return valor;
        if (Array.isArray(valor)) return valor.map(extrairMensagem).filter(Boolean).join(' ');
        if (typeof valor === 'object') {
          const direta = valor.mensagem || valor.message || valor.detail || valor.error;
          if (direta) return extrairMensagem(direta);
          if (valor.errors) return extrairMensagem(valor.errors);
          if (valor.fieldErrors) return extrairMensagem(valor.fieldErrors);
        }
        return '';
      };
      const message = extrairMensagem(data) || `Não foi possível concluir a operação (HTTP ${response.status}).`;
      const error = new Error(message); error.status = response.status; error.data = data; throw error;
    }
    return data;
  },
  get: path => BeassiAPI.request(path),
  post: (path, body) => BeassiAPI.request(path, { method: 'POST', body: body instanceof FormData ? body : JSON.stringify(body) }),
  put: (path, body) => BeassiAPI.request(path, { method: 'PUT', body: body instanceof FormData ? body : JSON.stringify(body) }),
  patch: (path, body) => BeassiAPI.request(path, { method: 'PATCH', ...(body == null ? {} : { body: JSON.stringify(body) }) }),
  delete: path => BeassiAPI.request(path, { method: 'DELETE' })
};
window.BeassiAPI = BeassiAPI;

function beassiMensagem(elemento, texto, erro = false) {
  if (elemento) {
    elemento.textContent = texto;
    elemento.classList.toggle('erro', !!erro);
    elemento.classList.add('visivel');
  }

  // Mensagem central, destacada e acessível para retornos da API.
  let modal = document.getElementById('beassi-feedback-global');
  if (!modal) {
    modal = document.createElement('div');
    modal.id = 'beassi-feedback-global';
    modal.className = 'beassi-feedback-overlay';
    modal.innerHTML = `
      <section class="beassi-feedback-card" role="alertdialog" aria-modal="true"
               aria-labelledby="beassi-feedback-titulo" aria-describedby="beassi-feedback-texto">
        <div class="beassi-feedback-icone" aria-hidden="true"></div>
        <h2 id="beassi-feedback-titulo"></h2>
        <p id="beassi-feedback-texto"></p>
        <button type="button" class="beassi-feedback-fechar">Entendi</button>
      </section>`;
    document.body.append(modal);
    modal.querySelector('.beassi-feedback-fechar').addEventListener('click', () => {
      modal.classList.remove('ativo');
    });
    modal.addEventListener('click', event => {
      if (event.target === modal) modal.classList.remove('ativo');
    });
    document.addEventListener('keydown', event => {
      if (event.key === 'Escape') modal.classList.remove('ativo');
    });
  }

  const card = modal.querySelector('.beassi-feedback-card');
  const titulo = modal.querySelector('#beassi-feedback-titulo');
  const mensagem = modal.querySelector('#beassi-feedback-texto');
  const icone = modal.querySelector('.beassi-feedback-icone');
  card.classList.toggle('erro', !!erro);
  titulo.textContent = erro ? 'Não foi possível concluir' : 'Tudo certo!';
  mensagem.textContent = texto;
  icone.textContent = erro ? '!' : '✓';
  modal.classList.add('ativo');
  modal.querySelector('.beassi-feedback-fechar').focus();
}

function aplicarMascaraCPF(valor) {
  const numeros = String(valor || '').replace(/\D/g, '').slice(0, 11);
  return numeros
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
}

function aplicarMascaraTelefone(valor) {
  const numeros = String(valor || '').replace(/\D/g, '').slice(0, 11);
  if (numeros.length <= 10) {
    return numeros
      .replace(/(\d{2})(\d)/, '($1) $2')
      .replace(/(\d{4})(\d)/, '$1-$2');
  }
  return numeros
    .replace(/(\d{2})(\d)/, '($1) $2')
    .replace(/(\d{5})(\d)/, '$1-$2');
}

document.addEventListener('DOMContentLoaded', () => {
  if (!document.getElementById('beassi-feedback-estilos')) {
    const estilos = document.createElement('style');
    estilos.id = 'beassi-feedback-estilos';
    estilos.textContent = `
      .beassi-feedback-overlay {
        position: fixed; inset: 0; z-index: 99999; display: none;
        align-items: center; justify-content: center; padding: 24px;
        background: rgba(24, 29, 22, .62); backdrop-filter: blur(3px);
      }
      .beassi-feedback-overlay.ativo { display: flex; }
      .beassi-feedback-card {
        width: min(460px, 100%); box-sizing: border-box; padding: 32px 28px;
        border-radius: 16px; background: #fff; color: #474E3A;
        box-shadow: 0 22px 70px rgba(0,0,0,.28); text-align: center;
        font-family: Montserrat, Arial, sans-serif; border-top: 6px solid #52694c;
      }
      .beassi-feedback-card.erro { border-top-color: #a63d35; }
      .beassi-feedback-icone {
        display: grid; place-items: center; width: 54px; height: 54px;
        margin: 0 auto 16px; border-radius: 50%; background: #edf3eb;
        color: #52694c; font-size: 30px; font-weight: 700;
      }
      .beassi-feedback-card.erro .beassi-feedback-icone {
        background: #f9e9e7; color: #a63d35;
      }
      .beassi-feedback-card h2 { margin: 0 0 12px; font-size: 21px; color: #474E3A; }
      .beassi-feedback-card p { margin: 0; color: #514d48; font-size: 14px; line-height: 1.65; overflow-wrap: anywhere; }
      .beassi-feedback-fechar {
        margin-top: 24px; min-width: 130px; padding: 12px 22px; border: 0;
        border-radius: 7px; background: #474E3A; color: #fff; cursor: pointer;
        font: 600 13px Montserrat, Arial, sans-serif;
      }
      .beassi-feedback-fechar:hover { background: #AF7864; }
      .beassi-feedback-fechar:focus-visible { outline: 3px solid #AF7864; outline-offset: 3px; }
      @media (max-width: 480px) {
        .beassi-feedback-card { padding: 26px 20px; }
        .beassi-feedback-card h2 { font-size: 19px; }
      }
    `;
    document.head.append(estilos);
  }

  document.querySelectorAll('input[name="cpf"], input[id*="cpf" i]').forEach(input => {
    input.inputMode = 'numeric';
    input.autocomplete = input.autocomplete || 'off';
    input.maxLength = 14;
    input.addEventListener('input', () => {
      const inicio = input.selectionStart;
      const anterior = input.value;
      input.value = aplicarMascaraCPF(input.value);
      if (document.activeElement === input && inicio === anterior.length) {
        input.setSelectionRange(input.value.length, input.value.length);
      }
    });
  });

  document.querySelectorAll('input[type="tel"], input[name="telefone"], input[id*="telefone" i]').forEach(input => {
    input.inputMode = 'tel';
    input.autocomplete = input.autocomplete || 'tel';
    input.maxLength = 15;
    input.addEventListener('input', () => {
      const anterior = input.value;
      input.value = aplicarMascaraTelefone(input.value);
      if (document.activeElement === input && input.selectionStart === anterior.length) {
        input.setSelectionRange(input.value.length, input.value.length);
      }
    });
  });

  if (new URLSearchParams(location.search).get('cadastro') === 'sucesso') {
    beassiMensagem(null, 'Seu cadastro foi realizado com sucesso. Agora você já pode entrar com seu e-mail e senha.', false);
    history.replaceState({}, document.title, location.pathname);
  }
});
function beassiIdDaUrl(chave = 'id') { return new URLSearchParams(location.search).get(chave); }
function beassiArray(payload) {
  if (Array.isArray(payload)) return payload;
  return payload?.content || payload?.items || payload?.data || [];
}
