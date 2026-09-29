import { useCallback, useEffect, useMemo, useState } from 'react';
import BalanceOverview from './components/BalanceOverview';
import BalanceSummary from './components/BalanceSummary';
import QuickActions from './components/QuickActions';
import RecentActivity from './components/RecentActivity';
import Sidebar from './components/Sidebar';
import TechWaveBackground from './components/TechWaveBackground';
import MicroserviceStatus from './components/MicroserviceStatus';
import { requestJson, TRANSACTION_API_BASE, WALLET_API_BASE } from './api';

let walletInitialization;
const currency = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

function initializeWallet() {
  if (walletInitialization) return walletInitialization;
  walletInitialization = (async () => {
    const users = await requestJson(`${WALLET_API_BASE}/users`);
    const demoUser = users.find((candidate) => candidate.email?.toLowerCase() === 'fer.dev@email.com')
      || users.find((candidate) => candidate.name?.toLowerCase().includes('fernando'));
    const user = demoUser || await requestJson(`${WALLET_API_BASE}/users`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: 'Fernando Dev', email: 'fer.dev@email.com' })
    });
    let wallets = await requestJson(`${WALLET_API_BASE}/wallets/user/${user.id}`);
    if (!wallets.length) {
      const wallet = await requestJson(`${WALLET_API_BASE}/wallets`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: user.id, currency: 'BRL' })
      });
      wallets = [wallet];
    }
    return { user, wallets };
  })().catch((error) => { walletInitialization = null; throw error; });
  return walletInitialization;
}

function initials(name = '') {
  return name.split(/\s+/).filter(Boolean).slice(0, 2).map((part) => part[0]).join('').toUpperCase() || 'U';
}

function Dialog({ title, children, onClose, actions }) {
  return (
    <div className="action-dialog-backdrop" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <section className="action-dialog app-dialog" role="dialog" aria-modal="true" aria-labelledby="app-dialog-title">
        <button type="button" className="dialog-close" onClick={onClose} aria-label="Close">×</button>
        <h3 id="app-dialog-title">{title}</h3>
        <div className="app-dialog-content">{children}</div>
        {actions && <div className="dialog-actions">{actions}</div>}
      </section>
    </div>
  );
}

function HeaderPanel({ eyebrow, title, subtitle, children, footer, onClose }) {
  return (
    <div className="header-panel-backdrop" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <aside className="header-panel" role="dialog" aria-modal="true" aria-labelledby="header-panel-title">
        <div className="header-panel-glow" aria-hidden="true" />
        <header className="header-panel-heading">
          <div><small>{eyebrow}</small><h2 id="header-panel-title">{title}</h2><p>{subtitle}</p></div>
          <button type="button" onClick={onClose} aria-label="Close panel">×</button>
        </header>
        <div className="header-panel-body">{children}</div>
        {footer && <footer className="header-panel-footer">{footer}</footer>}
      </aside>
    </div>
  );
}

function formatNotificationDate(value) {
  if (!value) return 'Just now';
  return new Date(value).toLocaleString('en-GB', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' });
}

function App() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [user, setUser] = useState(null);
  const [wallets, setWallets] = useState([]);
  const [walletId, setWalletId] = useState(null);
  const [balance, setBalance] = useState(0);
  const [activeSection, setActiveSection] = useState('Dashboard');
  const [dialog, setDialog] = useState(null);
  const [requestedAction, setRequestedAction] = useState(null);
  const [seenTransactions, setSeenTransactions] = useState(new Set());
  const [toast, setToast] = useState('');

  const unreadTransactions = useMemo(
    () => transactions.filter((transaction) => !seenTransactions.has(transaction.id)),
    [transactions, seenTransactions]
  );

  async function fetchTransactions(id = walletId) {
    if (!id) return;
    try {
      setError('');
      const [transactionData, balanceData] = await Promise.all([
        requestJson(`${TRANSACTION_API_BASE}/transactions/wallet/${id}?limit=50`),
        requestJson(`${TRANSACTION_API_BASE}/transactions/wallet/${id}/balance`)
      ]);
      setTransactions(transactionData);
      setBalance(Number(balanceData.balance) || 0);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    let active = true;
    initializeWallet().then((state) => {
      if (!active) return;
      setUser(state.user);
      setWallets(state.wallets);
      setWalletId(state.wallets[0].id);
      return fetchTransactions(state.wallets[0].id);
    }).catch((err) => {
      if (active) { setError(err.message); setLoading(false); }
    });
    return () => { active = false; };
  }, []);

  useEffect(() => {
    if (!toast) return undefined;
    const timeout = window.setTimeout(() => setToast(''), 3500);
    return () => window.clearTimeout(timeout);
  }, [toast]);

  useEffect(() => {
    if (!dialog) return undefined;
    const closeOnEscape = (event) => event.key === 'Escape' && setDialog(null);
    window.addEventListener('keydown', closeOnEscape);
    return () => window.removeEventListener('keydown', closeOnEscape);
  }, [dialog]);

  async function addTransaction(transaction) {
    if (!walletId) throw new Error('A carteira ainda está sendo preparada');
    await requestJson(`${TRANSACTION_API_BASE}/transactions`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...transaction, walletId })
    });
    await fetchTransactions(walletId);
    setToast('Transaction completed successfully.');
  }

  function requestDelete(transaction) {
    setDialog({ type: 'delete', transaction });
  }

  async function confirmDelete() {
    const transaction = dialog?.transaction;
    if (!transaction) return;
    try {
      await requestJson(`${TRANSACTION_API_BASE}/transactions/${transaction.id}`, { method: 'DELETE' });
      setDialog(null);
      await fetchTransactions(walletId);
      setToast('Transaction deleted and balance recalculated.');
    } catch (deleteError) {
      setError(deleteError.message);
      setDialog(null);
    }
  }

  async function changeWallet(event) {
    const selectedId = Number(event.target.value);
    setWalletId(selectedId);
    setLoading(true);
    setSeenTransactions(new Set());
    await fetchTransactions(selectedId);
  }

  async function saveProfile(event) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    try {
      const updated = await requestJson(`${WALLET_API_BASE}/users/${user.id}`, {
        method: 'PUT', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: form.get('name'), email: form.get('email') })
      });
      setUser(updated);
      setDialog(null);
      setToast('Profile updated.');
    } catch (profileError) {
      setError(profileError.message);
    }
  }

  function scrollTo(id, section) {
    setActiveSection(section);
    requestAnimationFrame(() => document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' }));
  }

  function navigate(section) {
    if (section === 'Recipients') {
      setActiveSection(section);
      setRequestedAction('send');
      return;
    }
    if (section === 'Settings') {
      setActiveSection(section);
      setDialog({ type: 'profile' });
      return;
    }
    const targets = { Dashboard: 'dashboard', Wallet: 'wallet', Transactions: 'transactions', Analytics: 'analytics' };
    scrollTo(targets[section], section);
  }

  const actionOpened = useCallback(() => setRequestedAction(null), []);
  const openHistory = useCallback(() => scrollTo('transactions', 'Transactions'), []);

  function resetSession() {
    setActiveSection('Dashboard');
    setSeenTransactions(new Set(transactions.map((transaction) => transaction.id)));
    setDialog(null);
    scrollTo('dashboard', 'Dashboard');
    fetchTransactions(walletId);
    setToast('Session refreshed from the services.');
  }

  return (
    <div className="app-shell">
      <TechWaveBackground />
      <Sidebar activeSection={activeSection} user={user} onNavigate={navigate} onHelp={() => setDialog({ type: 'help' })} onLogout={() => setDialog({ type: 'logout' })} />

      <div className="app-container" id="dashboard">
        <header className="wallet-header">
          <div className="wallet-header-actions">
            <MicroserviceStatus baseUrl={TRANSACTION_API_BASE.startsWith('/') ? TRANSACTION_API_BASE : TRANSACTION_API_BASE.replace(/\/api$/, '')} />
            <button className={`notification-button${dialog?.type === 'notifications' ? ' active' : ''}`} type="button" aria-label={`${unreadTransactions.length} unread transaction notifications`} onClick={() => setDialog({ type: 'notifications' })}>
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9ZM10 21h4" /></svg>
              {unreadTransactions.length > 0 && <span className="notification-dot" />}
            </button>
            <button className={`header-avatar${dialog?.type === 'profile' ? ' active' : ''}`} type="button" aria-label="Edit user profile" onClick={() => setDialog({ type: 'profile' })}>{initials(user?.name)}</button>
          </div>

          <div className="wallet-heading-row" id="wallet">
            <div><h1>Safe Wallet</h1><p>Manage your assets and transactions.</p></div>
            <label className="wallet-selector">
              <svg className="wallet-selector-icon" viewBox="0 0 24 24" aria-hidden="true"><path d="M4 6.5h14a2 2 0 0 1 2 2v10H4a2 2 0 0 1-2-2v-12a2 2 0 0 1 2-2h13" /><path d="M16 11h6v5h-6a2.5 2.5 0 0 1 0-5Z" /></svg>
              <span className="sr-only">Select wallet</span>
              <select value={walletId || ''} onChange={changeWallet} disabled={!wallets.length}>
                {wallets.map((wallet, index) => <option key={wallet.id} value={wallet.id}>{index === 0 ? 'Main Wallet' : `Wallet ${index + 1}`} · {wallet.currency}</option>)}
              </select>
              <svg className="wallet-chevron" viewBox="0 0 24 24" aria-hidden="true"><path d="m7 9.5 5 5 5-5" /></svg>
            </label>
          </div>
        </header>

        <BalanceSummary balance={balance} transactions={transactions} />
        <div className="wallet-overview-grid">
          <BalanceOverview transactions={transactions} balance={balance} />
          <QuickActions onCreate={addTransaction} requestedAction={requestedAction} onActionOpened={actionOpened} onViewHistory={openHistory} />
        </div>
        <RecentActivity transactions={transactions} loading={loading} error={error} onDelete={requestDelete} />
      </div>

      {dialog?.type === 'notifications' && (
        <HeaderPanel
          eyebrow="Activity center"
          title="Notifications"
          subtitle={`${unreadTransactions.length} unread · ${transactions.length} recent transactions`}
          onClose={() => setDialog(null)}
          footer={<><button className="panel-text-button" type="button" onClick={() => { setDialog(null); scrollTo('transactions', 'Transactions'); }}>View full activity</button><button className="panel-primary-button" type="button" disabled={!unreadTransactions.length} onClick={() => setSeenTransactions(new Set(transactions.map((item) => item.id)))}>Mark all as read</button></>}
        >
          {transactions.length === 0 ? <div className="panel-empty"><span>✓</span><strong>You're all caught up</strong><p>New wallet activity will appear here.</p></div> : (
            <div className="notification-list">{transactions.slice(0, 6).map((transaction) => {
              const credit = transaction.type === 'CREDIT';
              const unread = !seenTransactions.has(transaction.id);
              return <button className={unread ? 'unread' : ''} type="button" key={transaction.id} onClick={() => { setSeenTransactions((current) => new Set(current).add(transaction.id)); setDialog(null); scrollTo('transactions', 'Transactions'); }}>
                <span className={`notification-type ${credit ? 'credit' : 'debit'}`} aria-hidden="true">{credit ? '↓' : '↗'}</span>
                <span className="notification-copy"><strong>{credit ? 'Money received' : 'Payment completed'}</strong><span>{transaction.description || 'Wallet transaction'}</span><time>{formatNotificationDate(transaction.createdAt)}</time></span>
                <span className={`notification-value ${credit ? 'credit' : 'debit'}`}>{credit ? '+' : '-'}{currency.format(Number(transaction.amount) || 0)}</span>
                {unread && <i className="notification-unread" aria-label="Unread" />}
              </button>;
            })}</div>
          )}
        </HeaderPanel>
      )}

      {dialog?.type === 'profile' && user && (
        <HeaderPanel eyebrow="Personal account" title="Profile" subtitle="Manage the identity shown in your wallet." onClose={() => setDialog(null)}>
          <div className="profile-hero">
            <span className="profile-hero-avatar">{initials(user.name)}</span>
            <div><strong>{user.name}</strong><span>{user.email}</span><small><i /> Active account</small></div>
          </div>
          <form className="profile-form" onSubmit={saveProfile}>
            <div className="profile-form-heading"><strong>Personal information</strong><span>Synced with Wallet Service</span></div>
            <label><span>Full name</span><div className="profile-input"><svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="8" r="3.5"/><path d="M5 21v-2a7 7 0 0 1 14 0v2"/></svg><input name="name" defaultValue={user.name} maxLength="120" required /></div></label>
            <label><span>Email address</span><div className="profile-input"><svg viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="m4 7 8 6 8-6"/></svg><input name="email" type="email" defaultValue={user.email} maxLength="180" required /></div></label>
            <div className="profile-meta"><span>Wallets connected</span><strong>{wallets.length}</strong></div>
            <button className="panel-primary-button profile-save" type="submit">Save changes</button>
          </form>
        </HeaderPanel>
      )}

      {dialog?.type === 'help' && (
        <Dialog title="Help & support" onClose={() => setDialog(null)}>
          <p>Use Quick actions to create credits and debits. The transaction list supports search, filters and CSV export.</p>
          <dl className="help-links"><div><dt>Transaction API</dt><dd>Port 8081</dd></div><div><dt>Grafana monitoring</dt><dd><a href="http://localhost:3001" target="_blank" rel="noreferrer">Open dashboard ↗</a></dd></div><div><dt>RabbitMQ management</dt><dd><a href="http://localhost:15672" target="_blank" rel="noreferrer">Open queues ↗</a></dd></div></dl>
        </Dialog>
      )}

      {dialog?.type === 'delete' && (
        <Dialog title="Delete transaction?" onClose={() => setDialog(null)} actions={<><button className="dialog-secondary" type="button" onClick={() => setDialog(null)}>Cancel</button><button className="dialog-danger" type="button" onClick={confirmDelete}>Delete and recalculate</button></>}>
          <p>This removes “{dialog.transaction.description || 'Transaction'}” and reverses its effect on the wallet balance.</p>
        </Dialog>
      )}

      {dialog?.type === 'logout' && (
        <Dialog title="Refresh this demo session?" onClose={() => setDialog(null)} actions={<><button className="dialog-secondary" type="button" onClick={() => setDialog(null)}>Cancel</button><button className="dialog-submit compact" type="button" onClick={resetSession}>Refresh session</button></>}>
          <p>Authentication is outside this delivery scope. This action reloads the current wallet from the microservices without pretending to end an authenticated session.</p>
        </Dialog>
      )}

      {toast && <div className="app-toast" role="status">{toast}</div>}
    </div>
  );
}

export default App;
