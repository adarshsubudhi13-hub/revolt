/**
 * Cart and Points Redemption Slider Logic
 */

let cart = [];
let userTradeIns = [];
let pointsToRedeem = 0;

async function initCart() {
  cart = JSON.parse(localStorage.getItem('revolt_cart') || '[]');
  renderCartItems();

  if (Revolt.isLoggedIn()) {
    try {
      userTradeIns = await Revolt.request('/tradein/my-devices');
      populateTradeInDropdown();
    } catch (e) {
      console.error(e);
    }
  }

  calculateTotals();
}

function renderCartItems() {
  const container = document.getElementById('cart-items-container');
  if (cart.length === 0) {
    container.innerHTML = `
      <div style="text-align:center; padding:40px;">
        <span style="font-size:3rem;">🛒</span>
        <h3 style="margin-top:12px;">Your cart is empty</h3>
        <p style="color:var(--text-muted); margin-bottom:20px;">Browse our store to find new electronics eligible for trade-in points discounts!</p>
        <a href="/shop.html" class="btn btn-primary">Browse Shop</a>
      </div>
    `;
    return;
  }

  container.innerHTML = `
    <h3 style="margin-bottom:20px;">Cart Items (${cart.length})</h3>
    <div style="display:flex; flex-direction:column; gap:20px;">
      ${cart.map((item, idx) => `
        <div style="display:flex; gap:16px; align-items:center; border-bottom:1px solid var(--border-color); padding-bottom:16px;">
          <img src="${item.imageUrl}" alt="${item.title}" style="width:90px; height:90px; object-fit:cover; border-radius:var(--radius-md);">
          <div style="flex:1;">
            <h4 style="font-size:1rem; margin-bottom:4px;">${item.title}</h4>
            <div style="color:var(--primary); font-weight:700; margin-bottom:8px;">${Revolt.formatRupees(item.price)}</div>
            
            <div style="display:flex; align-items:center; gap:12px;">
              <div style="display:flex; align-items:center; border:1px solid var(--border-color); border-radius:var(--radius-sm);">
                <button class="btn btn-secondary btn-sm" style="padding:2px 8px;" onclick="changeQty(${idx}, -1)">-</button>
                <span style="padding:0 10px; font-weight:600; font-size:0.9rem;">${item.quantity}</span>
                <button class="btn btn-secondary btn-sm" style="padding:2px 8px;" onclick="changeQty(${idx}, 1)">+</button>
              </div>
              <button class="btn btn-danger btn-sm" style="padding:4px 8px; font-size:0.75rem;" onclick="removeItem(${idx})">Remove</button>
            </div>
          </div>
          <div style="text-align:right; font-weight:800; font-size:1.1rem;">
            ${Revolt.formatRupees(item.price * item.quantity)}
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

function changeQty(index, delta) {
  cart[index].quantity += delta;
  if (cart[index].quantity <= 0) {
    cart.splice(index, 1);
  }
  localStorage.setItem('revolt_cart', JSON.stringify(cart));
  renderCartItems();
  calculateTotals();
}

function removeItem(index) {
  cart.splice(index, 1);
  localStorage.setItem('revolt_cart', JSON.stringify(cart));
  renderCartItems();
  calculateTotals();
}

function populateTradeInDropdown() {
  const select = document.getElementById('cart-tradein-select');
  const availableDevices = userTradeIns.filter(d => d.status === 'VALUATED');

  if (availableDevices.length > 0) {
    select.innerHTML = `
      <option value="">No Trade-In (Buy without swap)</option>
      ${availableDevices.map(d => `
        <option value="${d.id}" selected>
          Swap ${d.brand} ${d.model} (${d.estimatedPoints.toLocaleString()} pts available)
        </option>
      `).join('')}
    `;
  }
}

function onTradeInSelectChange() {
  calculateTotals();
}

function calculateTotals() {
  const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  document.getElementById('summary-subtotal').innerText = Revolt.formatRupees(subtotal);

  const user = Revolt.getUser();
  const maxCap = Math.round(subtotal * 0.3); // 30% points cap
  const userAvailable = user ? user.availablePoints || 0 : 0;
  const maxRedeemable = Math.min(maxCap, userAvailable);

  const slider = document.getElementById('points-slider');
  slider.max = maxRedeemable;

  // Auto-apply maximum possible points if available
  if (pointsToRedeem > maxRedeemable) {
    pointsToRedeem = maxRedeemable;
  } else if (pointsToRedeem === 0 && maxRedeemable > 0) {
    pointsToRedeem = maxRedeemable;
  }

  slider.value = pointsToRedeem;
  document.getElementById('slider-points-val').innerText = `${pointsToRedeem.toLocaleString()} pts`;
  document.getElementById('slider-max-hint').innerText = `Max: ${maxRedeemable.toLocaleString()} pts (30% Cap)`;

  const discount = pointsToRedeem;
  const cashToPay = Math.max(0, subtotal - discount);

  document.getElementById('summary-discount').innerText = `- ${Revolt.formatRupees(discount)}`;
  document.getElementById('summary-final-total').innerText = Revolt.formatRupees(cashToPay);
}

function updatePointsRedemption() {
  const slider = document.getElementById('points-slider');
  pointsToRedeem = parseInt(slider.value) || 0;
  calculateTotals();
}

function proceedToCheckout() {
  if (cart.length === 0) {
    RevoltUI.toast('Please add items to cart before checkout', 'warning');
    return;
  }

  if (!Revolt.isLoggedIn()) {
    RevoltUI.toast('Please log in to complete checkout', 'warning');
    window.location.href = '/login.html?redirect=cart.html';
    return;
  }

  const tradeInId = document.getElementById('cart-tradein-select').value;
  sessionStorage.setItem('revolt_checkout_points', pointsToRedeem);
  sessionStorage.setItem('revolt_checkout_tradein', tradeInId || '');

  window.location.href = '/checkout.html';
}

document.addEventListener('DOMContentLoaded', initCart);
