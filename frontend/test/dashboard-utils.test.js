import assert from 'node:assert/strict';
import test from 'node:test';
import { buildBalanceSeries, chartGeometry, transactionTotals, transactionsForPeriod } from '../src/dashboard-utils.js';

const now = new Date('2026-09-28T12:00:00Z');
const transactions = [
  { type: 'CREDIT', amount: 100, createdAt: '2026-09-28T10:00:00Z' },
  { type: 'DEBIT', amount: 25, createdAt: '2026-09-27T10:00:00Z' },
  { type: 'CREDIT', amount: 50, createdAt: '2026-08-01T10:00:00Z' }
];

test('filtra lançamentos pelo período selecionado', () => {
  assert.equal(transactionsForPeriod(transactions, '7', now).length, 2);
  assert.equal(transactionsForPeriod(transactions, 'all', now).length, 3);
});

test('calcula entradas, saídas e série de saldo usando dados reais', () => {
  assert.deepEqual(transactionTotals(transactions.slice(0, 2)), { income: 100, expense: 25 });
  const series = buildBalanceSeries(transactions, 125, '7', now);
  assert.equal(series.length, 7);
  assert.equal(series.at(-1).balance, 125);
});

test('gera coordenadas SVG válidas para o gráfico', () => {
  const geometry = chartGeometry([{ balance: 10 }, { balance: 20 }]);
  assert.match(geometry.points, /^0\.0,/);
  assert.match(geometry.points, /560\.0,/);
  assert.equal(geometry.max, 20);
});
