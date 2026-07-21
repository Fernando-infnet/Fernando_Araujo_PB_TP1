const activities = [
  { id: 1, direction: 'in', title: 'Received from dNxGdZMVmznikD...', amount: '+R$ 100,00' },
  { id: 2, direction: 'out', title: 'Sent to dNxGdZMVmznikD...', amount: '-R$ 100,00' },
  { id: 3, direction: 'in', title: 'Received from dNxGdZMVmznikD...', amount: '+R$ 100,00' },
  { id: 4, direction: 'out', title: 'Sent to dNxGdZMVmznikD...', amount: '-R$ 100,00' },
  { id: 5, direction: 'in', title: 'Received from dNxGdZMVmznikD...', amount: '+R$ 100,00' }
];

function TransferIcon({ direction }) {
  return (
    <span className={`transfer-icon ${direction}`} aria-hidden="true">
      <svg viewBox="0 0 24 24">
        {direction === 'in' ? (
          <><path d="M12 4v14M7 13l5 5 5-5"/><path d="M6 21h12"/></>
        ) : (
          <><path d="m8 16 8-8M10 8h6v6"/></>
        )}
      </svg>
    </span>
  );
}

function RecentActivity() {
  return (
    <section className="activity-card" aria-labelledby="activity-title">
      <div className="activity-card-header">
        <h2 id="activity-title">Recent activity</h2>
        <button type="button" className="view-activity-button">
          View all activity
          <span aria-hidden="true">↗</span>
        </button>
      </div>

      <div className="activity-list">
        {activities.map((activity) => (
          <article className="activity-row" key={activity.id}>
            <TransferIcon direction={activity.direction} />

            <div className="activity-copy">
              <strong>{activity.title}</strong>
              <span>23 Nov 2026 <i /> 20:30</span>
            </div>

            <span className="activity-status"><i /> Completed</span>
            <strong className={`activity-amount ${activity.direction}`}>{activity.amount}</strong>
            <span className="activity-chevron" aria-hidden="true">›</span>
          </article>
        ))}
      </div>

      <button type="button" className="load-more-button">
        Load more entries
        <span aria-hidden="true">⌄</span>
      </button>
    </section>
  );
}

export default RecentActivity;
