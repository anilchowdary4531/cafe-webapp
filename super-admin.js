const superAdminScript = document.createElement('script');
superAdminScript.src = 'data.js';
superAdminScript.onload = initSuperAdmin;
document.head.appendChild(superAdminScript);

function initSuperAdmin() {
  void bootstrapSuperAdmin();
}

async function bootstrapSuperAdmin() {
  const loginForm = document.getElementById('superAdminLoginForm');
  const emailInput = document.getElementById('superAdminEmail');
  const passwordInput = document.getElementById('superAdminPassword');
  const logoutBtn = document.getElementById('superAdminLogoutBtn');
  const authStatus = document.getElementById('superAdminAuthStatus');
  const createRestaurantForm = document.getElementById('createRestaurantForm');
  const restaurantNameInput = document.getElementById('restaurantName');
  const restaurantSlugInput = document.getElementById('restaurantSlug');
  const newAdminEmailInput = document.getElementById('newAdminEmail');
  const newAdminPasswordInput = document.getElementById('newAdminPassword');
  const restaurantsList = document.getElementById('restaurantsList');
  const refreshRestaurantsBtn = document.getElementById('refreshRestaurantsBtn');

  function isLoggedIn() {
    return Boolean(getAdminAccessToken());
  }

  async function ensureSuperAdmin() {
    if (!isLoggedIn()) return false;
    try {
      const me = await fetchAuthMe();
      if (me?.role !== 'SUPER_ADMIN') {
        alert('Only super admin can access this page.');
        window.location.href = 'admin.html';
        return false;
      }
      return true;
    } catch (_error) {
      clearAdminAccessToken();
      return false;
    }
  }

  function updateAuthStatus(authorized = false) {
    authStatus.textContent = authorized
      ? 'Super admin authenticated. You can create restaurant admin accounts.'
      : 'Super admin login required for secure actions.';
    authStatus.classList.toggle('verified', authorized);
  }

  async function renderRestaurants() {
    const authorized = await ensureSuperAdmin();
    updateAuthStatus(authorized);

    if (!authorized) {
      restaurantsList.innerHTML = '<p class="muted">Login as super admin to view restaurants.</p>';
      return;
    }

    try {
      const restaurants = await fetchSuperAdminRestaurants();
      if (!Array.isArray(restaurants) || restaurants.length === 0) {
        restaurantsList.innerHTML = '<p class="muted">No restaurant accounts found.</p>';
        return;
      }

      restaurantsList.innerHTML = '';
      restaurants.forEach((restaurant) => {
        const row = document.createElement('div');
        row.className = 'request-row';
        row.innerHTML = `
          <div class="section-head">
            <div>
              <strong>${restaurant.restaurantName}</strong>
              <div class="muted small">Slug: ${restaurant.restaurantSlug}</div>
            </div>
            <span class="status ${restaurant.active ? 'served' : 'cancelled'}">${restaurant.active ? 'Active' : 'Inactive'}</span>
          </div>
          <div class="muted small">Primary admin: ${restaurant.primaryAdminEmail || 'Not assigned'}</div>
        `;
        restaurantsList.appendChild(row);
      });
    } catch (error) {
      restaurantsList.innerHTML = `<p class="muted">${error.message || 'Unable to load restaurants.'}</p>`;
    }
  }

  if (loginForm) {
    loginForm.onsubmit = async (event) => {
      event.preventDefault();
      try {
        const user = await adminLogin(emailInput.value.trim(), passwordInput.value);
        passwordInput.value = '';
        if (user?.role !== 'SUPER_ADMIN') {
          alert('This account is not a super admin.');
          clearAdminAccessToken();
          updateAuthStatus(false);
          return;
        }
        updateAuthStatus(true);
        await renderRestaurants();
      } catch (error) {
        alert(error.message || 'Login failed.');
      }
    };
  }

  if (logoutBtn) {
    logoutBtn.onclick = async () => {
      clearAdminAccessToken();
      updateAuthStatus(false);
      await renderRestaurants();
    };
  }

  if (createRestaurantForm) {
    createRestaurantForm.onsubmit = async (event) => {
      event.preventDefault();
      const authorized = await ensureSuperAdmin();
      if (!authorized) {
        alert('Please login as super admin first.');
        updateAuthStatus(false);
        return;
      }

      try {
        await createRestaurantWithAdmin({
          restaurantName: restaurantNameInput.value.trim(),
          restaurantSlug: restaurantSlugInput.value.trim(),
          adminEmail: newAdminEmailInput.value.trim(),
          adminPassword: newAdminPasswordInput.value
        });
        createRestaurantForm.reset();
        await renderRestaurants();
      } catch (error) {
        alert(error.message || 'Unable to create restaurant account.');
      }
    };
  }

  if (refreshRestaurantsBtn) {
    refreshRestaurantsBtn.onclick = () => { void renderRestaurants(); };
  }

  updateAuthStatus(false);
  await renderRestaurants();
}

