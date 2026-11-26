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
        const message = data.message || data.error || `Request failed (${response.status})`;
        throw new Error(message);
    }
    return data;
}

export async function getAllDoctors() {
    const response = await fetch('/api/doctors', {
        method: 'GET',
        headers: getAuthHeaders()
    });
    return handleResponse(response);
}

export async function filterDoctors({ name = '', time = '', specialty = '' } = {}) {
    const params = new URLSearchParams();
    if (name.trim()) {
        params.set('name', name.trim());
    }
    if (time.trim()) {
        params.set('time', time.trim());
    }
    if (specialty.trim()) {
        params.set('specialty', specialty.trim());
    }
    const query = params.toString();
    const url = query ? `/api/doctors/filter?${query}` : '/api/doctors';
    const response = await fetch(url, {
        method: 'GET',
        headers: getAuthHeaders()
    });
    return handleResponse(response);
}

export async function createDoctor(doctorData) {
    const response = await fetch('/api/admin/doctors', {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(doctorData)
    });
    return handleResponse(response);
}

export async function deleteDoctor(doctorId) {
    const response = await fetch(`/api/admin/doctors/${doctorId}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    });
    return handleResponse(response);
}

export async function getDoctorAvailability(doctorId, date) {
    const params = new URLSearchParams({ date });
    const response = await fetch(`/api/doctors/${doctorId}/availability?${params.toString()}`, {
        method: 'GET',
        headers: getAuthHeaders()
    });
    return handleResponse(response);
}

export async function getDoctorAppointments(doctorId, date = '', patientName = '') {
    const params = new URLSearchParams();
    if (date) {
        params.set('date', date);
    }
    if (patientName.trim()) {
        params.set('patientName', patientName.trim());
    }
    const query = params.toString();
    const url = query
        ? `/api/appointments/doctor/${doctorId}?${query}`
        : `/api/appointments/doctor/${doctorId}`;
    const response = await fetch(url, {
        method: 'GET',
        headers: getAuthHeaders()
    });
    return handleResponse(response);
}

export async function createPrescription(prescriptionData) {
    const response = await fetch('/api/prescriptions', {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(prescriptionData)
    });
    return handleResponse(response);
}

export async function getPrescriptionsByPatientName(patientName) {
    const response = await fetch(`/api/prescriptions/patient/${encodeURIComponent(patientName)}`, {
        method: 'GET',
        headers: getAuthHeaders()
    });
    return handleResponse(response);
}
