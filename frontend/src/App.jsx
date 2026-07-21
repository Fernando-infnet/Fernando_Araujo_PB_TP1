import { useEffect, useState } from 'react';
import TransactionList from './components/TransactionList';
import Sidebar from './components/Sidebar';
import RecentActivity from './components/RecentActivity';
import BalanceOverview from './components/BalanceOverview';
import QuickActions from './components/QuickActions';

const API_URL = 'http://localhost:8080/api/transactions';

function App() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  async function fetchTransactions() {
    try {
      const response = await fetch(API_URL);
      if (!response.ok) {
        throw new Error('Falha ao carregar transações');
      }
      const data = await response.json();
      setTransactions(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchTransactions();
  }, []);

  async function addTransaction(transaction) {
    const response = await fetch(API_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(transaction)
    });
    if (response.ok) {
      fetchTransactions();
    }
  }

  async function removeTransaction(id) {
    const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
    if (response.ok) {
      setTransactions((prev) => prev.filter((t) => t.id !== id));
    }
  }

  return (
    <div className="app-shell">
      <Sidebar />

      <div className="app-container">
        <header className="wallet-header">
          <div className="wallet-header-actions">
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

        <div className="wallet-overview-grid">
          <BalanceOverview />
          <QuickActions onCreate={addTransaction} />
        </div>

        <RecentActivity />

        <main>
          <section className="panel transaction-panel">
            <h2>Lista de transações</h2>
            {loading && <p>Carregando...</p>}
            {error && <p className="error">{error}</p>}
            {!loading && !error && (
              <TransactionList transactions={transactions} onDelete={removeTransaction} />
            )}
          </section>
        </main>
      </div>
    </div>
  );
}

export default App;
