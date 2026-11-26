export function escapeHtml(value) {
    if (value === null || value === undefined) {
        return '';
    }
    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

export function renderHeader(containerId = 'appHeader') {
    const container = document.getElementById(containerId);
    if (!container) {
        return;
    }

    const role = localStorage.getItem('userRole') || '';
    const userName = localStorage.getItem('userName') || role;
    const token = localStorage.getItem('token');

    let navLinks = '';
    if (token && role === 'ADMIN') {
        navLinks = `
            <a class="nav-link" href="/adminDashboard/${encodeURIComponent(token)}">Admin Dashboard</a>
        `;
    } else if (token && role === 'DOCTOR') {
        navLinks = `
            <a class="nav-link" href="/doctorDashboard/${encodeURIComponent(token)}">Doctor Schedule</a>
        `;
    } else if (token && role === 'PATIENT') {
        navLinks = `
            <a class="nav-link" href="/pages/patientDashboard.html">Find Doctors</a>
            <a class="nav-link" href="/pages/patientRecord.html">My Records</a>
        `;
    }

    const authSection = token
        ? `
            <span class="user-badge">${escapeHtml(role)}: ${escapeHtml(userName)}</span>
            <button type="button" id="logoutBtn" class="btn btn-danger btn-sm">Logout</button>
          `
        : '<a class="nav-link" href="/">Home</a>';

    container.innerHTML = `
        <header class="app-header">
            <a href="/" class="brand-title" style="text-decoration: none;">
                SmartCare <span>Clinic</span>
            </a>
            <nav class="header-nav">
                ${navLinks}
                ${authSection}
            </nav>
        </header>
    `;

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            localStorage.removeItem('token');
            localStorage.removeItem('userRole');
            localStorage.removeItem('userId');
            localStorage.removeItem('userName');
            window.location.href = '/';
        });
    }
}
