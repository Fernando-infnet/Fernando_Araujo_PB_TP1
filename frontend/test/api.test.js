import assert from 'node:assert/strict';
import test from 'node:test';
import { requestJson } from '../src/api.js';

test('requestJson retorna o corpo de uma resposta bem-sucedida', async (t) => {
  t.mock.method(globalThis, 'fetch', async () => ({
    ok: true,
    status: 200,
    json: async () => ({ status: 'UP' })
  }));

  assert.deepEqual(await requestJson('/health'), { status: 'UP' });
});

test('requestJson usa a mensagem da API em respostas de erro', async (t) => {
  t.mock.method(globalThis, 'fetch', async () => ({
    ok: false,
    status: 400,
    json: async () => ({ message: 'Valor inválido' })
  }));

  await assert.rejects(() => requestJson('/transactions'), /Valor inválido/);
});

test('requestJson trata respostas sem conteúdo', async (t) => {
  t.mock.method(globalThis, 'fetch', async () => ({
    ok: true,
    status: 204,
    json: async () => { throw new Error('não deveria ler o corpo'); }
  }));

  assert.equal(await requestJson('/transactions/1', { method: 'DELETE' }), null);
});
