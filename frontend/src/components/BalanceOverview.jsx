function BalanceOverview() {
  return (
    <section className="balance-card" aria-labelledby="balance-title">
      <div className="balance-header">
        <h2 id="balance-title">Balance overview</h2>
        <button type="button">This month <span>⌄</span></button>
      </div>

      <div className="chart-wrap">
        <div className="chart-y-axis"><span>R$ 12k</span><span>R$ 9k</span><span>R$ 6k</span><span>R$ 3k</span><span>R$ 0</span></div>
        <svg className="balance-chart" viewBox="0 0 560 235" preserveAspectRatio="none" aria-label="Balance trend chart">
          <defs>
            <linearGradient id="chartFill" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0" stopColor="#14d9ae" stopOpacity=".3" />
              <stop offset="1" stopColor="#14d9ae" stopOpacity="0" />
            </linearGradient>
          </defs>
          <g className="chart-grid">
            <path d="M0 18H560M0 69H560M0 120H560M0 171H560M0 222H560" />
            <path d="M0 0V222M112 0V222M224 0V222M336 0V222M448 0V222M560 0V222" />
          </g>
          <path className="chart-area" d="M0 171C25 153 30 137 60 143S92 138 112 111 151 121 168 83 210 76 224 62 259 55 280 72 311 118 336 103 373 96 392 88 420 74 448 76 476 68 512 81 532 45 560 32V222H0Z" />
          <path className="chart-line" d="M0 171C25 153 30 137 60 143S92 138 112 111 151 121 168 83 210 76 224 62 259 55 280 72 311 118 336 103 373 96 392 88 420 74 448 76 476 68 512 81 532 45 560 32" />
          <circle className="chart-point-glow" cx="420" cy="74" r="10" />
          <circle className="chart-point" cx="420" cy="74" r="5" />
        </svg>
        <div className="chart-tooltip"><small>23 Nov</small><strong><i /> R$ 8.450,00</strong></div>
        <div className="chart-x-axis"><span>1 Nov</span><span>6 Nov</span><span>11 Nov</span><span>16 Nov</span><span>21 Nov</span><span>26 Nov</span><span>30 Nov</span></div>
      </div>

      <div className="balance-summary">
        <div><span><i className="income" />Income</span><strong>R$ 2.300,00</strong></div>
        <div><span><i className="expense" />Expenses</span><strong>R$ 1.140,00</strong></div>
        <div><span><i className="net" />Net Flow</span><strong>R$ 1.160,00</strong></div>
      </div>
    </section>
  );
}

export default BalanceOverview;
