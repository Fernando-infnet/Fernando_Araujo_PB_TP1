import { useEffect, useState } from 'react';
import CreateTransaction from './components/CreateTransaction';
import TransactionList from './components/TransactionList';

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
    <div className="app-container">
      <header>
        <h1>Gestão de Transações</h1>
        <p>Spring Boot + React</p>
      </header>

      <main>
        <section className="panel">
          <h2>Registrar transação</h2>
          <CreateTransaction onCreate={addTransaction} />
        </section>

        <section className="panel">
          <h2>Lista de transações</h2>
          {loading && <p>Carregando...</p>}
          {error && <p className="error">{error}</p>}
          {!loading && !error && (
            <TransactionList transactions={transactions} onDelete={removeTransaction} />
          )}
        </section>
      </main>
    </div>
  );
}

export default App;
