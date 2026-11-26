import { renderHeader, escapeHtml } from './components/header.js';
import { createDoctorCard } from './components/doctorCard.js';
import { openModal, closeModal, bindModalCloseHandlers, showAlert, clearAlert } from './components/modal.js';
import { filterDoctors, getDoctorAvailability } from './services/doctorServices.js';
import {
    getPatientAppointments,
    bookAppointment,
    cancelAppointment
} from './services/patientServices.js';

function ensurePatientSession() {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('userRole');
    const userId = localStorage.getItem('userId');
    if (!token || role !== 'PATIENT' || !userId) {
        window.location.href = '/';
        return null;
    }
    return Number(userId);
}

export function slotToIsoDateTime(dateStr, slotStr) {
    const startPart = slotStr.split('-')[0].trim();
    const match = startPart.match(/^(\d{1,2}):(\d{2})\s*(AM|PM)$/i);
    if (!match) {
        throw new Error(`Invalid time slot format: ${slotStr}`);
    }
    let hours = parseInt(match[1], 10);
    const minutes = match[2];
    const period = match[3].toUpperCase();
    if (period === 'PM' && hours !== 12) {
        hours += 12;
    } else if (period === 'AM' && hours === 12) {
        hours = 0;
    }
    const hh = String(hours).padStart(2, '0');
    return `${dateStr}T${hh}:${minutes}:00`;
}

function getStatusClass(status) {
    if (status === 1) {
        return 'status-completed';
    }
    if (status === 2) {
        return 'status-cancelled';
    }
    return 'status-scheduled';
}

async function loadAvailabilityForDate(doctorId, date) {
    const slotSelect = document.getElementById('bookSlot');
    slotSelect.innerHTML = '<option value="">Loading slots...</option>';
    try {
        const slots = await getDoctorAvailability(doctorId, date);
        if (!slots.length) {
            slotSelect.innerHTML = '<option value="">No available slots on this date</option>';
            return;
        }
        slotSelect.innerHTML = slots
            .map((slot) => `<option value="${escapeHtml(slot)}">${escapeHtml(slot)}</option>`)
            .join('');
    } catch (error) {
        slotSelect.innerHTML = '<option value="">Unable to load slots</option>';
        showAlert('bookAlert', error.message, 'error');
    }
}

async function loadDoctors() {
    const grid = document.getElementById('doctorGrid');
    const name = document.getElementById('searchDoctorInput')?.value || '';
    const time = document.getElementById('filterTimeSelect')?.value || '';
    const specialty = document.getElementById('filterSpecialtySelect')?.value || '';

    try {
        const doctors = await filterDoctors({ name, time, specialty });
        grid.innerHTML = '';

        if (!doctors.length) {
            grid.innerHTML = '<div class="empty-state">No doctors match your search criteria.</div>';
            return;
        }

        doctors.forEach((doctor) => {
            const card = createDoctorCard(doctor, {
                mode: 'patient',
                onBook: (doc) => {
                    clearAlert('bookAlert');
                    document.getElementById('bookDoctorId').value = doc.id;
                    document.getElementById('bookDoctorName').value = `${doc.name} (${doc.specialty})`;
                    const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];
                    const dateInput = document.getElementById('bookDate');
                    dateInput.min = tomorrow;
                    dateInput.value = tomorrow;
                    loadAvailabilityForDate(doc.id, tomorrow);
                    openModal('bookModal');
                }
            });
            grid.appendChild(card);
        });
    } catch (error) {
        showAlert('patientAlert', error.message, 'error');
    }
}

async function loadMyAppointments(patientId) {
    const tbody = document.getElementById('myAppointmentsBody');
    try {
        const appointments = await getPatientAppointments(patientId);
        tbody.innerHTML = '';

        if (!appointments.length) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" style="text-align: center; padding: 2rem;">
                        You have no appointments yet.
                    </td>
                </tr>
            `;
            return;
        }

        appointments.forEach((appt) => {
            const isScheduled = appt.status === 0;
            const actionsHtml = isScheduled
                ? `
                    <a href="/pages/updateAppointment.html?id=${encodeURIComponent(appt.id)}"
                       class="btn btn-slate btn-sm">Reschedule</a>
                    <button type="button" class="btn btn-danger btn-sm cancel-appt-btn">Cancel</button>
                  `
                : '<span style="color: var(--text-muted); font-size: 0.85rem;">No actions</span>';

            const row = document.createElement('tr');
            row.innerHTML = `
                <td>#${escapeHtml(appt.id)}</td>
                <td>${escapeHtml(appt.doctorName)}</td>
                <td>${escapeHtml(appt.doctorSpecialty)}</td>
                <td>${escapeHtml(appt.appointmentDate)}</td>
                <td>${escapeHtml(appt.appointmentTimeOnly)}</td>
                <td>
                    <span class="status-badge ${getStatusClass(appt.status)}">
                        ${escapeHtml(appt.statusLabel)}
                    </span>
                </td>
                <td><div class="card-actions" style="margin-top: 0;">${actionsHtml}</div></td>
            `;

            const cancelBtn = row.querySelector('.cancel-appt-btn');
            cancelBtn?.addEventListener('click', async () => {
                if (!confirm(`Cancel appointment #${appt.id}?`)) {
                    return;
                }
                try {
                    await cancelAppointment(appt.id);
                    showAlert('patientAlert', `Appointment #${appt.id} cancelled.`, 'success');
                    await loadMyAppointments(patientId);
                } catch (error) {
                    showAlert('patientAlert', error.message, 'error');
                }
            });

            tbody.appendChild(row);
        });
    } catch (error) {
        showAlert('patientAlert', error.message, 'error');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const patientId = ensurePatientSession();
    if (!patientId) {
        return;
    }

    renderHeader();
    bindModalCloseHandlers('bookModal');

    const searchInput = document.getElementById('searchDoctorInput');
    const timeSelect = document.getElementById('filterTimeSelect');
    const specialtySelect = document.getElementById('filterSpecialtySelect');
    const resetBtn = document.getElementById('resetFiltersBtn');
    const bookDateInput = document.getElementById('bookDate');
    const bookForm = document.getElementById('bookForm');

    searchInput?.addEventListener('input', loadDoctors);
    timeSelect?.addEventListener('change', loadDoctors);
    specialtySelect?.addEventListener('change', loadDoctors);

    resetBtn?.addEventListener('click', () => {
        searchInput.value = '';
        timeSelect.value = '';
        specialtySelect.value = '';
        loadDoctors();
    });

    bookDateInput?.addEventListener('change', () => {
        const doctorId = document.getElementById('bookDoctorId').value;
        if (doctorId && bookDateInput.value) {
            loadAvailabilityForDate(doctorId, bookDateInput.value);
        }
    });

    bookForm?.addEventListener('submit', async (event) => {
        event.preventDefault();
        clearAlert('bookAlert');

        const doctorId = Number(document.getElementById('bookDoctorId').value);
        const dateStr = bookDateInput.value;
        const slotStr = document.getElementById('bookSlot').value;
        if (!slotStr) {
            showAlert('bookAlert', 'Please select an available time slot.', 'error');
            return;
        }

        try {
            const appointmentTime = slotToIsoDateTime(dateStr, slotStr);
            await bookAppointment({ doctorId, patientId, appointmentTime });
            closeModal('bookModal');
            showAlert('patientAlert', 'Appointment booked successfully!', 'success');
            await loadMyAppointments(patientId);
        } catch (error) {
            showAlert('bookAlert', error.message, 'error');
        }
    });

    loadDoctors();
    loadMyAppointments(patientId);
});
