/**
 * Admin Control Center Logic
 */

async function initAdmin() {
  const user = Revolt.getUser();
  if (!user || user.role !== 'ADMIN') {
    RevoltUI.toast('Access restricted. Please log in as an Administrator.', 'error');
    setTimeout(() => {
      window.location.href = '/login.html?redirect=admin.html';
    }, 1200);
    return;
  }

  loadDashboardStats();
  loadOrders();
}

async function loadDashboardStats() {
  try {
    const stats = await Revolt.request('/admin/dashboard-stats');
    document.getElementById('stat-revenue').innerText = Revolt.formatRupees(stats.totalRevenue);
    document.getElementById('stat-orders').innerText = `${stats.totalOrders} Total Orders`;
    document.getElementById('stat-tradeins').innerText = stats.totalTradeIns;
    document.getElementById('stat-mismatches').innerText = `${stats.mismatchCount} Discrepancies Recorded`;
    document.getElementById('stat-points-redeemed').innerText = `${stats.totalPointsRedeemed.toLocaleString()} pts`;
    document.getElementById('stat-eco').innerText = `${stats.eWastePreventedKg.toFixed(1)} kg / ${stats.co2SavedKg.toFixed(0)} kg`;
  } catch (err) {
    console.error(err);
  }
}

async function loadOrders() {
  try {
    const orders = await Revolt.request('/admin/orders');
    const tbody = document.getElementById('orders-table-body');
    tbody.innerHTML = orders.map(o => `
      <tr style="border-bottom:1px solid var(--border-color);">
        <td style="padding:14px;"><b>#${o.orderNumber}</b></td>
        <td style="padding:14px;">User #${o.userId}</td>
        <td style="padding:14px;">${o.productTitle}</td>
        <td style="padding:14px;">${Revolt.formatRupees(o.totalAmount)}</td>
        <td style="padding:14px;">${o.tradeInDeviceId ? `Device #${o.tradeInDeviceId} (${o.pointsRedeemed.toLocaleString()} pts held)` : 'None'}</td>
        <td style="padding:14px;"><span class="badge badge-info">${o.orderStatus.replace('_', ' ')}</span></td>
        <td style="padding:14px;">
          <button class="btn btn-secondary btn-sm" onclick="alert('Order Handover OTP: ' + '${o.handoverOtp}')">View OTP</button>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    console.error(err);
  }
}

async function loadReports() {
  try {
    const reports = await Revolt.request('/admin/inspection-reports');
    const tbody = document.getElementById('reports-table-body');
    tbody.innerHTML = reports.map(r => `
      <tr style="border-bottom:1px solid var(--border-color);">
        <td style="padding:14px;">#AUD-${r.id}</td>
        <td style="padding:14px;">Order #${r.orderId}</td>
        <td style="padding:14px;">
          <span class="badge ${r.isApproved ? 'badge-success' : 'badge-danger'}">
            ${r.isApproved ? 'APPROVED' : 'MISMATCH'}
          </span>
        </td>
        <td style="padding:14px;">${r.physicalGrade ? r.physicalGrade.replace('_', ' ') : 'N/A'}</td>
        <td style="padding:14px;">${r.mismatchReason || 'None'}</td>
        <td style="padding:14px;">${Revolt.formatRupees(r.additionalCashCollected)}</td>
        <td style="padding:14px;">${r.agentNotes || '-'}</td>
      </tr>
    `).join('');
  } catch (err) {
    console.error(err);
  }
}

async function loadEmails() {
  try {
    const emails = await Revolt.request('/admin/email-logs');
    const tbody = document.getElementById('emails-table-body');
    tbody.innerHTML = emails.map(e => `
      <tr style="border-bottom:1px solid var(--border-color);">
        <td style="padding:14px; font-size:0.8rem;">${e.timestamp ? e.timestamp.replace('T', ' ').substring(0, 19) : ''}</td>
        <td style="padding:14px;"><b>${e.recipient}</b></td>
        <td style="padding:14px;"><span class="badge badge-info">${e.emailType}</span></td>
        <td style="padding:14px;">${e.subject}</td>
        <td style="padding:14px;"><span class="badge badge-success">${e.status}</span></td>
      </tr>
    `).join('');
  } catch (err) {
    console.error(err);
  }
}

function switchAdminTab(tab) {
  document.getElementById('admin-tab-orders').style.display = tab === 'orders' ? 'block' : 'none';
  document.getElementById('admin-tab-reports').style.display = tab === 'reports' ? 'block' : 'none';
  document.getElementById('admin-tab-emails').style.display = tab === 'emails' ? 'block' : 'none';

  if (tab === 'reports') loadReports();
  if (tab === 'emails') loadEmails();
}

function openAddProductModal() {
  document.getElementById('product-modal').classList.add('active');
}

function closeAddProductModal() {
  document.getElementById('product-modal').classList.remove('active');
}

async function saveNewProduct() {
  const title = document.getElementById('prod-title').value.trim();
  const category = document.getElementById('prod-category').value;
  const brand = document.getElementById('prod-brand').value.trim();
  const price = parseFloat(document.getElementById('prod-price').value);
  const stock = parseInt(document.getElementById('prod-stock').value) || 10;
  const img = document.getElementById('prod-img').value.trim() || 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop&q=80';
  const desc = document.getElementById('prod-desc').value.trim();

  if (!title || !brand || isNaN(price)) {
    RevoltUI.toast('Please fill all required product fields', 'warning');
    return;
  }

  try {
    await Revolt.request('/admin/products', {
      method: 'POST',
      body: JSON.stringify({
        title, category, brand, price, stockQuantity: stock, imageUrl: img, description: desc
      })
    });

    RevoltUI.toast(`Product "${title}" published successfully!`, 'success');
    closeAddProductModal();
  } catch (err) {
    console.error(err);
  }
}

document.addEventListener('DOMContentLoaded', initAdmin);
