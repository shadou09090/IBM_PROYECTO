import { renderHeader, escapeHtml } from './components/header.js';
import { openModal, closeModal, bindModalCloseHandlers, showAlert, clearAlert } from './components/modal.js';
import {
    getDoctorAppointments,
    createPrescription,
    getPrescriptionsByPatientName
} from './services/doctorServices.js';

function ensureDoctorSession() {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('userRole');
    const userId = localStorage.getItem('userId');
    if (!token || role !== 'DOCTOR' || !userId) {
        window.location.href = '/';
        return null;
    }
    return userId;
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

async function loadAppointments(doctorId) {
    const tbody = document.getElementById('appointmentsTableBody');
    const date = document.getElementById('appointmentDateFilter')?.value || '';
    const patientName = document.getElementById('patientSearchInput')?.value || '';

    try {
        const appointments = await getDoctorAppointments(doctorId, date, patientName);
        tbody.innerHTML = '';

        if (!appointments.length) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" style="text-align: center; padding: 2rem;">
                        No appointments found for the selected filter.
                    </td>
                </tr>
            `;
            return;
        }

        appointments.forEach((appt) => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>#${escapeHtml(appt.id)}</td>
                <td>${escapeHtml(appt.patientName)}</td>
                <td>${escapeHtml(appt.patientPhone)}</td>
                <td>${escapeHtml(appt.appointmentDate)}</td>
                <td>${escapeHtml(appt.appointmentTimeOnly)}</td>
                <td>
                    <span class="status-badge ${getStatusClass(appt.status)}">
                        ${escapeHtml(appt.statusLabel)}
                    </span>
                </td>
                <td>
                    <div class="card-actions" style="margin-top: 0;">
                        <button type="button" class="btn btn-primary btn-sm add-rx-btn">
                            Prescribe
                        </button>
                        <button type="button" class="btn btn-slate btn-sm view-rx-btn">
                            History
                        </button>
                    </div>
                </td>
            `;

            row.querySelector('.add-rx-btn').addEventListener('click', () => {
                clearAlert('prescriptionAlert');
                document.getElementById('prescriptionForm').reset();
                document.getElementById('rxAppointmentId').value = appt.id;
                document.getElementById('rxPatientName').value = appt.patientName;
                openModal('prescriptionModal');
            });

            row.querySelector('.view-rx-btn').addEventListener('click', async () => {
                await openPrescriptionHistory(appt.patientName);
            });

            tbody.appendChild(row);
        });
    } catch (error) {
        showAlert('doctorAlert', error.message, 'error');
    }
}

async function openPrescriptionHistory(patientName) {
    const container = document.getElementById('historyContent');
    container.innerHTML = '<p>Loading prescriptions...</p>';
    openModal('historyModal');

    try {
        const prescriptions = await getPrescriptionsByPatientName(patientName);
        if (!prescriptions.length) {
            container.innerHTML = `<p>No previous prescriptions found for ${escapeHtml(patientName)}.</p>`;
            return;
        }
        container.innerHTML = prescriptions
            .map((rx) => `
                <div class="prescription-item">
                    <h4>${escapeHtml(rx.medication)}</h4>
                    <p><strong>Dosage:</strong> ${escapeHtml(rx.dosage)}</p>
                    <p><strong>Appointment ID:</strong> #${escapeHtml(rx.appointmentId)}</p>
                    <p><strong>Notes:</strong> ${escapeHtml(rx.doctorNotes || 'None')}</p>
                </div>
            `)
            .join('');
    } catch (error) {
        container.innerHTML = `<p class="alert alert-error show">${escapeHtml(error.message)}</p>`;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const doctorId = ensureDoctorSession();
    if (!doctorId) {
        return;
    }

    renderHeader();
    bindModalCloseHandlers('prescriptionModal');
    bindModalCloseHandlers('historyModal');

    const dateInput = document.getElementById('appointmentDateFilter');
    const searchInput = document.getElementById('patientSearchInput');
    const todayBtn = document.getElementById('todayBtn');
    const clearBtn = document.getElementById('clearFiltersBtn');
    const rxForm = document.getElementById('prescriptionForm');

    dateInput?.addEventListener('change', () => loadAppointments(doctorId));
    searchInput?.addEventListener('input', () => loadAppointments(doctorId));

    todayBtn?.addEventListener('click', () => {
        const today = new Date().toISOString().split('T')[0];
        dateInput.value = today;
        loadAppointments(doctorId);
    });

    clearBtn?.addEventListener('click', () => {
        dateInput.value = '';
        searchInput.value = '';
        loadAppointments(doctorId);
    });

    rxForm?.addEventListener('submit', async (event) => {
        event.preventDefault();
        clearAlert('prescriptionAlert');

        const payload = {
            appointmentId: Number(document.getElementById('rxAppointmentId').value),
            patientName: document.getElementById('rxPatientName').value.trim(),
            medication: document.getElementById('rxMedication').value.trim(),
            dosage: document.getElementById('rxDosage').value.trim(),
            doctorNotes: document.getElementById('rxNotes').value.trim()
        };

        try {
            await createPrescription(payload);
            closeModal('prescriptionModal');
            showAlert('doctorAlert', `Prescription saved for ${payload.patientName}.`, 'success');
        } catch (error) {
            showAlert('prescriptionAlert', error.message, 'error');
        }
    });

    loadAppointments(doctorId);
});
