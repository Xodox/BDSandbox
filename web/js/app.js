(function () {
  'use strict';

  const API = '/api';
  const AUTH_KEY = 'bdsb_auth';

  let auth = null;
  let role = null;
  let driverId = null;
  let canAccessPages = false;

  function getAuth() {
    const raw = sessionStorage.getItem(AUTH_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw);
    } catch (_) {
      return null;
    }
  }

  function setAuth(username, password) {
    auth = { username, password };
    sessionStorage.setItem(AUTH_KEY, JSON.stringify(auth));
  }

  function clearAuth() {
    auth = null;
    sessionStorage.removeItem(AUTH_KEY);
    role = null;
    driverId = null;
    canAccessPages = false;
  }

  function authHeader() {
    if (!auth) return {};
    const token = btoa(auth.username + ':' + auth.password);
    return { Authorization: 'Basic ' + token };
  }

  function apiFetch(path, options = {}) {
    const url = path.startsWith('http') ? path : API + path;
    const headers = { ...authHeader(), ...(options.headers || {}) };
    if (options.body && typeof options.body === 'object' && !(options.body instanceof FormData)) {
      headers['Content-Type'] = 'application/json';
      options.body = JSON.stringify(options.body);
    }
    return fetch(url, { ...options, headers });
  }

  function detectRole() {
    return apiFetch('/drivers/me')
      .then(r => {
        if (r.ok) return r.json().then(me => ({ me, hasMe: true }));
        return { me: null, hasMe: false };
      })
      .then(({ me, hasMe }) => {
        if (me) driverId = me.id;
        return apiFetch('/drivers').then(r => r.ok ? r.json() : []);
      })
      .then(drivers => {
        if (drivers.length === 1 && driverId && drivers[0].id === driverId) role = 'DRIVER';
        else role = 'OFFICER';
        return apiFetch('/url_page/getAll').then(r => ({ ok: r.ok }));
      })
      .then(({ ok }) => {
        canAccessPages = ok;
        if (ok) role = 'ADMIN';
      });
  }

  function showLogin() {
    document.getElementById('login-page').classList.remove('hidden');
    document.getElementById('main-app').classList.add('hidden');
  }

  function showApp() {
    document.getElementById('login-page').classList.add('hidden');
    document.getElementById('main-app').classList.remove('hidden');
    const u = document.querySelector('.user-info');
    if (u) u.textContent = auth ? auth.username + (role ? ' (' + role + ')' : '') : '';
    const adminLink = document.querySelector('.nav-admin');
    if (adminLink) adminLink.style.display = canAccessPages ? '' : 'none';
    const meLink = document.querySelector('.nav-link[data-page="me"]');
    if (meLink) meLink.style.display = driverId != null ? '' : 'none';
    const addBtn = document.getElementById('driver-add-btn');
    if (addBtn) addBtn.style.display = role === 'DRIVER' ? 'none' : '';
    const carsToolbar = document.getElementById('cars-toolbar');
    if (carsToolbar) carsToolbar.style.display = canAccessPages ? '' : 'none';
  }

  function showPage(id) {
    document.querySelectorAll('.content-page').forEach(el => el.classList.add('hidden'));
    document.querySelectorAll('.nav-link').forEach(el => el.classList.remove('active'));
    const page = document.getElementById(id);
    if (page) page.classList.remove('hidden');
    const link = document.querySelector('.nav-link[data-page="' + id.replace('-page', '').replace('-form', '').replace('s', '') + '"]');
    if (!link && (id.startsWith('driver-form') || id.startsWith('car-form'))) {
      document.querySelector('.nav-link[data-page="' + (id.indexOf('driver') !== -1 ? 'drivers' : 'cars') + '"]')?.classList.add('active');
    } else if (!link && id.startsWith('car-view')) document.querySelector('.nav-link[data-page="cars"]')?.classList.add('active');
    else if (link) link.classList.add('active');
  }

  function navTo(page, sub) {
    if (page === 'drivers' && sub) {
      showPage('driver-form-page');
      renderDriverForm(sub);
      return;
    }
    if (page === 'cars' && sub) {
      showPage('car-view-page');
      renderCarView(sub);
      return;
    }
    const map = { drivers: 'drivers-page', cars: 'cars-page', me: 'me-page', pages: 'pages-page' };
    showPage(map[page] || 'drivers-page');
    if (page === 'drivers') loadDrivers();
    if (page === 'cars') loadCars();
    if (page === 'me') loadMe();
    if (page === 'pages') loadPages();
  }

  function parseHash() {
    const hash = (window.location.hash || '#/').slice(1);
    const parts = hash.split('/').filter(Boolean);
    const page = parts[0] || 'drivers';
    const id = parts[1];
    const sub = parts[2];
    if (page === 'drivers' && id === 'new') {
      showPage('driver-form-page');
      renderDriverForm(null);
      document.querySelector('.nav-link[data-page="drivers"]')?.classList.add('active');
      return;
    }
    if (page === 'cars' && id === 'new') {
      showPage('car-form-page');
      renderCarForm(null);
      document.querySelector('.nav-link[data-page="cars"]')?.classList.add('active');
      return;
    }
    if (page === 'cars' && id === 'edit' && sub) {
      showPage('car-form-page');
      renderCarForm(parseInt(sub, 10));
      document.querySelector('.nav-link[data-page="cars"]')?.classList.add('active');
      return;
    }
    navTo(page, id ? parseInt(id, 10) : null);
  }

  function loadDrivers() {
    const list = document.getElementById('drivers-list');
    list.innerHTML = '<p>Loading…</p>';
    apiFetch('/drivers')
      .then(r => {
        if (r.status === 403 || r.status === 401) { clearAuth(); showLogin(); return []; }
        if (!r.ok) return [];
        return r.json();
      })
      .then(arr => {
        list.innerHTML = '';
        arr.forEach(d => {
          const card = document.createElement('div');
          card.className = 'card';
          card.innerHTML =
            '<div><p class="card-title">' + escapeHtml(d.firstName + ' ' + d.lastName) + '</p>' +
            '<p class="card-meta">Born: ' + (d.yearOfBirth || '') + '</p></div>' +
            '<div class="card-actions">' +
            '<a href="#/drivers/' + d.id + '" class="btn btn-outline btn-sm">Edit</a>' +
            (role !== 'DRIVER' ? '<button type="button" class="btn btn-danger btn-sm" data-id="' + d.id + '" data-action="delete-driver">Delete</button>' : '') +
            '</div>';
          list.appendChild(card);
        });
        list.querySelectorAll('[data-action="delete-driver"]').forEach(btn => {
          btn.addEventListener('click', () => deleteDriver(parseInt(btn.dataset.id, 10)));
        });
      });
  }

  function deleteDriver(id) {
    if (!confirm('Delete this driver?')) return;
    apiFetch('/drivers/' + id, { method: 'DELETE' })
      .then(r => {
        if (r.status === 403 || r.status === 401) { clearAuth(); showLogin(); return; }
        loadDrivers();
      });
  }

  function renderDriverForm(id) {
    const title = document.getElementById('driver-form-title');
    const formEl = document.getElementById('driver-form');
    const assignedCarsEl = document.getElementById('driver-assigned-cars');
    const isNew = !id;
    if (title) title.textContent = isNew ? 'New driver' : 'Edit driver';
    if (assignedCarsEl) {
      assignedCarsEl.innerHTML = '';
      assignedCarsEl.classList.toggle('hidden', isNew);
    }
    if (isNew) {
      formEl.innerHTML =
        '<label>First name</label><input name="firstName" type="text">' +
        '<label>Last name</label><input name="lastName" type="text">' +
        '<label>Year of birth</label><input name="yearOfBirth" type="number" min="1900" max="2100">' +
        '<div class="actions"><button type="submit" class="btn btn-primary">Create</button><a href="#/drivers" class="btn btn-outline">Cancel</a></div>';
      formEl.onsubmit = e => {
        e.preventDefault();
        const fd = new FormData(formEl);
        apiFetch('/drivers', {
          method: 'POST',
          body: {
            firstName: fd.get('firstName'),
            lastName: fd.get('lastName'),
            yearOfBirth: fd.get('yearOfBirth') ? parseInt(fd.get('yearOfBirth'), 10) : null
          }
        }).then(r => {
          if (r.status === 401 || r.status === 403) { clearAuth(); showLogin(); return; }
          if (r.ok) window.location.hash = '#/drivers';
        });
      };
      return;
    }
    apiFetch('/drivers/' + id)
      .then(r => {
        if (r.status === 403 || r.status === 401) { clearAuth(); showLogin(); return null; }
        return r.ok ? r.json() : null;
      })
      .then(d => {
        if (!d) { formEl.innerHTML = '<p>Not found.</p>'; return; }
        formEl.innerHTML =
          '<label>First name</label><input name="firstName" type="text" value="' + escapeAttr(d.firstName || '') + '">' +
          '<label>Last name</label><input name="lastName" type="text" value="' + escapeAttr(d.lastName || '') + '">' +
          '<label>Year of birth</label><input name="yearOfBirth" type="number" min="1900" max="2100" value="' + (d.yearOfBirth || '') + '">' +
          '<div class="actions"><button type="submit" class="btn btn-primary">Save</button><a href="#/drivers" class="btn btn-outline">Cancel</a></div>';
        formEl.onsubmit = e => {
          e.preventDefault();
          const fd = new FormData(formEl);
          apiFetch('/drivers/' + id, {
            method: 'PUT',
            body: {
              id: id,
              firstName: fd.get('firstName'),
              lastName: fd.get('lastName'),
              yearOfBirth: fd.get('yearOfBirth') ? parseInt(fd.get('yearOfBirth'), 10) : null
            }
          }).then(r => {
            if (r.status === 401 || r.status === 403) { clearAuth(); showLogin(); return; }
            if (r.ok) window.location.hash = '#/drivers';
          });
        };
        return apiFetch('/drivers/' + id + '/cars').then(r => r.ok ? r.json() : []);
      })
      .then(cars => {
        if (!assignedCarsEl || isNew) return;
        assignedCarsEl.classList.remove('hidden');
        assignedCarsEl.innerHTML = '<div class="driver-cars-block"><h3>Cars this driver has</h3><p class="section-desc">Cars assigned to this driver (read-only).</p></div>';
        const block = assignedCarsEl.querySelector('.driver-cars-block');
        if (!cars || !cars.length) {
          block.innerHTML += '<p class="read-only-muted">No cars assigned.</p>';
          return;
        }
        const list = document.createElement('div');
        list.className = 'card-list read-only-list';
        cars.forEach(c => {
          const card = document.createElement('div');
          card.className = 'card read-only-card';
          card.innerHTML =
            '<div><p class="card-title">' + escapeHtml((c.name || '') + ' ' + (c.model || '')) + '</p>' +
            '<p class="card-meta">Year: ' + (c.manufacturingYear || '') + '</p></div>';
          list.appendChild(card);
        });
        block.appendChild(list);
      });
  }

  function loadCars() {
    const list = document.getElementById('cars-list');
    list.innerHTML = '<p>Loading…</p>';
    apiFetch('/cars')
      .then(r => {
        if (r.status === 403 || r.status === 401) { clearAuth(); showLogin(); return []; }
        if (!r.ok) return [];
        return r.json();
      })
      .then(arr => {
        list.innerHTML = '';
        arr.forEach(c => {
          const card = document.createElement('div');
          card.className = 'card';
          const adminBtns = canAccessPages
            ? '<a href="#/cars/' + c.id + '" class="btn btn-outline btn-sm">View</a>' +
              '<a href="#/cars/edit/' + c.id + '" class="btn btn-outline btn-sm">Edit</a>' +
              '<button type="button" class="btn btn-danger btn-sm" data-id="' + c.id + '" data-action="delete-car">Delete</button>'
            : '<a href="#/cars/' + c.id + '" class="btn btn-outline btn-sm">View</a>';
          card.innerHTML =
            '<div><p class="card-title">' + escapeHtml(c.name || '') + ' ' + escapeHtml(c.model || '') + '</p>' +
            '<p class="card-meta">Year: ' + (c.manufacturingYear || '') + '</p></div>' +
            '<div class="card-actions">' + adminBtns + '</div>';
          list.appendChild(card);
        });
        list.querySelectorAll('[data-action="delete-car"]').forEach(btn => {
          btn.addEventListener('click', () => deleteCar(parseInt(btn.dataset.id, 10)));
        });
      });
  }

  function deleteCar(id) {
    if (!confirm('Delete this car?')) return;
    apiFetch('/cars/' + id, { method: 'DELETE' })
      .then(r => {
        if (r.status === 403 || r.status === 401) { clearAuth(); showLogin(); return; }
        loadCars();
      });
  }

  function renderCarForm(id) {
    const title = document.getElementById('car-form-title');
    const formEl = document.getElementById('car-form');
    const isNew = !id;
    if (title) title.textContent = isNew ? 'New car' : 'Edit car';
    formEl.innerHTML = '<p>Loading…</p>';

    Promise.all([
      apiFetch('/drivers').then(r => r.ok ? r.json() : []),
      isNew ? Promise.resolve(null) : apiFetch('/cars/' + id).then(r => r.ok ? r.json() : null),
      isNew ? Promise.resolve([]) : apiFetch('/cars/' + id + '/drivers').then(r => r.ok ? r.json() : [])
    ]).then(([drivers, car, driverIds]) => {
      if (!isNew && !car) { formEl.innerHTML = '<p>Not found.</p>'; return; }
      const selId = 'car-form-drivers';
      let options = '';
      (drivers || []).forEach(d => {
        const selected = (driverIds || []).indexOf(d.id) !== -1 ? ' selected' : '';
        options += '<option value="' + d.id + '"' + selected + '>' + escapeHtml((d.firstName || '') + ' ' + (d.lastName || '') + ' (ID ' + d.id + ')') + '</option>';
      });
      formEl.innerHTML =
        '<label>Name</label><input name="name" type="text" value="' + escapeAttr((car && car.name) || '') + '">' +
        '<label>Model</label><input name="model" type="text" value="' + escapeAttr((car && car.model) || '') + '">' +
        '<label>Manufacturing year</label><input name="manufacturingYear" type="number" min="1900" max="2100" value="' + ((car && car.manufacturingYear) || '') + '">' +
        '<label>Drivers</label><select id="' + selId + '" name="driverIds" multiple class="select-multi">' + options + '</select><p class="hint">Hold Ctrl (Windows/Linux) or Cmd (Mac) to select several drivers.</p>' +
        '<div class="actions"><button type="submit" class="btn btn-primary">' + (isNew ? 'Create' : 'Save') + '</button><a href="#/cars" class="btn btn-outline">Cancel</a></div>';

      formEl.onsubmit = function (e) {
        e.preventDefault();
        const fd = new FormData(formEl);
        const sel = document.getElementById(selId);
        const selectedIds = Array.from(sel.selectedOptions).map(o => parseInt(o.value, 10)).filter(Boolean);
        const body = {
          name: fd.get('name'),
          model: fd.get('model'),
          manufacturingYear: fd.get('manufacturingYear') ? parseInt(fd.get('manufacturingYear'), 10) : null
        };
        if (isNew) {
          apiFetch('/cars', { method: 'POST', body: body })
            .then(r => {
              if (r.status === 401 || r.status === 403) { clearAuth(); showLogin(); return null; }
              return r.ok ? r.json() : null;
            })
            .then(created => {
              if (!created) return;
              apiFetch('/cars/' + created.id + '/drivers', { method: 'PUT', body: selectedIds }).then(() => {});
              window.location.hash = '#/cars';
            });
        } else {
          apiFetch('/cars/' + id, { method: 'PUT', body: body })
            .then(r => {
              if (r.status === 401 || r.status === 403) { clearAuth(); showLogin(); return; }
              if (!r.ok) return;
              return apiFetch('/cars/' + id + '/drivers', { method: 'PUT', body: selectedIds });
            })
            .then(r => {
              if (r && (r.status === 401 || r.status === 403)) { clearAuth(); showLogin(); return; }
              window.location.hash = '#/cars';
            });
        }
      };
    });
  }

  function renderCarView(id) {
    const wrap = document.getElementById('car-view');
    wrap.innerHTML = '<p>Loading…</p>';
    apiFetch('/cars/' + id)
      .then(r => {
        if (r.status === 403 || r.status === 401) { clearAuth(); showLogin(); return null; }
        return r.ok ? r.json() : null;
      })
      .then(c => {
        if (!c) { wrap.innerHTML = '<p>Not found.</p>'; return; }
        wrap.innerHTML =
          '<p class="row"><strong>Name:</strong> ' + escapeHtml(c.name || '') + '</p>' +
          '<p class="row"><strong>Model:</strong> ' + escapeHtml(c.model || '') + '</p>' +
          '<p class="row"><strong>Manufacturing year:</strong> ' + (c.manufacturingYear || '') + '</p>' +
          '<div class="actions"><a href="#/cars" class="btn btn-outline">Back to list</a></div>';
      });
  }

  function loadMe() {
    const profile = document.getElementById('me-profile');
    const carsEl = document.getElementById('me-cars');
    profile.innerHTML = '<p>Loading…</p>';
    carsEl.innerHTML = '';
    apiFetch('/drivers/me')
      .then(r => {
        if (r.status === 403 || r.status === 404) { profile.innerHTML = '<p>No driver profile linked to your account.</p>'; return null; }
        if (r.status === 401) { clearAuth(); showLogin(); return null; }
        return r.ok ? r.json() : null;
      })
      .then(me => {
        if (!me) return;
        profile.innerHTML =
          '<p class="row"><strong>First name:</strong> ' + escapeHtml(me.firstName || '') + '</p>' +
          '<p class="row"><strong>Last name:</strong> ' + escapeHtml(me.lastName || '') + '</p>' +
          '<p class="row"><strong>Year of birth:</strong> ' + (me.yearOfBirth || '') + '</p>' +
          '<div class="actions"><a href="#/drivers/' + me.id + '" class="btn btn-primary">Edit my profile</a></div>';
        return apiFetch('/drivers/me/cars').then(r => r.ok ? r.json() : []);
      })
      .then(cars => {
        if (!cars || !cars.length) { carsEl.innerHTML = '<p>No cars assigned.</p>'; return; }
        carsEl.innerHTML = '';
        cars.forEach(c => {
          const card = document.createElement('div');
          card.className = 'card';
          card.innerHTML =
            '<div><p class="card-title">' + escapeHtml(c.name || '') + ' ' + escapeHtml(c.model || '') + '</p>' +
            '<p class="card-meta">Year: ' + (c.manufacturingYear || '') + '</p></div>' +
            '<a href="#/cars/' + c.id + '" class="btn btn-outline btn-sm">View</a>';
          carsEl.appendChild(card);
        });
      });
  }

  function loadPages() {
    const list = document.getElementById('pages-list');
    list.innerHTML = '<p>Loading…</p>';
    apiFetch('/url_page/getAll')
      .then(r => {
        if (r.status === 403 || r.status === 401) { clearAuth(); showLogin(); return []; }
        if (!r.ok) return [];
        return r.json();
      })
      .then(arr => {
        list.innerHTML = '';
        (arr || []).forEach(p => {
          const card = document.createElement('div');
          card.className = 'card';
          card.innerHTML =
            '<div><p class="card-title">' + escapeHtml(p.name || '') + '</p>' +
            '<p class="card-meta">' + escapeHtml(p.url || '') + '</p></div>';
          list.appendChild(card);
        });
      });
  }

  function escapeHtml(s) {
    const div = document.createElement('div');
    div.textContent = s;
    return div.innerHTML;
  }
  function escapeAttr(s) {
    return escapeHtml(s).replace(/"/g, '&quot;');
  }

  document.getElementById('login-form')?.addEventListener('submit', function (e) {
    e.preventDefault();
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const errEl = document.getElementById('login-error');
    errEl.textContent = '';
    setAuth(username, password);
    apiFetch('/drivers')
      .then(r => {
        if (r.status === 401 || r.status === 403) {
          clearAuth();
          errEl.textContent = 'Invalid username or password.';
          return;
        }
        if (!r.ok) {
          errEl.textContent = 'Error: ' + r.status;
          return;
        }
        return detectRole().then(() => {
          showApp();
          window.location.hash = role === 'DRIVER' ? '#/me' : '#/drivers';
          parseHash();
        });
      });
  });

  document.getElementById('logout-btn')?.addEventListener('click', function () {
    clearAuth();
    showLogin();
    window.location.hash = '#/';
  });

  document.getElementById('driver-add-btn')?.addEventListener('click', function () {
    window.location.hash = '#/drivers/new';
    showPage('driver-form-page');
    renderDriverForm(null);
  });

  document.getElementById('car-add-btn')?.addEventListener('click', function () {
    window.location.hash = '#/cars/new';
    showPage('car-form-page');
    renderCarForm(null);
  });

  window.addEventListener('hashchange', parseHash);

  auth = getAuth();
  if (auth) {
    detectRole().then(() => {
      showApp();
      parseHash();
    }).catch(() => {
      clearAuth();
      showLogin();
    });
  } else {
    showLogin();
    if (window.location.hash && window.location.hash !== '#/login') window.location.hash = '#/';
  }
})();
