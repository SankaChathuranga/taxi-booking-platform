/**
 * Driver Management JavaScript
 * Handles driver listing, sorting and rating functionality
 */

// Initialize when document is ready
document.addEventListener('DOMContentLoaded', function() {
    // Load drivers list
    loadDrivers();
    
    // Setup event listeners
    setupEventListeners();
});

/**
 * Load drivers from the API with optional sorting
 */
function loadDrivers() {
    const driversList = document.getElementById('drivers-list');
    if (!driversList) return;
    
    // Show loading state
    driversList.innerHTML = '<div class="text-center py-4">Loading drivers...</div>';
    
    // Get the API endpoint - default is regular listing
    let apiUrl = '/api/admin/drivers';
    
    // Check if we should use the sorted endpoint
    const sortSelect = document.getElementById('sort-by');
    if (sortSelect && sortSelect.value === 'rating') {
        apiUrl = '/api/admin/drivers/sort-by-rating?ascending=false';
    }
    
    // Fetch drivers from API
    fetch(apiUrl)
        .then(response => response.json())
        .then(result => {
            if (result.success && result.data) {
                displayDrivers(result.data);
            } else {
                driversList.innerHTML = '<div class="alert alert-danger">Failed to load drivers</div>';
            }
        })
        .catch(error => {
            console.error('Error loading drivers:', error);
            driversList.innerHTML = '<div class="alert alert-danger">Error loading drivers</div>';
        });
}

/**
 * Display drivers in the list
 */
function displayDrivers(drivers) {
    const driversList = document.getElementById('drivers-list');
    if (!driversList) return;
    
    // If no drivers found
    if (!drivers || drivers.length === 0) {
        driversList.innerHTML = '<div class="text-center py-4">No drivers found</div>';
        return;
    }
    
    // Clear the list
    driversList.innerHTML = '';
    
    // Create a table to display drivers
    const table = document.createElement('table');
    table.className = 'table table-striped table-hover';
    
    // Table header
    const thead = document.createElement('thead');
    thead.innerHTML = `
        <tr>
            <th>Full Name</th>
            <th>Contact Number</th>
            <th>Vehicle Model</th>
            <th>License Plate</th>
            <th>Rating</th>
            <th>Status</th>
            <th>Driver Type</th>
            <th>Actions</th>
        </tr>
    `;
    table.appendChild(thead);
    
    // Table body
    const tbody = document.createElement('tbody');
    
    // Add each driver to the table
    drivers.forEach(driver => {
        const tr = document.createElement('tr');
        
        // Format rating stars
        const stars = '★'.repeat(Math.round(driver.rating || 0)) + 
                     '☆'.repeat(5 - Math.round(driver.rating || 0));
        
        // Format availability badge
        const availabilityBadge = driver.available 
            ? '<span class="badge bg-success">Available</span>'
            : '<span class="badge bg-secondary">Unavailable</span>';
        
        // Format driver type badge
        const driverTypeBadge = driver.driverType === 'FULL_TIME' 
            ? '<span class="badge bg-primary">Full Time</span>'
            : '<span class="badge bg-info">Part Time</span>';
        
        // Fill in the row
        tr.innerHTML = `
            <td>${driver.fullName || 'N/A'}</td>
            <td>${driver.contactNumber || 'N/A'}</td>
            <td>${driver.vehicleModel || 'N/A'}</td>
            <td>${driver.licensePlate || 'N/A'}</td>
            <td>
                <div class="d-flex align-items-center">
                    <span>${driver.rating !== undefined ? driver.rating.toFixed(1) : '0.0'}</span>
                    <div class="ms-2 text-warning">
                        ${stars}
                    </div>
                </div>
            </td>
            <td>${availabilityBadge}</td>
            <td>${driverTypeBadge}</td>
            <td>
                <div class="dropdown">
                    <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                        Actions
                    </button>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="#" onclick="viewDriverDetails('${driver.id}')">View Details</a></li>
                        <li><a class="dropdown-item" href="#" onclick="toggleDriverAvailability('${driver.id}', ${!driver.availability})">
                            ${driver.availability ? 'Mark as Unavailable' : 'Mark as Available'}
                        </a></li>
                    </ul>
                </div>
            </td>
        `;
        
        tbody.appendChild(tr);
    });
    
    table.appendChild(tbody);
    driversList.appendChild(table);
}

/**
 * Setup event listeners for the page
 */
function setupEventListeners() {
    // Sort by select change
    const sortSelect = document.getElementById('sort-by');
    if (sortSelect) {
        sortSelect.addEventListener('change', loadDrivers);
    }
    
    // Refresh button
    const refreshBtn = document.getElementById('refresh-drivers');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', loadDrivers);
    }
}

/**
 * View driver details
 */
function viewDriverDetails(driverId) {
    fetch(`/api/admin/drivers/${driverId}`)
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                showDriverDetailsModal(result.data);
            } else {
                showNotification('Failed to load driver details', 'error');
            }
        })
        .catch(error => {
            console.error('Error loading driver details:', error);
            showNotification('Error loading driver details', 'error');
        });
}

/**
 * Toggle driver availability
 */
function toggleDriverAvailability(driverId, availability) {
    // In a real app, this would make an API call to update the driver status
    fetch(`/api/admin/drivers/${driverId}/toggle-availability`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ availability: availability })
    })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                showNotification('Driver status updated successfully', 'success');
                loadDrivers(); // Refresh the list
            } else {
                showNotification(result.message || 'Error updating driver status', 'error');
            }
        })
        .catch(error => {
            console.error('Error updating driver status:', error);
            showNotification('Error updating driver status', 'error');
        });
}

/**
 * Show notification message
 */
function showNotification(message, type = 'info') {
    // If we have a notification container, use it
    const container = document.getElementById('notification-container');
    if (container) {
        const notification = document.createElement('div');
        notification.className = `alert alert-${type === 'error' ? 'danger' : type}`;
        notification.textContent = message;
        
        // Add close button
        const closeBtn = document.createElement('button');
        closeBtn.type = 'button';
        closeBtn.className = 'btn-close';
        closeBtn.setAttribute('data-bs-dismiss', 'alert');
        closeBtn.setAttribute('aria-label', 'Close');
        notification.appendChild(closeBtn);
        
        container.appendChild(notification);
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            notification.remove();
        }, 5000);
    } else {
        // Fallback to alert
        alert(message);
    }
}

/**
 * Show driver details modal
 */
function showDriverDetailsModal(driver) {
    // Create modal if it doesn't exist
    let modal = document.getElementById('driver-details-modal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'driver-details-modal';
        modal.className = 'modal fade';
        modal.setAttribute('tabindex', '-1');
        
        document.body.appendChild(modal);
    }
    
    // Format stars
    const stars = '★'.repeat(Math.round(driver.rating || 0)) + 
                 '☆'.repeat(5 - Math.round(driver.rating || 0));
    
    // Format registration date
    let registrationDate = 'N/A';
    if (driver.registrationDate) {
        try {
            const date = new Date(driver.registrationDate);
            if (!isNaN(date.getTime())) {
                registrationDate = date.toLocaleDateString();
            } else {
                registrationDate = driver.registrationDate;
            }
        } catch (e) {
            console.warn('Invalid date format:', driver.registrationDate);
        }
    }
    
    // Set modal content
    modal.innerHTML = `
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Driver Details</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5 class="card-title">${driver.fullName}</h5>
                            <p class="card-text"><strong>Contact:</strong> ${driver.contactNumber || 'N/A'}</p>
                            <p class="card-text"><strong>Vehicle:</strong> ${driver.vehicleModel || 'N/A'} (${driver.licensePlate || 'N/A'})</p>
                            <p class="card-text"><strong>Rating:</strong> ${driver.rating.toFixed(1)} <span class="text-warning">${stars}</span></p>
                            <p class="card-text"><strong>Status:</strong> ${driver.available ? 'Available' : 'Unavailable'}</p>
                            <p class="card-text"><strong>Type:</strong> ${driver.driverType || 'N/A'}</p>
                            <p class="card-text"><strong>Experience:</strong> ${driver.yearsOfExperience || 0} years</p>
                            <p class="card-text"><strong>Total Trips:</strong> ${driver.totalTrips || 0}</p>
                            <p class="card-text"><strong>Registered On:</strong> ${registrationDate}</p>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    `;
    
    // Show the modal
    const bsModal = new bootstrap.Modal(modal);
    bsModal.show();
}
