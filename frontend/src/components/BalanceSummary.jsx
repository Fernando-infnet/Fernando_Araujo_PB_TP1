const currency = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
  minimumFractionDigits: 2
});

function SummaryIcon({ type }) {
  const icons = {
    balance: <><path d="M4 6.5h14a2 2 0 0 1 2 2v10H4a2 2 0 0 1-2-2v-12a2 2 0 0 1 2-2h13"/><path d="M16 11h6v5h-6a2.5 2.5 0 0 1 0-5Z"/></>,
    income: <><path d="M12 4v14M7 13l5 5 5-5"/><path d="M6 21h12"/></>,
    expense: <><path d="m8 16 8-8M10 8h6v6"/></>,
    net: <path d="M3 12h3l2.2-5 3.5 10 2.5-7 1.8 3h5"/>
  };

  return <svg viewBox="0 0 24 24" aria-hidden="true">{icons[type]}</svg>;
}

function BalanceSummary({ balance = 0, transactions = [] }) {
  const totals = transactions.reduce((result, transaction) => {
    const amount = Number(transaction.amount) || 0;
    if (transaction.type === 'CREDIT') {
      result.income += amount;
      result.incomeCount += 1;
    }
    if (transaction.type === 'DEBIT') {
      result.expense += amount;
      result.expenseCount += 1;
    }
    return result;
  }, { income: 0, expense: 0, incomeCount: 0, expenseCount: 0 });

  const cards = [
    { type: 'balance', label: 'Total Balance', value: balance, change: 'Live', suffix: 'from transaction service' },
    { type: 'income', label: 'Income', value: totals.income, change: totals.incomeCount, suffix: 'recent credits' },
    { type: 'expense', label: 'Expenses', value: totals.expense, change: totals.expenseCount, suffix: 'recent debits' },
    { type: 'net', label: 'Net Flow', value: totals.income - totals.expense, change: 'Live', suffix: 'from recent activity' }
  ];

  return (
    <section className="summary-grid" aria-label="Wallet summary">
      {cards.map((card) => (
        <article className={`summary-card ${card.type}`} key={card.type}>
          <div className="summary-card-top">
            <span>{card.label}</span>
            <span className="summary-card-icon"><SummaryIcon type={card.type} /></span>
          </div>
          <strong>{currency.format(card.value)}</strong>
          <small><b>{card.change}</b> {card.suffix}</small>
        </article>
      ))}
    </section>
  );
}

export default BalanceSummary;
