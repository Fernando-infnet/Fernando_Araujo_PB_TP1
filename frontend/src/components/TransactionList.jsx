import React from 'react';

function TransactionList({ transactions = [], onDelete }) {
  if (!transactions.length) return <p>Nenhuma transação registrada.</p>;

  return (
    <ul className="transaction-list">
      {transactions.map((tx) => (
        <li key={tx.id} className="transaction-item">
          <div>
            <strong>{tx.type} — {tx.amount}</strong>
            <p className="desc">{tx.description}</p>
            <small>{tx.createdAt ? new Date(tx.createdAt).toLocaleString() : ''}</small>
          </div>
          <div className="actions">
            <button className="delete" onClick={() => onDelete(tx.id)}>Remover</button>
          </div>
        </li>
      ))}
    </ul>
  );
}

export default TransactionList;
