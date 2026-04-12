const adminScript = document.createElement('script');
adminScript.src = 'data.js';
adminScript.onload = initAdmin;
document.head.appendChild(adminScript);

function initAdmin() {
  void bootstrapAdmin();
}

async function bootstrapAdmin() {
  await hydrateMenuFromApi();
  const adminLoginForm = document.getElementById('adminLoginForm');
  const adminEmailInput = document.getElementById('adminEmail');
  const adminPasswordInput = document.getElementById('adminPassword');
  const adminLogoutBtn = document.getElementById('adminLogoutBtn');
  const adminAuthStatus = document.getElementById('adminAuthStatus');
  const addStaffForm = document.getElementById('addStaffForm');
  const staffEmailInput = document.getElementById('staffEmail');
  const staffPasswordInput = document.getElementById('staffPassword');
  const staffUsersList = document.getElementById('staffUsersList');
  const refreshUsersBtn = document.getElementById('refreshUsersBtn');
  const menuForm = document.getElementById('menuForm');
  const imageUrlInput = document.getElementById('imageUrl');
  const menuImagePreview = document.getElementById('menuImagePreview');
  const adminMenuList = document.getElementById('adminMenuList');
  const resetMenuBtn = document.getElementById('resetMenuBtn');
  const connectionBanner = document.getElementById('connectionBanner');
  const connectionBannerText = document.getElementById('connectionBannerText');
  const connectionRetryBtn = document.getElementById('connectionRetryBtn');
  const connectionDismissBtn = document.getElementById('connectionDismissBtn');
  let dismissedBannerState = '';
  let currentBannerState = '';

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
      connectionBannerText.textContent = `Offline mode: admin changes are local until internet returns.${getLastCheckedSuffix(status)}`;
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
      connectionBannerText.textContent = `API unavailable: menu updates are running in fallback mode.${getLastCheckedSuffix(status)}`;
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
      connectionBannerText.textContent = `Connected: admin menu updates sync to backend.${getLastCheckedSuffix(status)}`;
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

  function updateAuthStatus() {
    const hasToken = Boolean(getAdminAccessToken());
    adminAuthStatus.textContent = hasToken
      ? 'Admin authenticated. You can create staff and manage access.'
      : 'Admin login required for secure actions.';
    adminAuthStatus.classList.toggle('verified', hasToken);
  }

  function getFoodType(item) {
    const text = `${item.name || ''} ${item.description || ''}`.toLowerCase();
    const nonVegPattern = /\b(chicken|mutton|fish|prawn|shrimp|egg|beef|pork|meat|nugget)\b/;
    return nonVegPattern.test(text) ? { label: 'Non-Veg', className: 'nonveg' } : { label: 'Veg', className: 'veg' };
  }

  async function renderUsers() {
    if (!staffUsersList) return;
    if (!getAdminAccessToken()) {
      staffUsersList.innerHTML = '<p class="muted">Login as admin to view and manage users.</p>';
      return;
    }

    try {
      const users = await fetchAdminUsers();
      if (!Array.isArray(users) || users.length === 0) {
        staffUsersList.innerHTML = '<p class="muted">No users available.</p>';
        return;
      }

      staffUsersList.innerHTML = '';
      users.forEach((user) => {
        const row = document.createElement('div');
        row.className = 'request-row';
        const isAdmin = user.role === 'ADMIN';
        const isStaff = user.role === 'STAFF';
        row.innerHTML = `
          <div class="section-head">
            <div>
              <strong>${user.email}</strong>
              <div class="muted small">Role: ${user.role}</div>
            </div>
            ${isAdmin ? '<span class="status served">Protected Admin</span>' : `<button data-user-id="${user.id}" data-next-staff="${!isStaff}">${isStaff ? 'Revoke Staff Access' : 'Grant Staff Access'}</button>`}
          </div>
        `;
        staffUsersList.appendChild(row);
      });

      staffUsersList.querySelectorAll('[data-user-id]').forEach((btn) => {
        btn.onclick = async () => {
          try {
            await updateStaffAccess(btn.dataset.userId, btn.dataset.nextStaff === 'true');
            await renderUsers();
          } catch (error) {
            alert(error.message || 'Unable to update staff access.');
          }
        };
      });
    } catch (error) {
      staffUsersList.innerHTML = `<p class="muted">${error.message || 'Unable to load users.'}</p>`;
    }
  }

  async function renderMenuList() {
    try {
      if (getAdminAccessToken()) {
        await fetchAdminMenu();
      }
    } catch (_error) {
      // Fall back to local data if admin menu fetch fails.
    }

    const menu = loadMenu();
    adminMenuList.innerHTML = '';
    menu.forEach((item) => {
      const row = document.createElement('div');
      row.className = 'request-row';
      const foodType = getFoodType(item);
      const availabilityText = item.available === false ? 'Disabled' : 'Enabled';
      const nextAvailable = item.available === false;
      const fallbackImage = `https://placehold.co/240x160/f6f3ee/8b5e3c?text=${encodeURIComponent(item.name || 'No Image')}`;
      const imageSrc = item.imageUrl || fallbackImage;
      row.innerHTML = `
        <div class="section-head">
          <div>
            <strong>${item.name}</strong>
            <div class="muted small">${item.category} · ${availabilityText} · <span class="food-label ${foodType.className}">${foodType.label}</span></div>
          </div>
          <div>${item.restricted ? 'Restricted' : currency(item.price)}</div>
        </div>
        <div class="menu-admin-media">
          <img class="menu-admin-thumb" src="${imageSrc}" alt="${item.name}" onerror="this.onerror=null;this.src='${fallbackImage}';" />
        </div>
        <p>${item.description || ''}</p>
        <div class="actions-row">
          <span class="status ${item.available === false ? 'cancelled' : 'served'}">${availabilityText}</span>
          <button data-availability-id="${item.id}" data-next-available="${nextAvailable}">${nextAvailable ? 'Enable' : 'Disable'}</button>
        </div>
      `;
      adminMenuList.appendChild(row);
    });

    adminMenuList.querySelectorAll('[data-availability-id]').forEach((btn) => {
      btn.onclick = async () => {
        try {
          await updateMenuAvailability(btn.dataset.availabilityId, btn.dataset.nextAvailable === 'true');
          await renderMenuList();
        } catch (error) {
          alert(error.message || 'Unable to update item visibility.');
        }
      };
    });
  }

  function renderMenuImagePreview() {
    if (!menuImagePreview || !imageUrlInput) return;
    const imageUrl = imageUrlInput.value.trim();
    if (!imageUrl) {
      menuImagePreview.innerHTML = '';
      menuImagePreview.classList.add('hidden');
      return;
    }
    menuImagePreview.innerHTML = `<img src="${imageUrl}" alt="Menu preview" class="menu-preview-image" onerror="this.onerror=null;this.src='https://placehold.co/600x400/f6f3ee/8b5e3c?text=Invalid+Image+URL';" />`;
    menuImagePreview.classList.remove('hidden');
  }

  menuForm.onsubmit = async (e) => {
    e.preventDefault();
    const item = {
      name: document.getElementById('name').value.trim(),
      category: document.getElementById('category').value,
      price: Number(document.getElementById('price').value || 0),
      description: document.getElementById('description').value.trim(),
      imageUrl: imageUrlInput?.value.trim() || '',
      restricted: document.getElementById('category').value === 'Cigarettes',
      available: true
    };
    try {
      await createMenuItemWithFallback(item);
      menuForm.reset();
      renderMenuImagePreview();
      await renderMenuList();
    } catch (error) {
      alert(error.message || 'Unable to save menu item.');
    }
  };

  if (imageUrlInput) {
    imageUrlInput.oninput = renderMenuImagePreview;
  }

  resetMenuBtn.onclick = () => {
    localStorage.removeItem(STORAGE_KEYS.menu);
    void renderMenuList();
  };

  if (adminLoginForm) {
    adminLoginForm.onsubmit = async (event) => {
      event.preventDefault();
      try {
        await adminLogin(adminEmailInput.value.trim(), adminPasswordInput.value);
        adminPasswordInput.value = '';
        updateAuthStatus();
        await renderUsers();
        await renderMenuList();
      } catch (error) {
        alert(error.message || 'Admin login failed.');
      }
    };
  }

  if (adminLogoutBtn) {
    adminLogoutBtn.onclick = async () => {
      clearAdminAccessToken();
      updateAuthStatus();
      await renderUsers();
      await renderMenuList();
    };
  }

  if (addStaffForm) {
    addStaffForm.onsubmit = async (event) => {
      event.preventDefault();
      try {
        await createStaffUser(staffEmailInput.value.trim(), staffPasswordInput.value);
        addStaffForm.reset();
        await renderUsers();
      } catch (error) {
        alert(error.message || 'Unable to create staff user.');
      }
    };
  }

  if (refreshUsersBtn) {
    refreshUsersBtn.onclick = () => { void renderUsers(); };
  }

  updateAuthStatus();
  void renderUsers();
  void renderMenuList();
}
