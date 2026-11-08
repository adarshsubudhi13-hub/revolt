/**
 * Trade-In Wizard & Valuation Logic - Expanded Vast Catalog
 */

const uploadedPhotos = {};
let currentValuation = null;

const MODEL_DATA = {
  Smartphones: {
    Apple: ["iPhone 16 Pro Max", "iPhone 16", "iPhone 15 Pro Max", "iPhone 15 Pro", "iPhone 15", "iPhone 14 Pro Max", "iPhone 14", "iPhone 13 Pro", "iPhone 13", "iPhone 12", "iPhone 11"],
    Samsung: ["Galaxy S24 Ultra", "Galaxy S24+", "Galaxy S24", "Galaxy Z Fold 5", "Galaxy Z Flip 5", "Galaxy S23 Ultra", "Galaxy S23", "Galaxy S23 FE", "Galaxy A55", "Galaxy A54"],
    OnePlus: ["OnePlus 12", "OnePlus 12R", "OnePlus Open", "OnePlus 11", "OnePlus 11R"],
    Google: ["Pixel 9 Pro", "Pixel 9", "Pixel 8 Pro", "Pixel 8", "Pixel 8a", "Pixel 7a"],
    Xiaomi: ["Xiaomi 14 Ultra", "Xiaomi 14"]
  },
  Laptops: {
    Apple: ["MacBook Pro 16 M3 Max", "MacBook Pro 14 M3 Pro", "MacBook Air 15 M2", "MacBook Air 13 M2", "MacBook Air M1"],
    Dell: ["XPS 15", "XPS 13 Plus", "Inspiron 16"],
    Lenovo: ["ThinkPad X1 Carbon", "Legion Pro 7i", "Yoga 9i"],
    HP: ["Spectre x360", "Omen 16"],
    ASUS: ["ROG Zephyrus G16", "ZenBook Duo"]
  },
  Tablets: {
    Apple: ["iPad Pro 13 M4", "iPad Pro 11 M4", "iPad Air 11 M2", "iPad 10th Gen", "iPad Mini 6"],
    Samsung: ["Galaxy Tab S9 Ultra", "Galaxy Tab S9+", "Galaxy Tab S9"],
    OnePlus: ["OnePlus Pad 2"]
  },
  Audio: {
    Sony: ["WH-1000XM5", "WH-1000XM4"],
    Bose: ["QuietComfort Ultra"],
    Apple: ["AirPods Max", "AirPods Pro 2"],
    Sennheiser: ["Momentum 4"],
    Marshall: ["Stanmore III"]
  },
  Wearables: {
    Apple: ["Watch Ultra 2", "Watch Ultra", "Watch Series 9", "Watch Series 8"],
    Samsung: ["Galaxy Watch 6 Classic", "Galaxy Watch 6"],
    Garmin: ["Fenix 7 Pro"]
  },
  Gaming: {
    Sony: ["PlayStation 5 Slim", "PlayStation 5"],
    Microsoft: ["Xbox Series X"],
    Nintendo: ["Switch OLED"],
    Valve: ["Steam Deck OLED"],
    ASUS: ["ROG Ally X"]
  },
  Cameras: {
    Sony: ["A7 IV"],
    Canon: ["EOS R6 Mark II"],
    DJI: ["Mini 4 Pro"],
    GoPro: ["Hero 12 Black"]
  }
};

function onCategoryChanged() {
  const cat = document.getElementById('tradein-category').value;
  const brandSelect = document.getElementById('tradein-brand');
  
  const brands = Object.keys(MODEL_DATA[cat] || {});
  brandSelect.innerHTML = brands.map(b => `<option value="${b}">${b}</option>`).join('');
  
  updateBrandModels();
}

function updateBrandModels() {
  const cat = document.getElementById('tradein-category').value;
  const brand = document.getElementById('tradein-brand').value;
  const modelSelect = document.getElementById('tradein-model');

  const models = (MODEL_DATA[cat] && MODEL_DATA[cat][brand]) || ["Standard Model"];
  modelSelect.innerHTML = models.map(m => `<option value="${m}">${m}</option>`).join('');
}

function goToStep(stepNum) {
  for (let i = 1; i <= 5; i++) {
    const el = document.getElementById(`step-${i}`);
    const tab = document.getElementById(`step-tab-${i}`);
    if (el) el.style.display = i === stepNum ? 'block' : 'none';
    if (tab) {
      if (i === stepNum) {
        tab.className = 'wizard-step active';
      } else if (i < stepNum) {
        tab.className = 'wizard-step completed';
      } else {
        tab.className = 'wizard-step';
      }
    }
  }
  window.scrollTo({ top: 100, behavior: 'smooth' });
}

async function uploadPhoto(type, inputEl) {
  const file = inputEl.files[0];
  if (!file) return;

  const previewEl = document.getElementById(`preview-${type.toLowerCase().replace('_', '')}`);
  previewEl.innerHTML = `<span style="color:var(--accent);">⏳ Uploading...</span>`;

  const formData = new FormData();
  formData.append('file', file);

  try {
    const res = await Revolt.request('/upload', {
      method: 'POST',
      body: formData
    });

    uploadedPhotos[type] = res.fileUrl;
    previewEl.innerHTML = `
      <div style="margin-top:6px;">
        <img src="${res.fileUrl}" style="width:70px; height:70px; object-fit:cover; border-radius:6px; border:1px solid var(--border-color); display:block; margin:0 auto;">
        <span style="color:var(--success); font-weight:600; font-size:0.75rem;">✓ Uploaded</span>
      </div>
    `;
    RevoltUI.toast(`${type} photo uploaded successfully`, 'success');
  } catch (err) {
    previewEl.innerHTML = `<span style="color:var(--danger);">Upload failed</span>`;
  }
}

function getCalculatedGrade() {
  const screen = document.getElementById('cond-screen').value;
  const body = document.getElementById('cond-body').value;
  const battery = document.getElementById('cond-battery').value;

  if (screen === 'CRACKED') return 'GRADE_D';
  if (screen === 'MINOR' || body === 'DENTED' || battery === 'SERVICED') return 'GRADE_C';
  if (screen === 'FLAWLESS' && body === 'FLAWLESS' && battery === 'EXCELLENT') return 'GRADE_A';
  return 'GRADE_B';
}

async function calculateFinalQuote() {
  const brand = document.getElementById('tradein-brand').value;
  const model = document.getElementById('tradein-model').value;
  const ageMonths = parseInt(document.getElementById('tradein-age').value);
  const conditionGrade = getCalculatedGrade();
  const box = document.getElementById('acc-box').checked;
  const charger = document.getElementById('acc-charger').checked;
  const bill = document.getElementById('acc-bill').checked;

  try {
    const res = await Revolt.request('/tradein/calculate-valuation', {
      method: 'POST',
      body: JSON.stringify({
        brand,
        model,
        ageMonths,
        conditionGrade,
        hasOriginalBox: box,
        hasOriginalCharger: charger,
        hasValidBill: bill
      })
    });

    currentValuation = res;

    document.getElementById('quote-device-name').innerText = `${brand} ${model}`;
    document.getElementById('quote-grade-badge').innerText = conditionGrade.replace('_', ' ');
    document.getElementById('quote-base-val').innerText = Revolt.formatRupees(res.basePrice);
    document.getElementById('quote-multipliers').innerText = `Age: -${res.ageDiscountPercent.toFixed(0)}% | Condition: x${res.conditionMultiplier}`;
    document.getElementById('quote-acc-bonus').innerText = `+${Revolt.formatRupees(res.bonusPointsAccessories)} (${res.bonusPointsAccessories} pts)`;
    document.getElementById('quote-final-amount').innerText = Revolt.formatRupees(res.estimatedRupees);
    document.getElementById('quote-final-points').innerText = res.estimatedPoints.toLocaleString();

    goToStep(5);
  } catch (e) {
    console.error(e);
  }
}

async function submitFinalListing() {
  if (!Revolt.isLoggedIn()) {
    RevoltUI.toast('Please log in or register to lock in your points quote', 'warning');
    setTimeout(() => {
      window.location.href = '/login.html?redirect=tradein.html';
    }, 1200);
    return;
  }

  const category = document.getElementById('tradein-category').value;
  const brand = document.getElementById('tradein-brand').value;
  const model = document.getElementById('tradein-model').value;
  const variant = document.getElementById('tradein-variant').value;
  const imei = document.getElementById('tradein-imei').value.trim() || "IMEI-DEMO-" + Math.floor(100000 + Math.random() * 900000);
  const ageMonths = parseInt(document.getElementById('tradein-age').value);
  const conditionGrade = getCalculatedGrade();
  const box = document.getElementById('acc-box').checked;
  const charger = document.getElementById('acc-charger').checked;
  const bill = document.getElementById('acc-bill').checked;

  try {
    const res = await Revolt.request('/tradein/submit-listing', {
      method: 'POST',
      body: JSON.stringify({
        category,
        brand,
        model,
        specsJson: JSON.stringify({ variant }),
        imei,
        ageMonths,
        conditionGrade,
        hasOriginalBox: box,
        hasOriginalCharger: charger,
        hasValidBill: bill,
        photoUrls: uploadedPhotos
      })
    });

    // Update session user points
    const u = Revolt.getUser();
    if (u) {
      u.availablePoints = (u.availablePoints || 0) + res.estimatedPoints;
      localStorage.setItem('revolt_user', JSON.stringify(u));
    }

    RevoltUI.toast(`Success! ${res.estimatedPoints.toLocaleString()} points credited to your wallet with 7-day price lock!`, 'success');

    setTimeout(() => {
      window.location.href = '/shop.html';
    }, 1500);

  } catch (err) {
    console.error(err);
  }
}

document.addEventListener('DOMContentLoaded', () => {
  onCategoryChanged();
});
