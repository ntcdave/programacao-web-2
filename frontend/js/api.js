const API_BASE = 'http://localhost:8080/api';

async function apiRequest(method, path, body) {
  const opts = {
    method,
    headers: { 'Content-Type': 'application/json' },
  };
  if (body) opts.body = JSON.stringify(body);
  const res = await fetch(API_BASE + path, opts);
  if (!res.ok) {
    const errBody = await res.json().catch(() => ({}));
    throw new Error(errBody.erro || `Erro ${res.status}`);
  }
  if (res.status === 204) return null;
  return res.json();
}

const api = {
  vulnerabilidades: {
    listar: () => apiRequest('GET', '/vulnerabilidades'),
    buscar: (id) => apiRequest('GET', `/vulnerabilidades/${id}`),
    criar: (dados) => apiRequest('POST', '/vulnerabilidades', dados),
  },
  aplicacoes: {
    listar: () => apiRequest('GET', '/aplicacoes'),
    buscar: (id) => apiRequest('GET', `/aplicacoes/${id}`),
    criar: (dados) => apiRequest('POST', '/aplicacoes', dados),
  },
  ocorrencias: {
    listar: () => apiRequest('GET', '/ocorrencias'),
    buscar: (id) => apiRequest('GET', `/ocorrencias/${id}`),
    criar: (dados) => apiRequest('POST', '/ocorrencias', dados),
    atualizarStatus: (id, status) => apiRequest('PATCH', `/ocorrencias/${id}/status?status=${status}`),
  },
};
