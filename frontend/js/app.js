// ===== Abas =====
document.querySelectorAll('.tab').forEach(tab => {
  tab.addEventListener('click', () => {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
    tab.classList.add('active');
    document.getElementById(tab.dataset.tab).classList.add('active');
  });
});

// ===== Toast =====
function mostrarToast(msg, tipo = 'success') {
  const t = document.getElementById('toast');
  t.textContent = msg;
  t.className = `toast ${tipo}`;
  setTimeout(() => t.classList.add('hidden'), 3000);
}

// ===== Modal =====
let modalTipo = null;
let modalResolve = null;

function abrirModal(tipo) {
  modalTipo = tipo;
  document.getElementById('modal').classList.remove('hidden');
  const title = document.getElementById('modal-title');
  const fields = document.getElementById('modal-fields');
  fields.innerHTML = '';
  document.getElementById('modal-form').onsubmit = null;

  if (tipo === 'vulnerabilidade') {
    title.textContent = 'Nova Vulnerabilidade';
    fields.innerHTML = `
      <div class="form-group">
        <label for="f-codigo">Código</label>
        <input id="f-codigo" required placeholder="Ex: CVE-2024-1234">
      </div>
      <div class="form-group">
        <label for="f-descricao">Descrição</label>
        <textarea id="f-descricao" required placeholder="Descreva a vulnerabilidade"></textarea>
      </div>
      <div class="form-group">
        <label for="f-gravidade">Gravidade</label>
        <select id="f-gravidade" required>
          <option value="">Selecione...</option>
          <option value="BAIXA">Baixa</option>
          <option value="MEDIA">Média</option>
          <option value="ALTA">Alta</option>
        </select>
      </div>
    `;
  } else if (tipo === 'aplicacao') {
    title.textContent = 'Nova Aplicação';
    fields.innerHTML = `
      <div class="form-group">
        <label for="f-nome">Nome</label>
        <input id="f-nome" required placeholder="Ex: Sistema de Login">
      </div>
      <div class="form-group">
        <label for="f-linguagem">Linguagem</label>
        <input id="f-linguagem" required placeholder="Ex: Java, Python, JavaScript">
      </div>
      <div class="form-group">
        <label for="f-repositorio">URL do Repositório</label>
        <input id="f-repositorio" placeholder="https://github.com/usuario/repo">
      </div>
    `;
  } else if (tipo === 'ocorrencia') {
    title.textContent = 'Nova Ocorrência';
    fields.innerHTML = `
      <div class="form-group">
        <label for="f-aplicacao">Aplicação</label>
        <select id="f-aplicacao" required><option value="">Carregando...</option></select>
      </div>
      <div class="form-group">
        <label for="f-vulnerabilidade">Vulnerabilidade</label>
        <select id="f-vulnerabilidade" required><option value="">Carregando...</option></select>
      </div>
      <div class="form-group">
        <label for="f-data">Data da Descoberta</label>
        <input id="f-data" type="date" required>
      </div>
    `;
    carregarSelectsOcorrencia();
  }
}

function fecharModal() {
  document.getElementById('modal').classList.add('hidden');
  modalTipo = null;
}

document.getElementById('modal-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const tipo = modalTipo;
  let dados;

  if (tipo === 'vulnerabilidade') {
    dados = {
      codigo: document.getElementById('f-codigo').value,
      descricao: document.getElementById('f-descricao').value,
      gravidade: document.getElementById('f-gravidade').value,
    };
  } else if (tipo === 'aplicacao') {
    dados = {
      nome: document.getElementById('f-nome').value,
      linguagem: document.getElementById('f-linguagem').value,
      repositorioUrl: document.getElementById('f-repositorio').value || null,
    };
  } else if (tipo === 'ocorrencia') {
    dados = {
      aplicacao: { id: parseInt(document.getElementById('f-aplicacao').value) },
      vulnerabilidade: { id: parseInt(document.getElementById('f-vulnerabilidade').value) },
      dataDescoberta: document.getElementById('f-data').value,
    };
  }

  try {
    if (tipo === 'vulnerabilidade') await api.vulnerabilidades.criar(dados);
    else if (tipo === 'aplicacao') await api.aplicacoes.criar(dados);
    else if (tipo === 'ocorrencia') await api.ocorrencias.criar(dados);
    fecharModal();
    mostrarToast(`${tipo} criada com sucesso!`);
    carregarTabelas();
  } catch (err) {
    mostrarToast(err.message, 'error');
  }
});

async function carregarSelectsOcorrencia() {
  try {
    const [aplicacoes, vulnerabilidades] = await Promise.all([
      api.aplicacoes.listar(),
      api.vulnerabilidades.listar(),
    ]);
    const selA = document.getElementById('f-aplicacao');
    selA.innerHTML = '<option value="">Selecione...</option>';
    aplicacoes.forEach(a => {
      selA.innerHTML += `<option value="${a.id}">${a.nome} (${a.linguagem})</option>`;
    });
    const selV = document.getElementById('f-vulnerabilidade');
    selV.innerHTML = '<option value="">Selecione...</option>';
    vulnerabilidades.forEach(v => {
      selV.innerHTML += `<option value="${v.id}">${v.codigo} - ${v.gravidade}</option>`;
    });
  } catch (err) {
    mostrarToast('Erro ao carregar dados para ocorrência: ' + err.message, 'error');
  }
}

// ===== Tabelas =====
async function carregarTabelas() {
  await Promise.all([
    carregarVulnerabilidades(),
    carregarAplicacoes(),
    carregarOcorrencias(),
  ]);
}

function badgeGravidade(g) {
  const cls = `badge-${g.toLowerCase()}`;
  return `<span class="badge ${cls}">${g}</span>`;
}

function badgeStatus(s) {
  const cls = `badge-${s.toLowerCase()}`;
  const nomes = { ABERTA: 'Aberta', EM_CORRECAO: 'Em Correção', RESOLVIDA: 'Resolvida' };
  return `<span class="badge ${cls}">${nomes[s] || s}</span>`;
}

async function carregarVulnerabilidades() {
  try {
    const dados = await api.vulnerabilidades.listar();
    const tbody = document.getElementById('tbody-vulnerabilidades');
    if (!dados.length) {
      tbody.innerHTML = '<tr><td colspan="4" class="empty-msg">Nenhuma vulnerabilidade cadastrada</td></tr>';
      return;
    }
    tbody.innerHTML = dados.map(v => `
      <tr>
        <td>${v.id}</td>
        <td><strong>${v.codigo}</strong></td>
        <td>${v.descricao}</td>
        <td>${badgeGravidade(v.gravidade)}</td>
      </tr>
    `).join('');
  } catch (err) {
    document.getElementById('tbody-vulnerabilidades').innerHTML =
      `<tr><td colspan="4" class="empty-msg">Erro ao carregar: ${err.message}</td></tr>`;
  }
}

async function carregarAplicacoes() {
  try {
    const dados = await api.aplicacoes.listar();
    const tbody = document.getElementById('tbody-aplicacoes');
    if (!dados.length) {
      tbody.innerHTML = '<tr><td colspan="4" class="empty-msg">Nenhuma aplicação cadastrada</td></tr>';
      return;
    }
    tbody.innerHTML = dados.map(a => `
      <tr>
        <td>${a.id}</td>
        <td><strong>${a.nome}</strong></td>
        <td>${a.linguagem}</td>
        <td>${a.repositorioUrl ? `<a href="${a.repositorioUrl}" target="_blank">${a.repositorioUrl}</a>` : '-'}</td>
      </tr>
    `).join('');
  } catch (err) {
    document.getElementById('tbody-aplicacoes').innerHTML =
      `<tr><td colspan="4" class="empty-msg">Erro ao carregar: ${err.message}</td></tr>`;
  }
}

async function carregarOcorrencias() {
  try {
    const dados = await api.ocorrencias.listar();
    const tbody = document.getElementById('tbody-ocorrencias');
    if (!dados.length) {
      tbody.innerHTML = '<tr><td colspan="6" class="empty-msg">Nenhuma ocorrência cadastrada</td></tr>';
      return;
    }
    tbody.innerHTML = dados.map(o => `
      <tr>
        <td>${o.id}</td>
        <td>${o.aplicacao?.nome || '-'}</td>
        <td>${o.vulnerabilidade?.codigo || '-'}</td>
        <td>${o.dataDescoberta ? new Date(o.dataDescoberta).toLocaleDateString('pt-BR') : '-'}</td>
        <td>${badgeStatus(o.status)}</td>
        <td>
          <div class="acoes">
            ${botoesStatus(o)}
          </div>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    document.getElementById('tbody-ocorrencias').innerHTML =
      `<tr><td colspan="6" class="empty-msg">Erro ao carregar: ${err.message}</td></tr>`;
  }
}

function botoesStatus(o) {
  const btns = {
    ABERTA: `<button class="btn btn-warning btn-sm" onclick="atualizarStatus(${o.id}, 'EM_CORRECAO')">Iniciar Correção</button>`,
    EM_CORRECAO: `<button class="btn btn-success btn-sm" onclick="atualizarStatus(${o.id}, 'RESOLVIDA')">Resolver</button>`,
    RESOLVIDA: `<button class="btn btn-sm" style="background:#95a5a6;color:#fff" onclick="atualizarStatus(${o.id}, 'ABERTA')">Reabrir</button>`,
  };
  return btns[o.status] || '';
}

async function atualizarStatus(id, novoStatus) {
  try {
    await api.ocorrencias.atualizarStatus(id, novoStatus);
    mostrarToast('Status atualizado com sucesso!');
    carregarOcorrencias();
  } catch (err) {
    mostrarToast(err.message, 'error');
  }
}

// ===== Inicialização =====
document.addEventListener('DOMContentLoaded', carregarTabelas);
