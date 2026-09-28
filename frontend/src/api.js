export const WALLET_API_BASE = import.meta.env?.VITE_WALLET_API_URL || 'http://localhost:8080/api';
export const TRANSACTION_API_BASE = import.meta.env?.VITE_TRANSACTION_API_URL || 'http://localhost:8081/api';

export async function requestJson(url, options) {
  const response = await fetch(url, options);
  if (!response.ok) {
    const body = await response.json().catch(() => null);
    throw new Error(body?.message || 'Não foi possível conectar ao backend');
  }
  return response.status === 204 ? null : response.json();
}
