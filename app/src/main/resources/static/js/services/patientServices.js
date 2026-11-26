function getAuthHeaders() {
    const token = localStorage.getItem('token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }
    return headers;
}

async function handleResponse(response) {
    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
        let message = data.message || data.error || `Request failed (${response.status})`;
        if (data.details && typeof data.details === 'object') {
            const fieldMessages = Object.values(data.details).join('; ');
            if (fieldMessages) {
                message = `${message}: ${fieldMessages}`;
            }
        }
        throw new Error(message);
    }
    return data;
}

export async function loginUser(role, identifier, password) {
    const endpointMap = {
        ADMIN: '/api/admin/login',
        DOCTOR: '/api/doctor/login',
        PATIENT: '/api/patient/login'
    };
    const url = endpointMap[role.toUpperCase()];
    if (!url) {
        throw new Error(`Unsupported role: ${role}`);
    }
    const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ identifier, password })
    });
    return handleResponse(response);
}

export async function registerPatient(patientData) {
    const response = await fetch('/api/patient/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(patientData)
    });
    return handleResponse(response);
}

export async function getCurrentPatient() {
    const response = await fetch('/api/patients/me', {
        method: 'GET',
        headers: getAuthHeaders()
    });
    return handleResponse(response);
}

export async function getPatientAppointments(patientId) {
    const response = await fetch(`/api/appointments/patient/${patientId}`, {
        method: 'GET',
        headers: getAuthHeaders()
    });
    return handleResponse(response);
}

export async function getAppointmentById(appointmentId) {
    const response = await fetch(`/api/appointments/${appointmentId}`, {
        method: 'GET',
        headers: getAuthHeaders()
    });
    return handleResponse(response);
}

export async function bookAppointment(payload) {
    const response = await fetch('/api/appointments', {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(payload)
    });
    return handleResponse(response);
}

export async function updateAppointment(appointmentId, payload) {
    const response = await fetch(`/api/appointments/${appointmentId}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(payload)
    });
    return handleResponse(response);
}

export async function cancelAppointment(appointmentId) {
    const response = await fetch(`/api/appointments/${appointmentId}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    });
    return handleResponse(response);
}

export async function getPrescriptionsByAppointment(appointmentId) {
    const response = await fetch(`/api/prescriptions/appointment/${appointmentId}`, {
        method: 'GET',
        headers: getAuthHeaders()
    });
    return handleResponse(response);
}
