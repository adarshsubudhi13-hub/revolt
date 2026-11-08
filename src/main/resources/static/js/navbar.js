/**
 * Injects responsive navigation and footer across all pages
 */
function renderNavbar() {
  const user = Revolt.getUser();
  const path = window.location.pathname;

  const header = document.createElement('header');
  header.className = 'navbar';

  let roleNavLinks = '';
  if (user && user.role === 'AGENT') {
    roleNavLinks = `<li><a href="/agent.html" class="${path.includes('agent') ? 'active' : ''}">Agent Portal</a></li>`;
  } else if (user && user.role === 'ADMIN') {
    roleNavLinks = `
      <li><a href="/shop.html" class="${path.includes('shop') ? 'active' : ''}">Catalog</a></li>
      <li><a href="/agent.html" class="${path.includes('agent') ? 'active' : ''}">Agent Portal</a></li>
      <li><a href="/admin.html" class="${path.includes('admin') ? 'active' : ''}">Admin Control</a></li>
    `;
  } else {
    roleNavLinks = `
      <li><a href="/tradein.html" class="${path.includes('tradein') ? 'active' : ''}">Trade-in & Valuate</a></li>
      <li><a href="/shop.html" class="${path.includes('shop') ? 'active' : ''}">Shop Electronics</a></li>
      <li><a href="/cart.html" class="${path.includes('cart') ? 'active' : ''}">Cart 🛒</a></li>
      <li><a href="/orders.html" class="${path.includes('orders') ? 'active' : ''}">My Orders</a></li>
    `;
  }

  let userActionHtml = '';
  if (user) {
    userActionHtml = `
      <div class="points-pill" title="1 Point = ₹1 INR Discount">
        <span>💎</span>
        <span><b>${(user.availablePoints || 0).toLocaleString()}</b> pts</span>
      </div>
      <div style="display:flex; align-items:center; gap:8px;">
        <span style="font-weight:600; font-size:0.9rem;">${user.fullName.split(' ')[0]}</span>
        <button class="btn btn-secondary btn-sm" onclick="handleLogout()">Logout</button>
      </div>
    `;
  } else {
    userActionHtml = `
      <a href="/login.html" class="btn btn-primary btn-sm">Sign In / Register</a>
    `;
  }

  header.innerHTML = `
    <div class="container nav-container">
      <a href="/index.html" class="brand">
        <div class="brand-icon">⚡</div>
        <span>ReVolt</span>
      </a>

      <ul class="nav-links">
        <li><a href="/index.html" class="${path === '/' || path.includes('index') ? 'active' : ''}">Home</a></li>
        ${roleNavLinks}
      </ul>

      <div class="nav-actions">
        <button class="btn btn-secondary btn-sm" onclick="RevoltUI.toggleTheme()" title="Toggle Dark/Light Mode">🌓</button>
        ${userActionHtml}
      </div>
    </div>
  `;

  document.body.prepend(header);

  // Render Footer
  const footer = document.createElement('footer');
  footer.innerHTML = `
    <div class="container">
      <p><b>⚡ ReVolt</b> &copy; 2026. Electronics Trade-in & Circular Commerce Platform.</p>
      <p style="font-size:0.8rem; margin-top:6px; color:var(--text-muted);">
        Doorstep Physical Verification &bull; Double-Entry Points Ledger &bull; Java Spring Boot &bull; Pure Vanilla Web
      </p>
    </div>
  `;
  document.body.appendChild(footer);
}

function handleLogout() {
  Revolt.clearAuth();
  RevoltUI.toast('Logged out successfully', 'info');
  window.location.href = '/index.html';
}

document.addEventListener('DOMContentLoaded', renderNavbar);
