import { renderHeader } from './components/header.js';
import { openModal, closeModal, bindModalCloseHandlers, showAlert, clearAlert } from './components/modal.js';
import { loginUser, registerPatient } from './services/patientServices.js';

function configureLoginModal(role) {
    const roleInput = document.getElementById('loginRole');
    const title = document.getElementById('loginModalTitle');
    const label = document.getElementById('identifierLabel');
    const identifierInput = document.getElementById('loginIdentifier');

    roleInput.value = role;
    if (role === 'ADMIN') {
        title.innerHTML = 'Admin <span>Login</span>';
        label.textContent = 'Username';
        identifierInput.type = 'text';
        identifierInput.placeholder = 'Enter admin username';
    } else if (role === 'DOCTOR') {
        title.innerHTML = 'Doctor <span>Login</span>';
        label.textContent = 'Email';
        identifierInput.type = 'email';
        identifierInput.placeholder = 'doctor@clinic.com';
    } else {
        title.innerHTML = 'Patient <span>Login</span>';
        label.textContent = 'Email';
        identifierInput.type = 'email';
        identifierInput.placeholder = 'patient@example.com';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    renderHeader();
    bindModalCloseHandlers('loginModal');
    bindModalCloseHandlers('signupModal');

    const roleButtons = document.querySelectorAll('.role-select-btn');
    const loginForm = document.getElementById('loginForm');
    const openSignupBtn = document.getElementById('openSignupBtn');
    const signupForm = document.getElementById('signupForm');

    roleButtons.forEach((btn) => {
        btn.addEventListener('click', () => {
            clearAlert('loginAlert');
            loginForm.reset();
            configureLoginModal(btn.dataset.role);
            openModal('loginModal');
        });
    });

    openSignupBtn?.addEventListener('click', () => {
        clearAlert('signupAlert');
        signupForm.reset();
        openModal('signupModal');
    });

    loginForm?.addEventListener('submit', async (event) => {
        event.preventDefault();
        clearAlert('loginAlert');

        const role = document.getElementById('loginRole').value;
        const identifier = document.getElementById('loginIdentifier').value.trim();
        const password = document.getElementById('loginPassword').value;

        try {
            const data = await loginUser(role, identifier, password);
            localStorage.setItem('token', data.token);
            localStorage.setItem('userRole', data.role);
            localStorage.setItem('userId', String(data.userId));
            localStorage.setItem('userName', data.name || data.username || identifier);

            if (data.role === 'ADMIN') {
                window.location.href = `/adminDashboard/${encodeURIComponent(data.token)}`;
            } else if (data.role === 'DOCTOR') {
                window.location.href = `/doctorDashboard/${encodeURIComponent(data.token)}`;
            } else {
                window.location.href = '/pages/patientDashboard.html';
            }
        } catch (error) {
            showAlert('loginAlert', error.message, 'error');
        }
    });

    signupForm?.addEventListener('submit', async (event) => {
        event.preventDefault();
        clearAlert('signupAlert');

        const payload = {
            name: document.getElementById('regName').value.trim(),
            email: document.getElementById('regEmail').value.trim(),
            phone: document.getElementById('regPhone').value.trim(),
            address: document.getElementById('regAddress').value.trim(),
            password: document.getElementById('regPassword').value
        };

        try {
            await registerPatient(payload);
            closeModal('signupModal');
            showAlert('homeAlert', 'Registration successful! Please log in as Patient.', 'success');
        } catch (error) {
            showAlert('signupAlert', error.message, 'error');
        }
    });
});
