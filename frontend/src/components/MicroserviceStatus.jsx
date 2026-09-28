import { useEffect, useState } from 'react';

function MicroserviceStatus({ baseUrl }) {
  const [state, setState] = useState('checking');

  useEffect(() => {
    const controller = new AbortController();
    fetch(`${baseUrl}/actuator/health`, { signal: controller.signal })
      .then((response) => response.ok ? response.json() : Promise.reject())
      .then((health) => setState(health.status === 'UP' ? 'online' : 'offline'))
      .catch((error) => {
        if (error?.name !== 'AbortError') setState('offline');
      });
    return () => controller.abort();
  }, [baseUrl]);

  const labels = {
    checking: 'Transactions: checking',
    online: 'Transactions: online',
    offline: 'Transactions: offline'
  };

  return (
    <span className={`service-status ${state}`} role="status" aria-live="polite">
      <i aria-hidden="true" />
      {labels[state]}
    </span>
  );
}

export default MicroserviceStatus;
