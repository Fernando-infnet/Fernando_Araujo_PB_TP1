import assert from 'node:assert/strict';
import test from 'node:test';
import { buildTransactionsCsv, filterTransactions } from '../src/transaction-utils.js';

const transactions = [
  { id: 1, type: 'CREDIT', amount: 100, description: 'Depósito inicial', createdAt: '2026-09-28T12:00:00Z' },
  { id: 2, type: 'DEBIT', amount: 25, description: 'Pagamento PIX', createdAt: '2026-09-28T13:00:00Z' }
];

test('filtra transações por tipo', () => {
  assert.deepEqual(filterTransactions(transactions, 'DEBIT').map(({ id }) => id), [2]);
});

test('pesquisa descrição ignorando caixa e acentos', () => {
  assert.deepEqual(filterTransactions(transactions, 'ALL', 'deposito').map(({ id }) => id), [1]);
});

test('gera extrato CSV compatível com valores e descrições', () => {
  const csv = buildTransactionsCsv(transactions);

  assert.match(csv, /"Data";"Tipo";"Descrição";"Valor \(BRL\)";"Status"/);
  assert.match(csv, /"Crédito";"Depósito inicial";"100,00";"Concluída"/);
  assert.match(csv, /"Débito";"Pagamento PIX";"25,00";"Concluída"/);
});
