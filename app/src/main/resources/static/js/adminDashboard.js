import { renderHeader } from './components/header.js';
import { createDoctorCard } from './components/doctorCard.js';
import { openModal, closeModal, bindModalCloseHandlers, showAlert, clearAlert } from './components/modal.js';
import { filterDoctors, createDoctor, deleteDoctor } from './services/doctorServices.js';

function ensureAdminSession() {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('userRole');
    if (!token || role !== 'ADMIN') {
        window.location.href = '/';
        return false;
    }
    return true;
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
            grid.innerHTML = '<div class="empty-state">No doctors match the selected filters.</div>';
            return;
        }

        doctors.forEach((doctor) => {
            const card = createDoctorCard(doctor, {
                mode: 'admin',
                onDelete: async (doc) => {
                    if (!confirm(`Delete ${doc.name} (${doc.specialty})?`)) {
                        return;
                    }
                    try {
                        await deleteDoctor(doc.id);
                        showAlert('adminAlert', `${doc.name} removed successfully.`, 'success');
                        await loadDoctors();
                    } catch (error) {
                        showAlert('adminAlert', error.message, 'error');
                    }
                }
            });
            grid.appendChild(card);
        });
    } catch (error) {
        showAlert('adminAlert', error.message, 'error');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (!ensureAdminSession()) {
        return;
    }

    renderHeader();
    bindModalCloseHandlers('addDoctorModal');

    const searchInput = document.getElementById('searchDoctorInput');
    const timeSelect = document.getElementById('filterTimeSelect');
    const specialtySelect = document.getElementById('filterSpecialtySelect');
    const resetBtn = document.getElementById('resetFiltersBtn');
    const openAddBtn = document.getElementById('openAddDoctorBtn');
    const addForm = document.getElementById('addDoctorForm');

    searchInput?.addEventListener('input', loadDoctors);
    timeSelect?.addEventListener('change', loadDoctors);
    specialtySelect?.addEventListener('change', loadDoctors);

    resetBtn?.addEventListener('click', () => {
        searchInput.value = '';
        timeSelect.value = '';
        specialtySelect.value = '';
        loadDoctors();
    });

    openAddBtn?.addEventListener('click', () => {
        clearAlert('addDoctorAlert');
        addForm.reset();
        openModal('addDoctorModal');
    });

    addForm?.addEventListener('submit', async (event) => {
        event.preventDefault();
        clearAlert('addDoctorAlert');

        const selectedTimes = Array.from(
            document.querySelectorAll('input[name="availableTimes"]:checked')
        ).map((cb) => cb.value);

        const payload = {
            name: document.getElementById('docName').value.trim(),
            specialty: document.getElementById('docSpecialty').value.trim(),
            email: document.getElementById('docEmail').value.trim(),
            phone: document.getElementById('docPhone').value.trim(),
            password: document.getElementById('docPassword').value,
            availableTimes: selectedTimes
        };

        try {
            await createDoctor(payload);
            closeModal('addDoctorModal');
            showAlert('adminAlert', `Doctor ${payload.name} added successfully.`, 'success');
            await loadDoctors();
        } catch (error) {
            showAlert('addDoctorAlert', error.message, 'error');
        }
    });

    loadDoctors();
});
