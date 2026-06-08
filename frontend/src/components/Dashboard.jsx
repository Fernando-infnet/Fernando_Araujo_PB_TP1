import React from 'react';

const TransactionList = ({ transactions = [], onDelete }) => {
  if (!transactions.length) return <p>Nenhuma transação encontrada.</p>;

  return (
    <ul className="transaction-list">
      {transactions.map((tx) => (
        <li key={tx.id} className="transaction-item">
          <div>
            <strong>{tx.type}</strong>
            <p className="desc">{tx.description}</p>
            <span className="amount">{tx.amount}</span>
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