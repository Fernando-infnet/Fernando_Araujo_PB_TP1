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
  return (

    <section className="activity-card-section">
      <div className="activity-card" aria-labelledby="activity-title">
        <div className="activity-card-header">
          <h2 id="activity-title">Recent activity</h2>
          <span className="activity-count">{transactions.length} entries</span>
        </div>

        <div className="activity-list">
          {loading && <p className="activity-message">Loading transactions...</p>}
          {!loading && error && <p className="activity-message error">{error}</p>}
          {!loading && !error && transactions.length === 0 && <p className="activity-message">No transactions yet. Use a quick action to create one.</p>}

          {!loading && !error && transactions.map((transaction) => {
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
                <button className="activity-delete" type="button" onClick={() => onDelete(transaction.id)} aria-label={`Delete ${transaction.description || fallback}`} title="Delete transaction">×</button>
              </article>
            );
          })}
        </div>

        {transactions.length > 0 && (
          <button type="button" className="load-more-button">
            Load more entries
            <span aria-hidden="true">

            </span>
          </button>
        )}
      </div>
    </section>
  );
}

export default RecentActivity;
