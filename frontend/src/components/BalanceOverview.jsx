import { useMemo, useState } from 'react';
import { buildBalanceSeries, chartGeometry, PERIODS, transactionTotals, transactionsForPeriod } from '../dashboard-utils';

const currency = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

function BalanceOverview({ transactions = [], balance = 0 }) {
  const [period, setPeriod] = useState('30');
  const filtered = useMemo(() => transactionsForPeriod(transactions, period), [transactions, period]);
  const totals = useMemo(() => transactionTotals(filtered), [filtered]);
  const series = useMemo(() => buildBalanceSeries(transactions, balance, period), [transactions, balance, period]);
  const geometry = useMemo(() => chartGeometry(series), [series]);
  const labelIndexes = [0, Math.floor((series.length - 1) / 2), series.length - 1];
  const labels = labelIndexes.map((index) => series[index]).filter(Boolean);
  const latest = series.at(-1);
  const lastPoint = geometry.points.split(' ').at(-1)?.split(',');

  return (
    <section className="balance-card" id="analytics" aria-labelledby="balance-title">
      <div className="balance-header">
        <h2 id="balance-title">Balance overview</h2>
        <label className="period-select">
          <span className="sr-only">Chart period</span>
          <select value={period} onChange={(event) => setPeriod(event.target.value)}>
            {Object.entries(PERIODS).map(([value, option]) => <option key={value} value={value}>{option.label}</option>)}
          </select>
        </label>
      </div>

      <div className="chart-wrap">
        <div className="chart-y-axis">
          <span>{currency.format(geometry.max)}</span>
          <span>{currency.format((geometry.max + geometry.min) / 2)}</span>
          <span>{currency.format(geometry.min)}</span>
        </div>
        <svg className="balance-chart" viewBox="0 0 560 235" preserveAspectRatio="none" aria-label="Balance trend chart">
          <defs>
            <linearGradient id="chartFill" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0" stopColor="#14d9ae" stopOpacity=".3" />
              <stop offset="1" stopColor="#14d9ae" stopOpacity="0" />
            </linearGradient>
          </defs>
          <g className="chart-grid"><path d="M0 18H560M0 120H560M0 222H560" /><path d="M0 0V222M280 0V222M560 0V222" /></g>
          <polygon className="chart-area" points={geometry.area} />
          <polyline className="chart-line" points={geometry.points} />
          {lastPoint && <circle className="chart-point" cx={lastPoint[0]} cy={lastPoint[1]} r="5" />}
        </svg>
        <div className="chart-tooltip"><small>{latest?.date || 'No data'}</small><strong><i /> {currency.format(latest?.balance || 0)}</strong></div>
        <div className="chart-x-axis">{labels.map((point, index) => <span key={`${point.date}-${index}`}>{new Date(`${point.date}T12:00:00`).toLocaleDateString('en-GB', { day: '2-digit', month: 'short' })}</span>)}</div>
      </div>

      <div className="balance-summary">
        <div><span><i className="income" />Income</span><strong>{currency.format(totals.income)}</strong></div>
        <div><span><i className="expense" />Expenses</span><strong>{currency.format(totals.expense)}</strong></div>
        <div><span><i className="net" />Net Flow</span><strong>{currency.format(totals.income - totals.expense)}</strong></div>
      </div>
    </section>
  );
}

export default BalanceOverview;
