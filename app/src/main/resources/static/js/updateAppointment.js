import { renderHeader, escapeHtml } from './components/header.js';
import { showAlert, clearAlert } from './components/modal.js';
import { getDoctorAvailability } from './services/doctorServices.js';
import { getAppointmentById, updateAppointment } from './services/patientServices.js';
import { slotToIsoDateTime } from './patientDashboard.js';

function ensurePatientSession() {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('userRole');
    if (!token || role !== 'PATIENT') {
        window.location.href = '/';
        return false;
    }
    return true;
}

async function loadSlots(doctorId, date) {
    const select = document.getElementById('newSlot');
    select.innerHTML = '<option value="">Loading slots...</option>';
    try {
        const slots = await getDoctorAvailability(doctorId, date);
        if (!slots.length) {
            select.innerHTML = '<option value="">No available slots on this date</option>';
            return;
        }
        select.innerHTML = slots
            .map((slot) => `<option value="${escapeHtml(slot)}">${escapeHtml(slot)}</option>`)
            .join('');
    } catch (error) {
        select.innerHTML = '<option value="">Unable to load slots</option>';
        showAlert('updateAlert', error.message, 'error');
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    if (!ensurePatientSession()) {
        return;
    }

    renderHeader();

    const params = new URLSearchParams(window.location.search);
    const appointmentId = params.get('id');
    if (!appointmentId) {
        showAlert('updateAlert', 'No appointment ID specified.', 'error');
        return;
    }

    const newDateInput = document.getElementById('newDate');
    const form = document.getElementById('updateAppointmentForm');

    try {
        const appt = await getAppointmentById(appointmentId);
        document.getElementById('apptId').value = appt.id;
        document.getElementById('apptDoctorId').value = appt.doctorId;
        document.getElementById('apptDoctorName').value = `${appt.doctorName} (${appt.doctorSpecialty})`;
        document.getElementById('apptCurrentTime').value =
            `${appt.appointmentDate} at ${appt.appointmentTimeOnly}`;

        const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];
        newDateInput.min = tomorrow;
        newDateInput.value = appt.appointmentDate >= tomorrow ? appt.appointmentDate : tomorrow;
        await loadSlots(appt.doctorId, newDateInput.value);
    } catch (error) {
        showAlert('updateAlert', error.message, 'error');
    }

    newDateInput?.addEventListener('change', () => {
        const doctorId = document.getElementById('apptDoctorId').value;
        if (doctorId && newDateInput.value) {
            loadSlots(doctorId, newDateInput.value);
        }
    });

    form?.addEventListener('submit', async (event) => {
        event.preventDefault();
        clearAlert('updateAlert');

        const slot = document.getElementById('newSlot').value;
        if (!slot) {
            showAlert('updateAlert', 'Please select an available time slot.', 'error');
            return;
        }

        try {
            const appointmentTime = slotToIsoDateTime(newDateInput.value, slot);
            await updateAppointment(appointmentId, { appointmentTime });
            showAlert('updateAlert', 'Appointment rescheduled successfully!', 'success');
            setTimeout(() => {
                window.location.href = '/pages/patientDashboard.html';
            }, 900);
        } catch (error) {
            showAlert('updateAlert', error.message, 'error');
        }
    });
});
