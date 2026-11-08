/**
 * ReVolt API & UI Client
 * Handles authentication headers, REST calls, modals, and toasts.
 */
const API_BASE = '/api';

const Revolt = {
  getToken() {
    return localStorage.getItem('revolt_token');
  },

  getUser() {
    const u = localStorage.getItem('revolt_user');
    return u ? JSON.parse(u) : null;
  },

  setAuth(token, user) {
    localStorage.setItem('revolt_token', token);
    localStorage.setItem('revolt_user', JSON.stringify(user));
  },

  clearAuth() {
    localStorage.removeItem('revolt_token');
    localStorage.removeItem('revolt_user');
  },

  isLoggedIn() {
    return !!this.getToken();
  },

  async request(endpoint, options = {}) {
    const url = `${API_BASE}${endpoint}`;
    const headers = options.headers || {};

    if (!(options.body instanceof FormData)) {
      headers['Content-Type'] = 'application/json';
    }

    const token = this.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers
    };

    try {
      const response = await fetch(url, config);
      if (response.status === 401) {
        this.clearAuth();
        if (window.location.pathname !== '/login.html') {
          window.location.href = '/login.html';
        }
        throw new Error('Session expired. Please log in.');
      }

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(data.message || `Request failed with status ${response.status}`);
      }

      return data;
    } catch (err) {
      RevoltUI.toast(err.message, 'error');
      throw err;
    }
  },

  formatRupees(amount) {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(amount || 0);
  }
};

const RevoltUI = {
  toast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = 'toast';

    let icon = 'ℹ️';
    if (type === 'success') icon = '✅';
    if (type === 'error') icon = '❌';
    if (type === 'warning') icon = '⚠️';

    toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(10px)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 4000);
  },

  toggleTheme() {
    const current = document.documentElement.getAttribute('data-theme') || 'light';
    const next = current === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', next);
    localStorage.setItem('revolt_theme', next);
  },

  initTheme() {
    const saved = localStorage.getItem('revolt_theme') || 'light';
    document.documentElement.setAttribute('data-theme', saved);
  }
};

// Initialize theme on load
document.addEventListener('DOMContentLoaded', () => {
  RevoltUI.initTheme();
});
