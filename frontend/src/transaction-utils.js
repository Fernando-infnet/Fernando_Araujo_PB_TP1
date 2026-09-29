function normalize(value) {
  return String(value ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();
}

export function filterTransactions(transactions, type = 'ALL', search = '') {
  const normalizedSearch = normalize(search).trim();

  return transactions.filter((transaction) => {
    const matchesType = type === 'ALL' || transaction.type === type;
    const searchableText = normalize(`${transaction.description ?? ''} ${transaction.amount ?? ''}`);
    return matchesType && (!normalizedSearch || searchableText.includes(normalizedSearch));
  });
}

function csvCell(value) {
  return `"${String(value ?? '').replaceAll('"', '""')}"`;
}

export function buildTransactionsCsv(transactions) {
  const header = ['Data', 'Tipo', 'Descrição', 'Valor (BRL)', 'Status'];
  const rows = transactions.map((transaction) => [
    transaction.createdAt ? new Date(transaction.createdAt).toLocaleString('pt-BR') : '',
    transaction.type === 'CREDIT' ? 'Crédito' : 'Débito',
    transaction.description || '',
    Number(transaction.amount || 0).toFixed(2).replace('.', ','),
    'Concluída'
  ]);

  return [header, ...rows].map((row) => row.map(csvCell).join(';')).join('\r\n');
}
