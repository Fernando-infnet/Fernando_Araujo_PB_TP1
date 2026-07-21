const navigation = [
  { label: 'Dashboard', icon: 'home' },
  { label: 'Wallet', icon: 'wallet', active: true },
  { label: 'Transactions', icon: 'arrows' },
  { label: 'Recipients', icon: 'users' },
  { label: 'Analytics', icon: 'chart' },
  { label: 'Settings', icon: 'settings' }
];

function Icon({ name }) {
  const paths = {
    home: <><path d="M3 10.5 12 3l9 7.5"/><path d="M5 9.5V21h14V9.5M9 21v-7h6v7"/></>,
    wallet: <><path d="M4 6.5h14a2 2 0 0 1 2 2v10H4a2 2 0 0 1-2-2v-12a2 2 0 0 1 2-2h13"/><path d="M16 11h6v5h-6a2.5 2.5 0 0 1 0-5Z"/></>,
    arrows: <><path d="m7 7 3-3 3 3M10 4v13"/><path d="m17 17-3 3-3-3M14 20V7"/></>,
    users: <><circle cx="9" cy="8" r="3"/><path d="M3 20v-2a5 5 0 0 1 5-5h2a5 5 0 0 1 5 5v2M16 4.5a3 3 0 0 1 0 7M18 14a5 5 0 0 1 3 4.6V20"/></>,
    chart: <><path d="M4 20V10M10 20V4M16 20v-7M22 20V7"/></>,
    settings: <><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.7 1.7 0 0 0 .34 1.88l.06.06-2.83 2.83-.06-.06A1.7 1.7 0 0 0 15 19.4a1.7 1.7 0 0 0-1 .6 1.7 1.7 0 0 0-.4 1.1V21h-4v-.09a1.7 1.7 0 0 0-1.1-1.55 1.7 1.7 0 0 0-1.88.34l-.06.06-2.83-2.83.06-.06A1.7 1.7 0 0 0 4.1 15a1.7 1.7 0 0 0-1.55-1H2.5v-4h.09A1.7 1.7 0 0 0 4.14 9a1.7 1.7 0 0 0-.34-1.88l-.06-.06 2.83-2.83.06.06A1.7 1.7 0 0 0 8.5 4.6 1.7 1.7 0 0 0 9.5 3V3h4v.09a1.7 1.7 0 0 0 1.1 1.55 1.7 1.7 0 0 0 1.88-.34l.06-.06 2.83 2.83-.06.06A1.7 1.7 0 0 0 19 9c.16.6.7 1 1.32 1H21v4h-.09A1.7 1.7 0 0 0 19.4 15Z"/></>
  };

  return (
    <svg className="nav-icon" viewBox="0 0 24 24" aria-hidden="true">
      {paths[name]}
    </svg>
  );
}

function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="logo-slot" aria-label="Logo placeholder" />

      <nav className="sidebar-nav" aria-label="Main navigation">
        {navigation.map((item) => (
          <button
            className={`nav-item${item.active ? ' active' : ''}`}
            type="button"
            key={item.label}
            aria-current={item.active ? 'page' : undefined}
            disabled={!item.active}
          >
            <Icon name={item.icon} />
            <span>{item.label}</span>
          </button>
        ))}
      </nav>

      <div className="sidebar-footer">
        <button className="support-button" type="button">
          <span className="help-icon">?</span>
          Help &amp; Support
        </button>

        <div className="profile-card">
          <span className="avatar">FD</span>
          <span className="profile-copy">
            <strong>Fernando Dev</strong>
            <small>fer.dev@email.com</small>
          </span>
        </div>

        <button className="logout-button" type="button">
          <span aria-hidden="true">↪</span>
          Log out
        </button>
      </div>
    </aside>
  );
}

export default Sidebar;
