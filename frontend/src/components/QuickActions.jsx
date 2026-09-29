import { useEffect, useState } from 'react';

const actions = [
  { key: 'send', label: 'Send', description: 'Send money', type: 'DEBIT', icon: '↗', svg: <><path d="M5 11.5 20 4l-7.5 15-2.2-6.3L5 11.5Z"/><path d="m10.3 12.7 4.1-4.1"/></> },
  { key: 'receive', label: 'Receive', description: 'Receive money', type: 'CREDIT', icon: '↓', svg: <><path d="M12 3v14M7 12l5 5 5-5"/><path d="M5 21h14"/></> },
  { key: 'pix', label: 'PIX', description: 'Pay with PIX', type: 'DEBIT', icon: '◆', svg: <><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 297 297" fill="white">
  <path d="M231.433 227.02C219.791 227.02 208.84 222.487 200.607 214.257L156.096 169.745C152.971 166.612 147.524 166.621 144.4 169.745L99.7266 214.42C91.4932 222.649 80.5425 227.183 68.8998 227.183H60.1281L116.503 283.556C134.108 301.161 162.653 301.161 180.26 283.556L236.795 227.02H231.433Z" fill="#25ddb7"/>
  <path d="M68.8992 69.5768C80.5419 69.5768 91.4927 74.1101 99.726 82.3395L144.399 127.021C147.617 130.239 152.87 130.251 156.095 127.017L200.606 82.5023C208.839 74.273 219.79 69.7397 231.433 69.7397H236.794L180.261 13.205C162.653 -4.40167 134.107 -4.40167 116.502 13.205L60.13 69.577L68.8992 69.5768Z" fill="#25ddb7"/>
  <path d="M283.557 116.501L249.393 82.3374C248.641 82.6386 247.826 82.8267 246.966 82.8267H231.433C223.402 82.8267 215.541 86.084 209.866 91.7627L165.357 136.273C161.192 140.439 155.718 142.523 150.25 142.523C144.777 142.523 139.308 140.439 135.144 136.277L90.4662 91.6C84.7915 85.92 76.9302 82.664 68.8995 82.664H49.7996C48.9849 82.664 48.2236 82.472 47.5049 82.2013L13.205 116.501C-4.40167 134.108 -4.40167 162.652 13.205 180.259L47.5036 214.557C48.2236 214.287 48.9849 214.095 49.7996 214.095H68.8995C76.9302 214.095 84.7915 210.839 90.4662 205.16L135.14 160.487C143.214 152.419 157.29 152.416 165.357 160.49L209.866 204.997C215.541 210.676 223.402 213.933 231.433 213.933H246.966C247.826 213.933 248.641 214.121 249.393 214.423L283.557 180.259C301.162 162.652 301.162 134.108 283.557 116.501Z" fill="#25ddb7"/>
</svg></> },
  { key: 'add', label: 'Add funds', description: 'Top up balance', type: 'CREDIT', icon: '+', svg: <><circle cx="12" cy="12" r="9"/><path d="M12 8v8M8 12h8"/></> }
];

function ActionIcon({ action }) {
  return (
    <span className={`quick-icon ${action.key}`} aria-hidden="true">
      {action.svg ? <svg viewBox="0 0 24 24">{action.svg}</svg> : action.icon}
    </span>
  );
}

function QuickActions({ onCreate, requestedAction, onActionOpened, onViewHistory }) {
  const [selected, setSelected] = useState(null);
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [recipient, setRecipient] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [actionError, setActionError] = useState('');

  useEffect(() => {
    if (!requestedAction) return;
    const requested = actions.find((action) => action.key === requestedAction);
    if (requested) setSelected(requested);
    onActionOpened?.();
  }, [requestedAction, onActionOpened]);

  function closeForm() {
    setSelected(null);
    setAmount('');
    setDescription('');
    setRecipient('');
    setActionError('');
  }

  async function submit(event) {
    event.preventDefault();
    if (!amount || Number(amount) <= 0) return;
    setSubmitting(true);
    setActionError('');
    try {
      const recipientDescription = recipient ? `${selected.label} to ${recipient}` : selected.description;
      await onCreate({ type: selected.type, amount: Number(amount), description: description || recipientDescription });
      closeForm();
    } catch (error) {
      setActionError(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="quick-card" aria-labelledby="quick-title">
      <h2 id="quick-title">Quick actions</h2>
      <div className="quick-grid">
        {actions.map((action) => (
          <button type="button" className="quick-action" key={action.key} onClick={() => setSelected(action)}>
            <ActionIcon action={action} />
            <strong>{action.label}</strong>
            <small>{action.description}</small>
          </button>
        ))}
      </div>
      <button type="button" className="all-actions-button" onClick={onViewHistory}>View transaction history</button>



      {selected && (
        <div className="action-dialog-backdrop" onMouseDown={(event) => event.target === event.currentTarget && closeForm()}>
          <form className="action-dialog" onSubmit={submit}>
            <button type="button" className="dialog-close" onClick={closeForm} aria-label="Close">×</button>
            <ActionIcon action={selected} />
            <h3>{selected.label} money</h3>
            <p>{selected.type === 'DEBIT' ? 'Create an outgoing transaction.' : 'Create an incoming transaction.'}</p>
            {(selected.key === 'send' || selected.key === 'pix') && (
              <label>{selected.key === 'pix' ? 'PIX key' : 'Recipient'}<input autoFocus value={recipient} onChange={(e) => setRecipient(e.target.value)} placeholder={selected.key === 'pix' ? 'Email, phone or random key' : 'Recipient name'} required /></label>
            )}
            <label>Amount (R$)<input autoFocus={selected.key !== 'send' && selected.key !== 'pix'} type="number" min="0.01" step="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} required /></label>
            <label>Description<input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Optional description" /></label>
            {actionError && <p className="dialog-error" role="alert">{actionError}</p>}
            <button className="dialog-submit" type="submit" disabled={submitting}>{submitting ? 'Processing...' : `Confirm ${selected.label}`}</button>
          </form>
        </div>
      )}
    </section>
  );
}

export default QuickActions;
