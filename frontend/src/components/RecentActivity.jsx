import { useMemo, useState } from 'react';
import { buildTransactionsCsv, filterTransactions } from '../transaction-utils';

const currency = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

function TransferIcon({ direction }) {
  return (
    <span className={`transfer-icon ${direction}`} aria-hidden="true">
      <svg viewBox="0 0 24 24">
        {direction === 'in' ? (
          <><path d="M12 4v14M7 13l5 5 5-5"/><path d="M6 21h12"/></>
        ) : (
          <><path d="m8 16 8-8M10 8h6v6"/></>
        )}
      </svg>
    </span>
  );
}

function formatDate(value) {
  if (!value) return { date: 'Today', time: '--:--' };
  const parsed = new Date(value);
  return {
    date: parsed.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }),
    time: parsed.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
  };
}

function RecentActivity({ transactions = [], loading, error, onDelete }) {
  const [typeFilter, setTypeFilter] = useState('ALL');
  const [search, setSearch] = useState('');
  const visibleTransactions = useMemo(
    () => filterTransactions(transactions, typeFilter, search),
    [transactions, typeFilter, search]
  );

  function exportStatement() {
    const blob = new Blob(['\ufeff', buildTransactionsCsv(visibleTransactions)], { type: 'text/csv;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `wallet-statement-${new Date().toISOString().slice(0, 10)}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  }

  return (
    <section className="activity-card-section" id="transactions">
      <div className="activity-card" aria-labelledby="activity-title">
        <div className="activity-card-header">
          <div className="activity-title-group">
            <h2 id="activity-title">Recent activity</h2>
            <span className="activity-count">{visibleTransactions.length} of {transactions.length} entries</span>
          </div>
          <button className="activity-export" type="button" onClick={exportStatement} disabled={!visibleTransactions.length}>
            Export CSV
          </button>
        </div>

        <div className="activity-toolbar">
          <label className="activity-search">
            <span className="sr-only">Search transactions</span>
            <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m16 16 4 4"/></svg>
            <input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search transactions" />
          </label>
          <div className="activity-filters" aria-label="Filter transactions by type">
            {[
              ['ALL', 'All'],
              ['CREDIT', 'Credits'],
              ['DEBIT', 'Debits']
            ].map(([value, label]) => (
              <button
                className={typeFilter === value ? 'active' : ''}
                type="button"
                key={value}
                aria-pressed={typeFilter === value}
                onClick={() => setTypeFilter(value)}
              >
                {label}
              </button>
            ))}
          </div>
        </div>

        <div className="activity-list">
          {loading && <p className="activity-message">Loading transactions...</p>}
          {!loading && error && <p className="activity-message error">{error}</p>}
          {!loading && !error && transactions.length === 0 && <p className="activity-message">No transactions yet. Use a quick action to create one.</p>}
          {!loading && !error && transactions.length > 0 && visibleTransactions.length === 0 && <p className="activity-message">No transactions match these filters.</p>}

          {!loading && !error && visibleTransactions.map((transaction) => {
            const direction = transaction.type === 'CREDIT' ? 'in' : 'out';
            const timestamp = formatDate(transaction.createdAt);
            const fallback = direction === 'in' ? 'Received money' : 'Sent money';
            return (
              <article className="activity-row" key={transaction.id}>
                <TransferIcon direction={direction} />
                <div className="activity-copy">
                  <strong>{transaction.description || fallback}</strong>
                  <span>{timestamp.date} <i /> {timestamp.time}</span>
                </div>
                <span className="activity-status"><i /> Completed</span>
                <strong className={`activity-amount ${direction}`}>
                  {direction === 'in' ? '+' : '-'}{currency.format(Number(transaction.amount) || 0)}
                </strong>
                <button className="activity-delete" type="button" onClick={() => onDelete(transaction)} aria-label={`Delete ${transaction.description || fallback}`} title="Delete transaction">×</button>
              </article>
            );
          })}
        </div>

      </div>
    </section>
  );
}

export default RecentActivity;
