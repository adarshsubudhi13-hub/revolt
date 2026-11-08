/**
 * Authentication and OTP Verification
 */

let pendingOtpIdentifier = '';

function switchAuthTab(tab) {
  const loginForm = document.getElementById('form-login');
  const regForm = document.getElementById('form-register');
  const loginBtn = document.getElementById('tab-login-btn');
  const regBtn = document.getElementById('tab-register-btn');

  if (tab === 'login') {
    loginForm.style.display = 'block';
    regForm.style.display = 'none';
    loginBtn.style.borderBottom = '2px solid var(--primary)';
    regBtn.style.borderBottom = 'none';
  } else {
    loginForm.style.display = 'none';
    regForm.style.display = 'block';
    regBtn.style.borderBottom = '2px solid var(--primary)';
    loginBtn.style.borderBottom = 'none';
  }
}

async function quickLogin(identifier, password) {
  try {
    const res = await Revolt.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ identifier, password })
    });

    Revolt.setAuth(res.token, res);
    RevoltUI.toast(`Welcome back, ${res.fullName}!`, 'success');

    redirectAfterAuth(res.role);
  } catch (err) {
    console.error(err);
  }
}

async function handleLoginSubmit(event) {
  event.preventDefault();
  const identifier = document.getElementById('login-identifier').value.trim();
  const password = document.getElementById('login-password').value;

  try {
    const res = await Revolt.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ identifier, password })
    });

    Revolt.setAuth(res.token, res);
    RevoltUI.toast(`Welcome, ${res.fullName}!`, 'success');

    redirectAfterAuth(res.role);
  } catch (err) {
    console.error(err);
  }
}

async function handleRegisterSubmit(event) {
  event.preventDefault();
  const fullName = document.getElementById('reg-name').value.trim();
  const email = document.getElementById('reg-email').value.trim();
  const phoneNumber = document.getElementById('reg-phone').value.trim();
  const password = document.getElementById('reg-password').value;

  try {
    await Revolt.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ fullName, email, phoneNumber, password })
    });

    pendingOtpIdentifier = email;
    document.getElementById('otp-target-email').innerText = email;
    document.getElementById('otp-modal').classList.add('active');

    RevoltUI.toast('Verification OTP sent to your email & phone!', 'success');
  } catch (err) {
    console.error(err);
  }
}

async function submitOtpVerification() {
  const otpCode = document.getElementById('otp-code-input').value.trim();
  if (!otpCode) {
    RevoltUI.toast('Please enter the 6-digit OTP code', 'warning');
    return;
  }

  try {
    const res = await Revolt.request('/auth/verify-otp', {
      method: 'POST',
      body: JSON.stringify({
        identifier: pendingOtpIdentifier,
        otpCode: otpCode,
        type: 'EMAIL_VERIFICATION'
      })
    });

    Revolt.setAuth(res.token, res);
    document.getElementById('otp-modal').classList.remove('active');
    RevoltUI.toast('Account verified and logged in successfully!', 'success');

    redirectAfterAuth(res.role);
  } catch (err) {
    console.error(err);
  }
}

async function resendOtp() {
  try {
    await Revolt.request('/auth/resend-otp', {
      method: 'POST',
      body: JSON.stringify({
        identifier: pendingOtpIdentifier,
        type: 'EMAIL_VERIFICATION'
      })
    });
    RevoltUI.toast('A new OTP has been dispatched', 'info');
  } catch (err) {
    console.error(err);
  }
}

function redirectAfterAuth(role) {
  const params = new URLSearchParams(window.location.search);
  const redirect = params.get('redirect');

  if (redirect) {
    window.location.href = `/${redirect}`;
    return;
  }

  if (role === 'AGENT') {
    window.location.href = '/agent.html';
  } else if (role === 'ADMIN') {
    window.location.href = '/admin.html';
  } else {
    window.location.href = '/shop.html';
  }
}
