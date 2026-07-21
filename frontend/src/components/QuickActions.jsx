import { useState } from 'react';

const actions = [
  { key: 'send', label: 'Send', description: 'Send money', type: 'SAIDA', icon: '↗' },
  { key: 'receive', label: 'Receive', description: 'Receive money', type: 'ENTRADA', icon: '↓' },
  { key: 'pix', label: 'PIX', description: 'Pay with PIX', type: 'SAIDA', icon: '◆' },
  { key: 'add', label: 'Add funds', description: 'Top up balance', type: 'ENTRADA', icon: '+' }
];

function QuickActions({ onCreate }) {
  const [selected, setSelected] = useState(null);
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);

  function closeForm() {
    setSelected(null);
    setAmount('');
    setDescription('');
  }

  async function submit(event) {
    event.preventDefault();
    if (!amount || Number(amount) <= 0) return;
    setSubmitting(true);
    await onCreate({ type: selected.type, amount: Number(amount), description: description || selected.description });
    setSubmitting(false);
    closeForm();
  }

  return (
    <section className="quick-card" aria-labelledby="quick-title">
      <h2 id="quick-title">Quick actions</h2>
      <div className="quick-grid">
        {actions.map((action) => (
          <button type="button" className="quick-action" key={action.key} onClick={() => setSelected(action)}>
            <span className={`quick-icon ${action.key}`}>{action.icon}</span>
            <strong>{action.label}</strong>
            <small>{action.description}</small>
          </button>
        ))}
      </div>
      <button type="button" className="all-actions-button">View all actions <span>⌃</span></button>

      {selected && (
        <div className="action-dialog-backdrop" onMouseDown={(event) => event.target === event.currentTarget && closeForm()}>
          <form className="action-dialog" onSubmit={submit}>
            <button type="button" className="dialog-close" onClick={closeForm} aria-label="Close">×</button>
            <span className={`quick-icon ${selected.key}`}>{selected.icon}</span>
            <h3>{selected.label} money</h3>
            <p>{selected.type === 'SAIDA' ? 'Create an outgoing transaction.' : 'Create an incoming transaction.'}</p>
            <label>Amount (R$)<input autoFocus type="number" min="0.01" step="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} required /></label>
            <label>Description<input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Optional description" /></label>
            <button className="dialog-submit" type="submit" disabled={submitting}>{submitting ? 'Processing...' : `Confirm ${selected.label}`}</button>
          </form>
        </div>
      )}
    </section>
  );
}

export default QuickActions;
