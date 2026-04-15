const staffScript = document.createElement('script');
staffScript.src = 'data.js';
staffScript.onload = initStaff;
document.head.appendChild(staffScript);

function initStaff() {
  const staffOrders = document.getElementById('staffOrders');
  const staffOrderFilters = document.getElementById('staffOrderFilters');
  const serviceRequests = document.getElementById('serviceRequests');
  const refreshStaffBtn = document.getElementById('refreshStaffBtn');
  const clearCompletedRequestsBtn = document.getElementById('clearCompletedRequestsBtn');
  const connectionBanner = document.getElementById('connectionBanner');
  const connectionBannerText = document.getElementById('connectionBannerText');
  const connectionRetryBtn = document.getElementById('connectionRetryBtn');
  const connectionDismissBtn = document.getElementById('connectionDismissBtn');
  let dismissedBannerState = '';
  let currentBannerState = '';
  let featureFlags = loadFeatureFlags();
  let activeOrderFilter = 'all';

  function matchesOrderFilter(order, filterKey) {
    if (filterKey === 'all') return true;
    if (filterKey === 'pending') return ['received', 'preparing'].includes(order.status);
    if (filterKey === 'accepted') return order.status === 'accepted';
    if (filterKey === 'served') return order.status === 'served';
    return true;
  }

  function renderStaffOrderFilters(orders) {
    if (!staffOrderFilters) return;
    const filterDefs = [
      { key: 'all', label: `All (${orders.length})` },
      { key: 'pending', label: `Pending (${orders.filter((order) => matchesOrderFilter(order, 'pending')).length})` },
      { key: 'accepted', label: `Accepted (${orders.filter((order) => matchesOrderFilter(order, 'accepted')).length})` },
      { key: 'served', label: `Served (${orders.filter((order) => matchesOrderFilter(order, 'served')).length})` }
    ];

    staffOrderFilters.innerHTML = '';
    filterDefs.forEach((filterDef) => {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = `tab ${activeOrderFilter === filterDef.key ? 'active' : ''}`;
      btn.textContent = filterDef.label;
      btn.onclick = () => {
        activeOrderFilter = filterDef.key;
        void render();
      };
      staffOrderFilters.appendChild(btn);
    });
  }

  async function handleConnectionRetry() {
    if (!connectionRetryBtn) return;
    connectionRetryBtn.disabled = true;
    await refreshApiHealth();
    connectionRetryBtn.disabled = false;
  }

  if (connectionRetryBtn) {
    connectionRetryBtn.onclick = () => { void handleConnectionRetry(); };
  }

  if (connectionDismissBtn) {
    connectionDismissBtn.onclick = () => {
      dismissedBannerState = currentBannerState;
      if (connectionBanner) connectionBanner.classList.add('hidden');
    };
  }

  function getLastCheckedSuffix(status) {
    if (!status.lastCheckedAt) return '';
    const parsed = new Date(status.lastCheckedAt);
    if (Number.isNaN(parsed.getTime())) return '';
    return ` Last checked ${parsed.toLocaleTimeString()}.`;
  }

  function renderConnectionBanner(status) {
    if (!connectionBanner || !connectionBannerText || !connectionRetryBtn) return;

    if (!status.online) {
      currentBannerState = 'offline';
      if (dismissedBannerState === currentBannerState) {
        connectionBanner.className = 'connection-banner hidden';
        return;
      }

      connectionBanner.className = 'connection-banner connection-offline';
      connectionBannerText.textContent = `Offline mode: showing locally cached updates.${getLastCheckedSuffix(status)}`;
      connectionRetryBtn.classList.remove('hidden');
      return;
    }

    if (status.apiReachable === false) {
      currentBannerState = 'api-down';
      if (dismissedBannerState === currentBannerState) {
        connectionBanner.className = 'connection-banner hidden';
        return;
      }

      connectionBanner.className = 'connection-banner connection-api-down';
      connectionBannerText.textContent = `API unavailable: staff changes are stored locally for now.${getLastCheckedSuffix(status)}`;
      connectionRetryBtn.classList.remove('hidden');
      return;
    }

    if (status.apiReachable === true) {
      currentBannerState = 'online';
      if (dismissedBannerState === currentBannerState) {
        connectionBanner.className = 'connection-banner hidden';
        return;
      }

      connectionBanner.className = 'connection-banner connection-online';
      connectionBannerText.textContent = `Connected: staff dashboard is synced with backend.${getLastCheckedSuffix(status)}`;
      connectionRetryBtn.classList.add('hidden');
      return;
    }

    currentBannerState = 'unknown';
    connectionBanner.className = 'connection-banner hidden';
    connectionBannerText.textContent = '';
    connectionRetryBtn.classList.add('hidden');
  }

  subscribeConnectionStatus(renderConnectionBanner);
  void refreshApiHealth();
  window.setInterval(() => { void refreshApiHealth(); }, 30000);

  async function render() {
    featureFlags = await hydrateFeatureFlagsFromApi();
    await hydrateOrdersFromApi();
    await hydrateRequestsFromApi();
    const orders = loadOrders();
    const requests = loadRequests();
    renderStaffOrderFilters(orders);
    const filteredOrders = orders.filter((order) => matchesOrderFilter(order, activeOrderFilter));

    staffOrders.innerHTML = '';
    if (!filteredOrders.length) {
      staffOrders.innerHTML = '<p class="muted">No orders yet.</p>';
    } else {
      filteredOrders.forEach((order) => {
        const acceptanceEnabled = Boolean(featureFlags.staffAcceptanceEtaEnabled);
        const row = document.createElement('div');
        row.className = 'order-card';
        row.innerHTML = `
          <div class="section-head">
            <div>
              <strong>${order.table}</strong>
              <div class="muted small">${order.createdAt}</div>
            </div>
            <span class="status ${order.status}">${order.status}</span>
          </div>
          <div class="muted small">Payment: <span class="status ${order.paymentStatus || 'unpaid'}">${order.paymentStatus || 'unpaid'}</span></div>
          ${order.status === 'accepted' && order.servingEtaMinutes ? `<div class="muted small">Serving ETA: ${order.servingEtaMinutes} min</div>` : ''}
          <div>${order.items.map((x) => `<div class="line-item">${x.qty} × ${x.name}</div>`).join('')}</div>
          ${order.customerPhoneMasked ? `<div class="muted small">Verified phone: ${order.customerPhoneMasked}</div>` : ''}
          ${order.customerEmail ? `<div class="muted small">Email: ${order.customerEmail}</div>` : ''}
          ${order.note ? `<div class="note">Note: ${order.note}</div>` : ''}
          <div class="actions-row">
            ${acceptanceEnabled ? `<input type="number" min="1" max="180" step="1" data-eta-id="${order.id}" placeholder="ETA (min)" style="max-width: 120px;" />` : ''}
            ${acceptanceEnabled ? `<button data-accept-id="${order.id}">Accept</button>` : ''}
            <button data-status="received" data-id="${order.id}">Received</button>
            <button data-status="preparing" data-id="${order.id}">Preparing</button>
            <button data-status="served" data-id="${order.id}">Served</button>
            <button data-payment="paid" data-id="${order.id}" ${order.paymentStatus === 'paid' ? 'disabled' : ''}>Mark Paid</button>
          </div>
        `;
        staffOrders.appendChild(row);
      });

      staffOrders.querySelectorAll('[data-accept-id]').forEach((btn) => {
        btn.onclick = async () => {
          const orderId = btn.dataset.acceptId;
          const etaInput = staffOrders.querySelector(`[data-eta-id="${orderId}"]`);
          const etaMinutes = Number(etaInput?.value || 0);
          if (!Number.isFinite(etaMinutes) || etaMinutes <= 0) {
            alert('Enter serving ETA in minutes before accepting the order.');
            return;
          }
          await updateOrderStatusWithFallback(orderId, 'accepted', {
            servingEtaMinutes: etaMinutes,
            acceptedBy: 'Staff',
            acceptedAt: new Date().toISOString()
          });
          await render();
        };
      });

      staffOrders.querySelectorAll('[data-status]').forEach((btn) => {
        btn.onclick = async () => {
          await updateOrderStatusWithFallback(btn.dataset.id, btn.dataset.status);
          await render();
        };
      });

      staffOrders.querySelectorAll('[data-payment]').forEach((btn) => {
        btn.onclick = async () => {
          await updateOrderPaymentWithFallback(btn.dataset.id, btn.dataset.payment);
          await render();
        };
      });
    }

    serviceRequests.innerHTML = '';
    if (!requests.length) {
      serviceRequests.innerHTML = '<p class="muted">No service requests.</p>';
    } else {
      requests.forEach((request) => {
        const row = document.createElement('div');
        row.className = 'request-row';
        row.innerHTML = `
          <div class="section-head">
            <div>
              <strong>${request.type}</strong>
              <div class="muted small">${request.table} · ${request.createdAt}</div>
            </div>
            <span class="status ${request.status}">${request.status}</span>
          </div>
          <p>${request.note}</p>
          ${request.customerPhoneMasked ? `<div class="muted small">Verified phone: ${request.customerPhoneMasked}</div>` : ''}
          <div class="actions-row">
            <button data-request-status="pending" data-id="${request.id}">Pending</button>
            <button data-request-status="completed" data-id="${request.id}">Completed</button>
          </div>
        `;
        serviceRequests.appendChild(row);
      });

      serviceRequests.querySelectorAll('[data-request-status]').forEach((btn) => {
        btn.onclick = async () => {
          await updateRequestStatusWithFallback(btn.dataset.id, btn.dataset.requestStatus);
          await render();
        };
      });
    }
  }

  refreshStaffBtn.onclick = () => { void render(); };
  clearCompletedRequestsBtn.onclick = async () => {
    await clearCompletedRequestsWithFallback();
    await render();
  };

  void render();
}
