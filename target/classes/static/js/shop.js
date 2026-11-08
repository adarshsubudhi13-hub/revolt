/**
 * Shop Catalog, Multi-Facet Filters, Quick View & Side-by-Side Comparison
 */

let allProducts = [];
let selectedCategory = 'ALL';
let compareIds = new Set();

async function initShop() {
  await loadFilterOptions();
  await loadProducts();
  updateBanner();
}

async function loadFilterOptions() {
  try {
    const filters = await Revolt.request('/products/filters');
    const brandSelect = document.getElementById('shop-brand-filter');
    if (filters && filters.brands) {
      brandSelect.innerHTML = `<option value="">All Brands</option>` +
        filters.brands.map(b => `<option value="${b}">${b}</option>`).join('');
    }
  } catch (err) {
    console.error(err);
  }
}

async function loadProducts() {
  try {
    const query = document.getElementById('shop-search').value.trim();
    const brand = document.getElementById('shop-brand-filter').value;
    const sort = document.getElementById('shop-sort').value;

    let url = `/products?sort=${sort}`;
    if (selectedCategory && selectedCategory !== 'ALL') url += `&category=${encodeURIComponent(selectedCategory)}`;
    if (brand) url += `&brand=${encodeURIComponent(brand)}`;
    if (query) url += `&search=${encodeURIComponent(query)}`;

    allProducts = await Revolt.request(url);
    renderProducts(allProducts);
  } catch (err) {
    console.error(err);
  }
}

function selectCategory(cat, btn) {
  selectedCategory = cat;
  document.querySelectorAll('.cat-pill').forEach(el => el.classList.remove('active'));
  btn.classList.add('active');
  loadProducts();
}

function applyFilters() {
  loadProducts();
}

function clearAllFilters() {
  document.getElementById('shop-search').value = '';
  document.getElementById('shop-brand-filter').value = '';
  document.getElementById('shop-sort').value = 'default';
  selectedCategory = 'ALL';
  document.querySelectorAll('.cat-pill').forEach((el, idx) => {
    if (idx === 0) el.classList.add('active');
    else el.classList.remove('active');
  });
  loadProducts();
}

function updateBanner() {
  const user = Revolt.getUser();
  const banner = document.getElementById('points-banner');
  if (user && user.availablePoints > 0) {
    document.getElementById('banner-points').innerText = user.availablePoints.toLocaleString();
    document.getElementById('banner-rupees').innerText = user.availablePoints.toLocaleString();
    banner.style.display = 'flex';
  } else {
    banner.style.display = 'none';
  }
}

function renderProducts(products) {
  const container = document.getElementById('product-grid');
  document.getElementById('results-count').innerText = `Showing ${products.length} flagship electronics`;

  if (products.length === 0) {
    container.innerHTML = `
      <div style="grid-column: 1/-1; text-align:center; padding:60px;">
        <span style="font-size:3rem;">🔍</span>
        <h3 style="margin-top:12px;">No products match your filters</h3>
        <p style="color:var(--text-muted); margin-bottom:20px;">Try clearing the search query or changing category.</p>
        <button class="btn btn-secondary" onclick="clearAllFilters()">Reset Filters</button>
      </div>
    `;
    return;
  }

  container.innerHTML = products.map(p => {
    const maxPointsDiscount = Math.round(p.price * 0.3);
    const effectivePrice = p.price - maxPointsDiscount;
    const isCompared = compareIds.has(p.id);

    return `
      <div class="card product-card">
        <div style="position:relative;">
          <img src="${p.imageUrl}" alt="${p.title}" class="product-img" onclick="openQuickView(${p.id})" style="cursor:pointer;">
          <button class="btn btn-secondary btn-sm" onclick="toggleCompare(${p.id})" 
                  style="position:absolute; top:10px; right:10px; padding:4px 8px; font-size:0.75rem; background:rgba(255,255,255,0.9); ${isCompared ? 'border-color:var(--primary); color:var(--primary); font-weight:700;' : ''}">
            ${isCompared ? '✓ Compared' : '+ Compare'}
          </button>
        </div>

        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
          <span class="badge badge-info">${p.category}</span>
          <div class="star-rating">
            <span>★</span>
            <span>${p.rating || 4.8}</span>
            <span style="color:var(--text-muted); font-size:0.75rem;">(${p.reviewCount || 30})</span>
          </div>
        </div>

        <h3 class="product-title" onclick="openQuickView(${p.id})" style="cursor:pointer;">${p.title}</h3>
        <p style="font-size:0.83rem; color:var(--text-muted); margin-bottom:12px; display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden;">
          ${p.description}
        </p>

        <div style="margin-top:auto;">
          <div style="display:flex; align-items:baseline; gap:8px; margin-bottom:6px;">
            <span class="product-price">${Revolt.formatRupees(p.price)}</span>
            <span style="font-size:0.8rem; color:var(--text-muted);">M.R.P.</span>
          </div>

          <div class="product-points-hint" style="margin-bottom:14px;">
            💎 Redeem up to <b>${maxPointsDiscount.toLocaleString()} pts</b> &bull; Pay <b>${Revolt.formatRupees(effectivePrice)}</b>
          </div>

          <div style="display:flex; gap:8px;">
            <button class="btn btn-secondary btn-sm" style="flex:1;" onclick="openQuickView(${p.id})">Specs</button>
            <button class="btn btn-secondary btn-sm" style="flex:1;" onclick="addToCart(${p.id})">Add 🛒</button>
            <button class="btn btn-primary btn-sm" style="flex:1.2;" onclick="buyNow(${p.id})">Buy Swap</button>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

function addToCart(productId) {
  const product = allProducts.find(p => p.id === productId);
  if (!product) return;

  let cart = JSON.parse(localStorage.getItem('revolt_cart') || '[]');
  const existing = cart.find(item => item.id === productId);

  if (existing) {
    existing.quantity += 1;
  } else {
    cart.push({ ...product, quantity: 1 });
  }

  localStorage.setItem('revolt_cart', JSON.stringify(cart));
  RevoltUI.toast(`Added ${product.title} to cart!`, 'success');
}

function buyNow(productId) {
  addToCart(productId);
  window.location.href = '/cart.html';
}

/* Quick View Modal */
function openQuickView(productId) {
  const p = allProducts.find(item => item.id === productId);
  if (!p) return;

  document.getElementById('qv-title').innerText = p.title;
  document.getElementById('qv-img').src = p.imageUrl;
  document.getElementById('qv-brand-badge').innerText = `${p.brand} &bull; ${p.category}`;
  document.getElementById('qv-price').innerText = Revolt.formatRupees(p.price);
  
  const maxPts = Math.round(p.price * 0.3);
  document.getElementById('qv-points-hint').innerHTML = `💎 Pay up to <b>${maxPts.toLocaleString()} points</b> off at checkout!`;
  document.getElementById('qv-desc').innerText = p.description;

  // Render parsed specs JSON
  let specsHtml = '<div style="display:grid; grid-template-columns:repeat(2, 1fr); gap:10px;">';
  try {
    const specs = JSON.parse(p.specsJson || '{}');
    for (const [k, v] of Object.entries(specs)) {
      specsHtml += `
        <div style="padding:6px 10px; background:var(--bg-card); border-radius:var(--radius-sm); border:1px solid var(--border-color);">
          <span style="color:var(--text-muted); display:block; font-size:0.75rem;">${k}</span>
          <b style="font-size:0.85rem;">${v}</b>
        </div>
      `;
    }
  } catch (e) {
    specsHtml += '<div>No specs detailed.</div>';
  }
  specsHtml += '</div>';
  document.getElementById('qv-specs-table').innerHTML = specsHtml;

  document.getElementById('qv-add-cart-btn').onclick = () => {
    addToCart(p.id);
    closeQuickView();
  };

  document.getElementById('quickview-modal').classList.add('active');
}

function closeQuickView() {
  document.getElementById('quickview-modal').classList.remove('active');
}

/* Comparison Dock & Modal */
function toggleCompare(id) {
  if (compareIds.has(id)) {
    compareIds.delete(id);
  } else {
    if (compareIds.size >= 3) {
      RevoltUI.toast('You can compare a maximum of 3 products at a time', 'warning');
      return;
    }
    compareIds.add(id);
  }
  updateCompareDock();
  renderProducts(allProducts);
}

function updateCompareDock() {
  const dock = document.getElementById('compare-dock');
  document.getElementById('compare-count').innerText = `${compareIds.size} Products`;
  if (compareIds.size > 0) {
    dock.classList.add('visible');
  } else {
    dock.classList.remove('visible');
  }
}

function clearComparison() {
  compareIds.clear();
  updateCompareDock();
  renderProducts(allProducts);
}

async function openComparisonModal() {
  if (compareIds.size === 0) return;
  const ids = Array.from(compareIds).join(',');
  try {
    const products = await Revolt.request(`/products/compare?ids=${ids}`);
    renderComparisonTable(products);
    document.getElementById('comparison-modal').classList.add('active');
  } catch (err) {
    console.error(err);
  }
}

function closeComparisonModal() {
  document.getElementById('comparison-modal').classList.remove('active');
}

function renderComparisonTable(products) {
  const container = document.getElementById('comparison-content');
  
  // Aggregate all unique spec keys
  const allSpecKeys = new Set();
  const parsedSpecs = products.map(p => {
    try {
      const s = JSON.parse(p.specsJson || '{}');
      Object.keys(s).forEach(k => allSpecKeys.add(k));
      return s;
    } catch(e) {
      return {};
    }
  });

  let html = `
    <table style="width:100%; border-collapse:collapse; text-align:left; font-size:0.9rem;">
      <thead>
        <tr style="border-bottom:2px solid var(--border-color);">
          <th style="padding:14px; width:25%;">Feature / Spec</th>
          ${products.map(p => `
            <th style="padding:14px; text-align:center;">
              <img src="${p.imageUrl}" style="width:100px; height:80px; object-fit:cover; border-radius:6px; display:block; margin:0 auto 8px;">
              <div style="font-weight:700; font-size:0.95rem;">${p.title}</div>
              <div style="color:var(--primary); font-size:1.1rem; font-weight:800; margin:4px 0;">${Revolt.formatRupees(p.price)}</div>
              <button class="btn btn-primary btn-sm" onclick="buyNow(${p.id})">Buy</button>
            </th>
          `).join('')}
        </tr>
      </thead>
      <tbody>
        <tr style="border-bottom:1px solid var(--border-color); background:var(--bg-main);">
          <td style="padding:12px; font-weight:700;">Max Trade-In Points (30% Cap)</td>
          ${products.map(p => `
            <td style="padding:12px; text-align:center; color:#b45309; font-weight:700;">
              💎 ${Math.round(p.price * 0.3).toLocaleString()} pts
            </td>
          `).join('')}
        </tr>
        <tr style="border-bottom:1px solid var(--border-color);">
          <td style="padding:12px; font-weight:700;">Effective Price with Trade-in</td>
          ${products.map(p => `
            <td style="padding:12px; text-align:center; font-weight:800; color:var(--success);">
              ${Revolt.formatRupees(p.price - Math.round(p.price * 0.3))}
            </td>
          `).join('')}
        </tr>
        <tr style="border-bottom:1px solid var(--border-color); background:var(--bg-main);">
          <td style="padding:12px; font-weight:700;">Brand & Category</td>
          ${products.map(p => `
            <td style="padding:12px; text-align:center;">${p.brand} &bull; ${p.category}</td>
          `).join('')}
        </tr>
        ${Array.from(allSpecKeys).map(k => `
          <tr style="border-bottom:1px solid var(--border-color);">
            <td style="padding:12px; font-weight:600; color:var(--text-muted);">${k}</td>
            ${parsedSpecs.map(s => `
              <td style="padding:12px; text-align:center;">${s[k] || '-'}</td>
            `).join('')}
          </tr>
        `).join('')}
      </tbody>
    </table>
  `;

  container.innerHTML = html;
}

document.addEventListener('DOMContentLoaded', initShop);
