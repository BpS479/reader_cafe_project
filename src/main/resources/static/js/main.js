// Modal Management
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('show');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('show');
    }
}

// Close modal when clicking outside of it
window.addEventListener('click', function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.classList.remove('show');
    }
});

// Navigation Active State
function setActiveNav(navId) {
    const navItems = document.querySelectorAll('.nav-link');
    navItems.forEach(item => item.classList.remove('active'));
    
    const activeItem = document.getElementById(navId);
    if (activeItem) {
        activeItem.classList.add('active');
    }
}

// Search Filter for Tables
function filterTable(inputId, tableId) {
    const input = document.getElementById(inputId);
    const table = document.getElementById(tableId);
    const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
    
    input.addEventListener('keyup', function() {
        const filter = input.value.toLowerCase();
        
        for (let row of rows) {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(filter) ? '' : 'none';
        }
    });
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Reader Cafe UI loaded successfully');
});
