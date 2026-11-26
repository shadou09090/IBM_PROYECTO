export function openModal(modalId) {
    const overlay = document.getElementById(modalId);
    if (overlay) {
        overlay.classList.add('open');
    }
}

export function closeModal(modalId) {
    const overlay = document.getElementById(modalId);
    if (overlay) {
        overlay.classList.remove('open');
    }
}

export function bindModalCloseHandlers(modalId) {
    const overlay = document.getElementById(modalId);
    if (!overlay) {
        return;
    }
    const closeBtn = overlay.querySelector('.modal-close');
    if (closeBtn) {
        closeBtn.addEventListener('click', () => closeModal(modalId));
    }
    overlay.addEventListener('click', (event) => {
        if (event.target === overlay) {
            closeModal(modalId);
        }
    });
}

export function showAlert(elementId, message, type = 'error') {
    const alertEl = document.getElementById(elementId);
    if (!alertEl) {
        return;
    }
    alertEl.textContent = message;
    alertEl.className = `alert alert-${type} show`;
}

export function clearAlert(elementId) {
    const alertEl = document.getElementById(elementId);
    if (!alertEl) {
        return;
    }
    alertEl.textContent = '';
    alertEl.className = 'alert';
}
