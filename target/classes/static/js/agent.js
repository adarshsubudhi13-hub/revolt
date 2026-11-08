/**
 * Agent Portal & Doorstep Inspection Logic
 */

let currentOrderId = null;
let currentInspectionData = null;
let agentProofPhotos = [];

async function initAgentPortal() {
  const user = Revolt.getUser();
  if (!user || (user.role !== 'AGENT' && user.role !== 'ADMIN')) {
    RevoltUI.toast('Access restricted. Please log in as an Agent or Admin.', 'warning');
    setTimeout(() => {
      window.location.href = '/login.html?redirect=agent.html';
    }, 1200);
    return;
  }

  document.getElementById('agent-profile-pill').innerText = `Logged in: ${user.fullName} (${user.role})`;
  loadAssignedOrders();
}

async function loadAssignedOrders() {
  try {
    const orders = await Revolt.request('/agent/assigned-orders');
    renderAssignedOrders(orders);
  } catch (err) {
    console.error(err);
  }
}

function renderAssignedOrders(orders) {
  const container = document.getElementById('assigned-orders-container');
  if (orders.length === 0) {
    container.innerHTML = `
      <div class="card" style="grid-column: 1/-1; text-align:center; padding:40px;">
        <h3>No assigned delivery runs</h3>
        <p style="color:var(--text-muted);">You currently have zero pending doorstep deliveries.</p>
      </div>
    `;
    return;
  }

  container.innerHTML = orders.map(o => {
    let statusClass = 'badge-info';
    if (o.orderStatus === 'DELIVERED') statusClass = 'badge-success';
    if (o.orderStatus === 'OUT_FOR_DELIVERY') statusClass = 'badge-warning';

    const hasTradeIn = o.tradeInDeviceId != null;

    return `
      <div class="card">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
          <b>#${o.orderNumber}</b>
          <span class="badge ${statusClass}">${o.orderStatus.replace('_', ' ')}</span>
        </div>

        <h4 style="font-size:1.05rem; margin-bottom:6px;">${o.productTitle}</h4>
        <div style="font-size:0.85rem; color:var(--text-muted); margin-bottom:14px;">
          Cash to Collect: <b style="color:var(--text-main); font-size:1rem;">${Revolt.formatRupees(o.cashPaid)}</b>
        </div>

        <div style="background:var(--bg-main); padding:10px; border-radius:var(--radius-sm); margin-bottom:14px; font-size:0.82rem;">
          <b>Trade-In Status:</b> ${hasTradeIn ? `📱 Device #${o.tradeInDeviceId} (${o.pointsRedeemed.toLocaleString()} pts held)` : 'None'}
        </div>

        ${o.orderStatus !== 'DELIVERED' && o.orderStatus !== 'CANCELLED' ? `
          <button class="btn btn-primary btn-block" onclick="startInspection(${o.id})">
            Start Doorstep Inspection & Delivery &rarr;
          </button>
        ` : `
          <div style="text-align:center; font-size:0.85rem; color:var(--success); font-weight:600;">
            ✓ Completed on ${o.deliveredAt ? o.deliveredAt.substring(0, 10) : 'Today'}
          </div>
        `}
      </div>
    `;
  }).join('');
}

async function startInspection(orderId) {
  try {
    const data = await Revolt.request(`/agent/inspection-details/${orderId}`);
    currentOrderId = orderId;
    currentInspectionData = data;
    agentProofPhotos = [];

    document.getElementById('inspect-order-number').innerText = `#${data.orderNumber}`;
    document.getElementById('inspect-customer-name').innerText = data.customerName;
    document.getElementById('inspect-customer-phone').innerText = data.customerPhone;
    
    // Address parse
    let addrText = data.shippingAddress;
    try {
      const a = JSON.parse(data.shippingAddress);
      addrText = `${a.address}, ${a.city}, ${a.state} - ${a.pincode}`;
    } catch(e) {}
    document.getElementById('inspect-address').innerText = addrText;

    document.getElementById('inspect-product-title').innerText = data.productName;
    document.getElementById('inspect-cash-due').innerText = Revolt.formatRupees(data.cashToCollect);

    if (data.tradeInDeviceId) {
      document.getElementById('inspect-declared-device').innerText = `${data.deviceBrand} ${data.deviceModel}`;
      document.getElementById('inspect-declared-imei').innerText = data.declaredImei;
      document.getElementById('inspect-declared-grade').innerText = data.declaredGrade ? data.declaredGrade.replace('_', ' ') : 'N/A';
    } else {
      document.getElementById('inspect-declared-device').innerText = "None (No Trade-In Attached)";
      document.getElementById('inspect-declared-imei').innerText = "N/A";
    }

    document.getElementById('inspect-photo-preview').innerHTML = '';
    document.getElementById('inspect-handover-otp').value = '';
    document.getElementById('inspect-notes').value = '';

    document.getElementById('agent-orders-view').style.display = 'none';
    document.getElementById('inspection-view').style.display = 'block';

    updateInspectionOutcome();
    window.scrollTo({ top: 0, behavior: 'smooth' });

  } catch (err) {
    console.error(err);
  }
}

function closeInspection() {
  document.getElementById('inspection-view').style.display = 'none';
  document.getElementById('agent-orders-view').style.display = 'block';
  loadAssignedOrders();
}

function updateInspectionOutcome() {
  const imeiVal = document.querySelector('input[name="chk-imei"]:checked').value;
  const actualGrade = document.getElementById('inspect-actual-grade').value;
  const declaredGrade = currentInspectionData && currentInspectionData.declaredGrade;

  const isImeiMatch = imeiVal === 'MATCH';
  const isGradeMatch = !declaredGrade || (actualGrade === declaredGrade || actualGrade === 'GRADE_A');

  const isApproved = isImeiMatch && isGradeMatch;
  const mismatchBox = document.getElementById('mismatch-fields');
  const btn = document.getElementById('btn-submit-inspection');

  if (isApproved) {
    mismatchBox.style.display = 'none';
    btn.className = 'btn btn-primary btn-lg';
    btn.innerText = '✓ Trade-In Matches! Finalize Points & Complete Delivery';
  } else {
    mismatchBox.style.display = 'block';
    btn.className = 'btn btn-danger btn-lg';
    btn.innerText = '⚠️ Record Discrepancy, Collect Difference & Deliver';

    // Auto calculate extra cash
    if (currentInspectionData && currentInspectionData.declaredPoints) {
      document.getElementById('inspect-extra-cash').value = currentInspectionData.declaredPoints;
    }
  }
}

async function uploadAgentProof(inputEl) {
  const file = inputEl.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append('file', file);

  try {
    const res = await Revolt.request('/upload', {
      method: 'POST',
      body: formData
    });

    agentProofPhotos.push(res.fileUrl);
    const previewContainer = document.getElementById('inspect-photo-preview');
    previewContainer.innerHTML += `
      <img src="${res.fileUrl}" style="width:70px; height:70px; object-fit:cover; border-radius:6px; border:2px solid var(--primary);">
    `;
    RevoltUI.toast('Inspection proof photo captured', 'success');
  } catch (e) {
    console.error(e);
  }
}

async function submitInspectionReport() {
  const enteredOtp = document.getElementById('inspect-handover-otp').value.trim();
  if (!enteredOtp) {
    RevoltUI.toast('Customer Secret Handover OTP is required to complete delivery!', 'warning');
    return;
  }

  const imeiChoice = document.querySelector('input[name="chk-imei"]:checked').value;
  const actualGrade = document.getElementById('inspect-actual-grade').value;
  const mismatchReason = document.getElementById('inspect-mismatch-reason').value;
  const extraCash = parseFloat(document.getElementById('inspect-extra-cash').value) || 0.0;
  const notes = document.getElementById('inspect-notes').value.trim();

  const isApproved = imeiChoice === 'MATCH' && (!currentInspectionData.declaredGrade || actualGrade === currentInspectionData.declaredGrade || actualGrade === 'GRADE_A');

  try {
    const res = await Revolt.request('/agent/submit-inspection', {
      method: 'POST',
      body: JSON.stringify({
        orderId: currentOrderId,
        enteredHandoverOtp: enteredOtp,
        imeiMatched: imeiChoice === 'MATCH',
        physicalGrade: actualGrade,
        isApproved: isApproved,
        mismatchReason: isApproved ? null : mismatchReason,
        additionalCashCollected: isApproved ? 0.0 : extraCash,
        agentNotes: notes,
        agentPhotoUrls: agentProofPhotos
      })
    });

    RevoltUI.toast(res.message, 'success');
    closeInspection();

  } catch (err) {
    console.error(err);
  }
}

document.addEventListener('DOMContentLoaded', initAgentPortal);
