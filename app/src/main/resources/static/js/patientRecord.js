import { renderHeader, escapeHtml } from './components/header.js';
import { openModal, bindModalCloseHandlers, showAlert } from './components/modal.js';
import { getPatientAppointments, getPrescriptionsByAppointment } from './services/patientServices.js';

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

function getStatusClass(status) {
    if (status === 1) {
        return 'status-completed';
    }
    if (status === 2) {
        return 'status-cancelled';
    }
    return 'status-scheduled';
}

async function openPrescriptionModal(appointmentId) {
    const content = document.getElementById('rxDetailContent');
    content.innerHTML = '<p>Loading prescription details...</p>';
    openModal('rxDetailModal');

    try {
        const prescriptions = await getPrescriptionsByAppointment(appointmentId);
        if (!prescriptions.length) {
            content.innerHTML = `<p>No prescription recorded for Appointment #${escapeHtml(appointmentId)}.</p>`;
            return;
        }
        content.innerHTML = prescriptions
            .map((rx) => `
                <div class="prescription-item">
                    <h4>${escapeHtml(rx.medication)}</h4>
                    <p><strong>Dosage:</strong> ${escapeHtml(rx.dosage)}</p>
                    <p><strong>Doctor Notes:</strong> ${escapeHtml(rx.doctorNotes || 'None')}</p>
                </div>
            `)
            .join('');
    } catch (error) {
        content.innerHTML = `<p class="alert alert-error show">${escapeHtml(error.message)}</p>`;
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    const patientId = ensurePatientSession();
    if (!patientId) {
        return;
    }

    renderHeader();
    bindModalCloseHandlers('rxDetailModal');

    const tbody = document.getElementById('patientRecordBody');
    try {
        const appointments = await getPatientAppointments(patientId);
        tbody.innerHTML = '';

        if (!appointments.length) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" style="text-align: center; padding: 2rem;">
                        No medical records or appointments found.
                    </td>
                </tr>
            `;
            return;
        }

        appointments.forEach((appt) => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${escapeHtml(appt.appointmentDate)} ${escapeHtml(appt.appointmentTimeOnly)}</td>
                <td>#${escapeHtml(appt.id)}</td>
                <td>#${escapeHtml(appt.patientId)}</td>
                <td>${escapeHtml(appt.doctorName)} (${escapeHtml(appt.doctorSpecialty)})</td>
                <td>
                    <span class="status-badge ${getStatusClass(appt.status)}">
                        ${escapeHtml(appt.statusLabel)}
                    </span>
                </td>
                <td>
                    <button type="button" class="btn btn-slate btn-sm view-rx-detail-btn">
                        View Prescription
                    </button>
                </td>
            `;

            row.querySelector('.view-rx-detail-btn').addEventListener('click', () => {
                openPrescriptionModal(appt.id);
            });

            tbody.appendChild(row);
        });
    } catch (error) {
        showAlert('recordAlert', error.message, 'error');
    }
});
