import { escapeHtml } from './header.js';

export function createDoctorCard(doctor, { mode = 'patient', onDelete = null, onBook = null } = {}) {
    const card = document.createElement('article');
    card.className = 'card';

    const timesHtml = Array.isArray(doctor.availableTimes) && doctor.availableTimes.length > 0
        ? doctor.availableTimes
            .map((slot) => `<span class="slot-pill">${escapeHtml(slot)}</span>`)
            .join('')
        : '<span class="slot-pill">By request</span>';

    let actionHtml = '';
    if (mode === 'admin') {
        actionHtml = `
            <button type="button" class="btn btn-danger btn-sm delete-doctor-btn">
                Delete Doctor
            </button>
        `;
    } else if (mode === 'patient') {
        actionHtml = `
            <button type="button" class="btn btn-slate btn-sm book-doctor-btn">
                Book Appointment
            </button>
        `;
    }

    card.innerHTML = `
        <div>
            <div class="card-title">${escapeHtml(doctor.name)}</div>
            <div class="card-subtitle">${escapeHtml(doctor.specialty)}</div>
        </div>
        <div class="card-meta">
            <div><strong>Email:</strong> ${escapeHtml(doctor.email)}</div>
            <div><strong>Phone:</strong> ${escapeHtml(doctor.phone)}</div>
            <div><strong>Available Slots:</strong></div>
            <div class="slot-list">${timesHtml}</div>
        </div>
        <div class="card-actions">${actionHtml}</div>
    `;

    if (mode === 'admin' && typeof onDelete === 'function') {
        const deleteBtn = card.querySelector('.delete-doctor-btn');
        deleteBtn?.addEventListener('click', () => onDelete(doctor));
    }

    if (mode === 'patient' && typeof onBook === 'function') {
        const bookBtn = card.querySelector('.book-doctor-btn');
        bookBtn?.addEventListener('click', () => onBook(doctor));
    }

    return card;
}
