import { useEffect, useState } from 'react';
import BalanceOverview from './components/BalanceOverview';
import BalanceSummary from './components/BalanceSummary';
import QuickActions from './components/QuickActions';
import RecentActivity from './components/RecentActivity';
import Sidebar from './components/Sidebar';
import TechWaveBackground from './components/TechWaveBackground';
import MicroserviceStatus from './components/MicroserviceStatus';

const WALLET_API_BASE = import.meta.env.VITE_WALLET_API_URL || 'http://localhost:8080/api';
const TRANSACTION_API_BASE = import.meta.env.VITE_TRANSACTION_API_URL || 'http://localhost:8081/api';
let walletInitialization;

async function requestJson(url, options) {
  const response = await fetch(url, options);
  if (!response.ok) {
    const body = await response.json().catch(() => null);
    throw new Error(body?.message || 'Não foi possível conectar ao backend');
  }
  return response.status === 204 ? null : response.json();
}

function initializeWallet() {
  if (walletInitialization) return walletInitialization;
  walletInitialization = (async () => {
    const users = await requestJson(`${WALLET_API_BASE}/users`);
    const user = users[0] || await requestJson(`${WALLET_API_BASE}/users`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: 'Fernando Dev', email: 'fer.dev@email.com' })
    });
    const wallets = await requestJson(`${WALLET_API_BASE}/wallets/user/${user.id}`);
    return wallets[0] || requestJson(`${WALLET_API_BASE}/wallets`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: user.id, currency: 'BRL' })
    });
  })().catch((error) => { walletInitialization = null; throw error; });
  return walletInitialization;
}

function App() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [walletId, setWalletId] = useState(null);
  const [balance, setBalance] = useState(0);

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
    initializeWallet().then((wallet) => {
      if (!active) return;
      setWalletId(wallet.id);
      return fetchTransactions(wallet.id);
    }).catch((err) => {
      if (active) { setError(err.message); setLoading(false); }
    });
    return () => { active = false; };
  }, []);

  async function addTransaction(transaction) {
    if (!walletId) throw new Error('A carteira ainda está sendo preparada');
    await requestJson(`${TRANSACTION_API_BASE}/transactions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...transaction, walletId })
    });
    await fetchTransactions(walletId);
  }

  async function removeTransaction(id) {
    await requestJson(`${TRANSACTION_API_BASE}/transactions/${id}`, { method: 'DELETE' });
    await fetchTransactions(walletId);
  }

  return (
    <div className="app-shell">
      <TechWaveBackground />
      <Sidebar />

      <div className="app-container">
        <header className="wallet-header">
          <div className="wallet-header-actions">
            <MicroserviceStatus baseUrl={TRANSACTION_API_BASE.replace(/\/api$/, '')} />
            <button className="notification-button" type="button" aria-label="Notifications">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9ZM10 21h4" />
              </svg>
              <span className="notification-dot" />
            </button>
            <button className="header-avatar" type="button" aria-label="User profile">FD</button>
          </div>

          <div className="wallet-heading-row">
            <div>
              <h1>Wallet</h1>
              <p>Manage your assets and transactions.</p>
            </div>

            <button className="wallet-selector" type="button" aria-label="Select wallet">
              <svg className="wallet-selector-icon" viewBox="0 0 24 24" aria-hidden="true">
                <path d="M4 6.5h14a2 2 0 0 1 2 2v10H4a2 2 0 0 1-2-2v-12a2 2 0 0 1 2-2h13" />
                <path d="M16 11h6v5h-6a2.5 2.5 0 0 1 0-5Z" />
              </svg>
              <span>Main Wallet</span>
              <svg className="wallet-chevron" viewBox="0 0 24 24" aria-hidden="true">
                <path d="m7 9.5 5 5 5-5" />
              </svg>
            </button>
          </div>
        </header>

        <BalanceSummary balance={balance} transactions={transactions} />

        <div className="wallet-overview-grid">
          <BalanceOverview />
          <QuickActions onCreate={addTransaction} />
        </div>

        <RecentActivity
          transactions={transactions}
          loading={loading}
          error={error}
          onDelete={removeTransaction}
        />
      </div>
    </div>
  );
}

export default App;
