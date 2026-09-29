const DAY_IN_MS = 24 * 60 * 60 * 1000;

export const PERIODS = {
  7: { label: 'Last 7 days', days: 7 },
  30: { label: 'Last 30 days', days: 30 },
  all: { label: 'All time', days: null }
};

function startOfDay(value) {
  const date = new Date(value);
  date.setHours(0, 0, 0, 0);
  return date;
}

export function transactionsForPeriod(transactions, period, now = new Date()) {
  const days = PERIODS[period]?.days;
  if (!days) return [...transactions];
  const cutoff = startOfDay(now).getTime() - (days - 1) * DAY_IN_MS;
  return transactions.filter((transaction) => new Date(transaction.createdAt).getTime() >= cutoff);
}

export function transactionTotals(transactions) {
  return transactions.reduce((totals, transaction) => {
    const amount = Number(transaction.amount) || 0;
    if (transaction.type === 'CREDIT') totals.income += amount;
    if (transaction.type === 'DEBIT') totals.expense += amount;
    return totals;
  }, { income: 0, expense: 0 });
}

export function buildBalanceSeries(transactions, currentBalance, period, now = new Date()) {
  const periodTransactions = transactionsForPeriod(transactions, period, now);
  const configuredDays = PERIODS[period]?.days;
  const datedTransactions = periodTransactions
    .filter((transaction) => transaction.createdAt && !Number.isNaN(new Date(transaction.createdAt).getTime()))
    .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));

  const firstDate = datedTransactions.length ? startOfDay(datedTransactions[0].createdAt) : startOfDay(now);
  const lastDate = startOfDay(now);
  const inferredDays = Math.max(1, Math.round((lastDate - firstDate) / DAY_IN_MS) + 1);
  const days = configuredDays || inferredDays;
  const startDate = new Date(lastDate.getTime() - (days - 1) * DAY_IN_MS);

  const periodDelta = datedTransactions.reduce((delta, transaction) => {
    const amount = Number(transaction.amount) || 0;
    return delta + (transaction.type === 'CREDIT' ? amount : -amount);
  }, 0);
  let runningBalance = Number(currentBalance) - periodDelta;

  return Array.from({ length: days }, (_, index) => {
    const date = new Date(startDate.getTime() + index * DAY_IN_MS);
    const dayKey = date.toISOString().slice(0, 10);
    datedTransactions
      .filter((transaction) => startOfDay(transaction.createdAt).getTime() === date.getTime())
      .forEach((transaction) => {
        const amount = Number(transaction.amount) || 0;
        runningBalance += transaction.type === 'CREDIT' ? amount : -amount;
      });
    return { date: dayKey, balance: runningBalance };
  });
}

export function chartGeometry(series, width = 560, height = 222) {
  if (!series.length) return { points: '', area: '', max: 0, min: 0 };
  const values = series.map((point) => point.balance);
  const min = Math.min(0, ...values);
  const max = Math.max(1, ...values);
  const range = max - min || 1;
  const points = series.map((point, index) => {
    const x = series.length === 1 ? width : index * width / (series.length - 1);
    const y = height - ((point.balance - min) / range) * (height - 12) - 6;
    return `${x.toFixed(1)},${y.toFixed(1)}`;
  }).join(' ');
  return { points, area: `0,${height} ${points} ${width},${height}`, max, min };
}
