import { useState } from 'react';

const initial = { type: 'ENTRADA', amount: '', description: '' };

function CreateTransaction({ onCreate }) {
  const [form, setForm] = useState(initial);

  function updateField(e) {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  }

  function submit(e) {
    e.preventDefault();
    if (!form.amount) return;
    const payload = { ...form, amount: parseFloat(form.amount) };
    onCreate(payload);
    setForm(initial);
  }

  return <>
    <form className="transaction-form" onSubmit={submit}>
      <label>
        Tipo
        <select name="type" value={form.type} onChange={updateField}>
          <option value="ENTRADA">Entrada</option>
          <option value="SAIDA">Saída</option>
        </select>
      </label>

      <label>
        Valor
        <input name="amount" value={form.amount} onChange={updateField} />
      </label>

      <label>
        Descrição
        <input name="description" value={form.description} onChange={updateField} />
      </label>

      <button type="submit">Registrar</button>
    </form>
  </>;
}

export default CreateTransaction;
