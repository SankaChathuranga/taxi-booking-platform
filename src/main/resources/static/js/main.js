// Global state
let currentUser = null;
let currentRole = null;
let currentDriverEntityId = null; // Store the driver entity ID for the logged-in driver

// Navigation handling
document.addEventListener('DOMContentLoaded', () => {
<<<<<<< HEAD
=======
    // Add Admin Panel button to public navigation
    addAdminPanelButton();
    
>>>>>>> ff35299 (FIXED SOME ERRORS)
    // Check for existing session
    const user = localStorage.getItem('user');
    if (user) {
        currentUser = JSON.parse(user);
        currentRole = currentUser.role;
        updateNavigation();
    }

    // Navigation click handlers
    document.querySelectorAll('.nav-links a[data-section]').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = link.getAttribute('data-section');
            showSection(targetId);
        });
    });

    // Logout handlers
    document.querySelectorAll('[id$="-logout"]').forEach(button => {
        button.addEventListener('click', handleLogout);
    });

    // Form submissions
    setupFormHandlers();
});

<<<<<<< HEAD
=======
// Add Admin Panel button to the navigation
function addAdminPanelButton() {
    const publicNav = document.querySelector('.public-nav');
    if (publicNav) {
        // Check if the admin button already exists
        if (!document.querySelector('.admin-link')) {
            const adminLink = document.createElement('a');
            adminLink.href = '/admin/index.html';
            adminLink.className = 'admin-link';
            adminLink.textContent = 'Admin Panel';
            publicNav.appendChild(adminLink);
        }
        
        // Add Payment History link
        if (!document.querySelector('.payment-history-link')) {
            const paymentHistoryLink = document.createElement('a');
            paymentHistoryLink.href = '/payment-history.html';
            paymentHistoryLink.className = 'payment-history-link';
            paymentHistoryLink.textContent = 'Payment History';
            publicNav.appendChild(paymentHistoryLink);
        }
    }
}

>>>>>>> ff35299 (FIXED SOME ERRORS)
// Navigation functions
function updateNavigation() {
    // Hide all navigation groups
    document.querySelectorAll('.public-nav, .user-nav, .driver-nav, .admin-nav').forEach(nav => {
        nav.style.display = 'none';
    });

    // Show appropriate navigation based on role
    switch (currentRole) {
        case 'CUSTOMER':
            document.querySelector('.user-nav').style.display = 'flex';
<<<<<<< HEAD
            break;
        case 'DRIVER':
            document.querySelector('.driver-nav').style.display = 'flex';
=======
            // Add Admin Panel button to user nav
            addAdminButtonToNav('.user-nav');
            break;
        case 'DRIVER':
            document.querySelector('.driver-nav').style.display = 'flex';
            // Add Admin Panel button to driver nav
            addAdminButtonToNav('.driver-nav');
>>>>>>> ff35299 (FIXED SOME ERRORS)
            break;
        case 'ADMIN':
            document.querySelector('.admin-nav').style.display = 'flex';
            break;
        default:
            document.querySelector('.public-nav').style.display = 'flex';
<<<<<<< HEAD
=======
            // Add Admin Panel button to public nav (already done in DOMContentLoaded)
            addAdminPanelButton();
    }
}

// Helper function to add Admin Panel button to any navigation bar
function addAdminButtonToNav(navSelector) {
    const nav = document.querySelector(navSelector);
    if (nav && !nav.querySelector('.admin-link')) {
        const adminLink = document.createElement('a');
        adminLink.href = '/admin/index.html';
        adminLink.className = 'admin-link';
        adminLink.textContent = 'Admin Panel';
        nav.appendChild(adminLink);
>>>>>>> ff35299 (FIXED SOME ERRORS)
    }
}

function showSection(sectionId) {
    // Hide all sections
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });

    // Show target section
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.add('active');
    }

    // Update active navigation
    document.querySelectorAll('.nav-links a').forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('data-section') === sectionId) {
            link.classList.add('active');
        }
    });

    // Load section-specific data if user is logged in
    if (currentUser) {
        loadSectionData(sectionId);
    }
}

// Form handlers
function setupFormHandlers() {
    // Login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(loginForm);
            const data = {
                email: formData.get('email'),
                password: formData.get('password')
            };

            try {
                const response = await fetch('/api/users/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });

                const result = await response.json();
                if (result.success) {
                    currentUser = result.data;
                    currentRole = result.data.role;
                    localStorage.setItem('user', JSON.stringify(result.data));
                    showNotification('Login successful!', 'success');
                    updateNavigation();
                    
                    // Show appropriate dashboard based on role
                    const dashboardSection = `${currentRole.toLowerCase()}-dashboard`;
                    showSection(dashboardSection);
                } else {
                    showNotification(result.message || 'Login failed', 'error');
                }
            } catch (error) {
                console.error('Login error:', error);
                showNotification('An error occurred during login', 'error');
            }
        });
    }

    // Registration form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(registerForm);
            const data = {
                name: formData.get('name'),
                email: formData.get('email'),
                password: formData.get('password'),
                phone: formData.get('phone'),
                address: formData.get('address'),
                role: formData.get('role')
            };

            try {
                const response = await fetch('/api/users/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });

                const result = await response.json();
                if (result.success) {
                    showNotification('Registration successful! Please login.', 'success');
                    showSection('login');
                } else {
                    showNotification(result.message, 'error');
                }
            } catch (error) {
                showNotification('An error occurred during registration', 'error');
            }
        });
    }

    // Booking form
    const bookingForm = document.getElementById('bookingForm');
    if (bookingForm) {
        bookingForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            if (!currentUser) {
                showNotification('Please login to book a ride', 'error');
                showSection('login');
                return;
            }

            const formData = new FormData(bookingForm);
            const data = {
                userId: currentUser.id,
                pickupLocation: formData.get('pickup'),
                dropLocation: formData.get('dropoff'),
                bookingTime: formData.get('bookingTime')
            };

            try {
                const response = await fetch('/api/bookings', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });

                const result = await response.json();
                if (result.success) {
                    showNotification(result.message || 'Booking successful!', 'success');
                    bookingForm.reset();
<<<<<<< HEAD
                    loadSectionData('user-dashboard');
                    loadSectionData('user-history');
=======
                    
                    // Get the booking ID from the response
                    const bookingId = result.data.id;
                    
                    // Redirect to payment processing page with the booking ID
                    window.location.href = `/payment-process.html?bookingId=${bookingId}`;
>>>>>>> ff35299 (FIXED SOME ERRORS)
                } else {
                    showNotification(result.message || 'An error occurred during booking', 'error');
                }
            } catch (error) {
                console.error('Booking error:', error);
                showNotification('An error occurred during booking', 'error');
            }
        });
    }

    // Driver availability toggle
    const availabilityToggle = document.getElementById('availability-toggle');
    if (availabilityToggle) {
        availabilityToggle.addEventListener('change', async (e) => {
            try {
                if (!currentDriverEntityId) {
                    showNotification('Driver profile not loaded', 'error');
                    e.target.checked = !e.target.checked;
                    return;
                }
                const response = await fetch(`/api/drivers/${currentDriverEntityId}/availability`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ available: e.target.checked })
                });

                const result = await response.json();
                if (result.success) {
                    showNotification(`Driver ${e.target.checked ? 'available' : 'unavailable'}`, 'success');
                } else {
                    showNotification(result.message, 'error');
                    e.target.checked = !e.target.checked;
                }
            } catch (error) {
                showNotification('An error occurred updating availability', 'error');
                e.target.checked = !e.target.checked;
            }
        });
    }
}

// Section data loading
function loadSectionData(sectionId) {
    if (!currentUser) return;

    switch (sectionId) {
        case 'user-dashboard':
            loadUserDashboard();
            break;
        case 'user-history':
            loadUserHistory();
            break;
        case 'driver-dashboard':
            loadDriverDashboard();
            break;
        case 'driver-current':
            loadDriverCurrentRides();
            break;
        case 'driver-history':
            loadDriverHistory();
            break;
        case 'admin-dashboard':
            loadAdminDashboard();
            break;
        case 'admin-drivers':
            loadAdminDrivers();
            break;
        case 'admin-users':
            loadAdminUsers();
            break;
        case 'admin-bookings':
            loadAdminBookings();
            break;
    }
}

// Data loading functions
async function loadUserDashboard() {
    try {
        const [bookingsResponse, statsResponse] = await Promise.all([
            fetch(`/api/bookings/user/${currentUser.id}`),
            fetch(`/api/users/${currentUser.id}/stats`)
        ]);

        const bookings = await bookingsResponse.json();
        const stats = await statsResponse.json();

        if (bookings.success && stats.success) {
            document.getElementById('user-active-bookings').textContent = 
                bookings.data.filter(b => b.status === 'ACCEPTED').length;
            document.getElementById('user-total-rides').textContent = 
                bookings.data.filter(b => b.status === 'COMPLETED').length;
        }
    } catch (error) {
        showNotification('Error loading dashboard data', 'error');
    }
}

async function loadUserHistory() {
    try {
        const response = await fetch(`/api/bookings/user/${currentUser.id}`);
        const result = await response.json();

        if (result.success) {
            const historyList = document.getElementById('user-booking-history');
            if (result.data && result.data.length > 0) {
                historyList.innerHTML = result.data.map(booking => `
                    <div class="history-item">
                        <h3>Booking #${booking.id}</h3>
                        <p>From: ${booking.pickupLocation}</p>
                        <p>To: ${booking.dropLocation}</p>
                        <p>Status: ${booking.status}</p>
                        <p>Time: ${new Date(booking.bookingTime).toLocaleString()}</p>
                        ${booking.status === 'COMPLETED' && !booking.rating ? `
                            <div class="rating-section">
                                <button class="action-button" onclick="rateRide('${booking.id}')">
                                    Rate Ride
                                </button>
                            </div>
                        ` : ''}
                    </div>
                `).join('');
            } else {
                historyList.innerHTML = '<p>No booking history found</p>';
            }
        }
    } catch (error) {
        console.error('Error loading booking history:', error);
        showNotification('Error loading booking history', 'error');
    }
}

async function rateRide(bookingId) {
    const rating = prompt('Please rate your ride (1-5):');
    if (rating && !isNaN(rating) && rating >= 1 && rating <= 5) {
        try {
            const response = await fetch(`/api/bookings/${bookingId}/rate?rating=${rating}`, {
                method: 'PUT'
            });
            const result = await response.json();
            
            if (result.success) {
                showNotification('Rating submitted successfully', 'success');
                loadUserHistory();
            } else {
                showNotification(result.message, 'error');
            }
        } catch (error) {
            showNotification('Error submitting rating', 'error');
        }
    } else {
        showNotification('Please enter a valid rating between 1 and 5', 'error');
    }
}

async function loadDriverDashboard() {
    try {
        // Fetch driver entity by userId
        const response = await fetch(`/api/drivers/user/${currentUser.id}`);
        const result = await response.json();

        if (result.success && result.data) {
            const driver = result.data;
            currentDriverEntityId = driver.id;
            document.getElementById('driver-rating').textContent = driver.rating ? driver.rating.toFixed(1) : '0.0';
            // Set toggle state
            const availabilityToggle = document.getElementById('availability-toggle');
            if (availabilityToggle) {
                availabilityToggle.checked = !!driver.availability;
            }
        } else {
            showNotification('Driver profile not found', 'error');
        }

        // Optionally, fetch current ride info as before
        const currentRideResponse = await fetch(`/api/bookings/driver/${currentUser.id}/current`);
        const currentRide = await currentRideResponse.json();
        if (currentRide.success) {
            document.getElementById('driver-current-ride').textContent = 
                currentRide.data ? `Booking #${currentRide.data.id}` : 'None';
        }
    } catch (error) {
        showNotification('Error loading driver dashboard', 'error');
    }
}

async function loadDriverCurrentRides() {
    try {
        const response = await fetch(`/api/bookings/driver/${currentUser.id}`);
        const result = await response.json();

        if (result.success) {
            const currentRidesList = document.getElementById('current-rides-list');
            const currentRides = result.data.filter(booking => 
                booking.status === 'PENDING' || booking.status === 'ACCEPTED'
            );
            
            if (currentRides.length > 0) {
                currentRidesList.innerHTML = currentRides.map(booking => `
                    <div class="history-item">
                        <h3>Booking #${booking.id}</h3>
                        <p>From: ${booking.pickupLocation}</p>
                        <p>To: ${booking.dropLocation}</p>
                        <p>Status: ${booking.status}</p>
                        <p>Time: ${new Date(booking.bookingTime).toLocaleString()}</p>
                        ${booking.status === 'PENDING' ? `
                            <button class="action-button" onclick="acceptRide('${booking.id}')">
                                Accept Ride
                            </button>
                        ` : booking.status === 'ACCEPTED' ? `
                            <button class="action-button" onclick="completeRide('${booking.id}')">
                                Complete Ride
                            </button>
                        ` : ''}
                    </div>
                `).join('');
            } else {
                currentRidesList.innerHTML = '<p>No current rides</p>';
            }
        }
    } catch (error) {
        console.error('Error loading current rides:', error);
        showNotification('Error loading current rides', 'error');
    }
}

async function acceptRide(bookingId) {
    try {
        const response = await fetch(`/api/bookings/${bookingId}/accept`, {
            method: 'PUT'
        });
        const result = await response.json();

        if (result.success) {
            showNotification('Ride accepted successfully', 'success');
            loadDriverCurrentRides();
            loadDriverDashboard();
        } else {
            showNotification(result.message, 'error');
        }
    } catch (error) {
        showNotification('Error accepting ride', 'error');
    }
}

async function completeRide(bookingId) {
    try {
        const response = await fetch(`/api/bookings/${bookingId}/complete`, {
            method: 'PUT'
        });
        const result = await response.json();

        if (result.success) {
            showNotification('Ride completed successfully', 'success');
            loadDriverCurrentRides();
            loadDriverDashboard();
        } else {
            showNotification(result.message || 'Error completing ride', 'error');
        }
    } catch (error) {
        console.error('Error completing ride:', error);
        showNotification('Error completing ride', 'error');
    }
}

async function loadDriverHistory() {
    try {
        const response = await fetch(`/api/bookings/driver/${currentUser.id}`);
        const result = await response.json();

        if (result.success) {
            const historyList = document.getElementById('driver-ride-history');
            if (result.data && result.data.length > 0) {
                historyList.innerHTML = result.data.map(booking => `
                    <div class="history-item">
                        <h3>Ride #${booking.id}</h3>
                        <p>From: ${booking.pickupLocation}</p>
                        <p>To: ${booking.dropLocation}</p>
                        <p>Status: ${booking.status}</p>
                        <p>Time: ${new Date(booking.bookingTime).toLocaleString()}</p>
                        ${booking.rating ? `<p>Rating: ${booking.rating}</p>` : ''}
                    </div>
                `).join('');
            } else {
                historyList.innerHTML = '<p>No ride history found</p>';
            }
        } else {
            showNotification(result.message || 'Error loading ride history', 'error');
        }
    } catch (error) {
        console.error('Error loading ride history:', error);
        showNotification('Error loading ride history', 'error');
    }
}

async function loadAdminDashboard() {
    try {
        const response = await fetch('/api/admin/reports');
        const result = await response.json();

        if (result.success) {
            const report = result.data;
            // Parse the report string to extract numbers
            const totalUsers = report.match(/Total Users: (\d+)/)?.[1] || '0';
            const totalDrivers = report.match(/Total Drivers: (\d+)/)?.[1] || '0';
            const activeBookings = report.match(/Pending Bookings: (\d+)/)?.[1] || '0';

            document.getElementById('total-users').textContent = totalUsers;
            document.getElementById('total-drivers').textContent = totalDrivers;
            document.getElementById('active-bookings').textContent = activeBookings;
        } else {
            showNotification(result.message || 'Error loading admin dashboard', 'error');
        }
    } catch (error) {
        console.error('Error loading admin dashboard:', error);
        showNotification('Error loading admin dashboard', 'error');
    }
}

async function loadAdminDrivers() {
    try {
        const response = await fetch('/api/admin/drivers');
        const result = await response.json();

        if (result.success) {
            const driversList = document.getElementById('drivers-list');
            driversList.innerHTML = result.data.map(driver => `
                <div class="history-item">
                    <h3>${driver.name}</h3>
                    <p>Email: ${driver.email}</p>
                    <p>Status: ${driver.status}</p>
                    <p>Rating: ${driver.rating.toFixed(1)}</p>
                    <div class="admin-actions">
                        ${driver.status === 'PENDING' ? `
                            <button class="action-button" onclick="approveDriver(${driver.id})">
                                Approve
                            </button>
                        ` : ''}
                        <button class="action-button" onclick="toggleDriverStatus(${driver.id})">
                            ${driver.status === 'ACTIVE' ? 'Suspend' : 'Activate'}
                        </button>
                    </div>
                </div>
            `).join('');
        }
    } catch (error) {
        showNotification('Error loading drivers', 'error');
    }
}

async function loadAdminUsers() {
    try {
        const response = await fetch('/api/admin/users');
        const result = await response.json();

        if (result.success) {
            const usersList = document.getElementById('users-list');
            usersList.innerHTML = result.data.map(user => `
                <div class="history-item">
                    <h3>${user.name}</h3>
                    <p>Email: ${user.email}</p>
                    <p>Role: ${user.role}</p>
                    <div class="admin-actions">
                        <button class="action-button" onclick="toggleUserStatus(${user.id})">
                            ${user.status === 'ACTIVE' ? 'Suspend' : 'Activate'}
                        </button>
                    </div>
                </div>
            `).join('');
        }
    } catch (error) {
        showNotification('Error loading users', 'error');
    }
}

async function loadAdminBookings() {
    try {
        const status = document.getElementById('booking-status-filter').value;
        const response = await fetch(`/api/admin/bookings?status=${status}`);
        const result = await response.json();

        if (result.success) {
            const bookingsList = document.getElementById('bookings-list');
            bookingsList.innerHTML = result.data.map(booking => `
                <div class="history-item">
                    <h3>Booking #${booking.id}</h3>
                    <p>From: ${booking.pickupLocation}</p>
                    <p>To: ${booking.dropLocation}</p>
                    <p>Status: ${booking.status}</p>
                    <p>Time: ${new Date(booking.bookingTime).toLocaleString()}</p>
                </div>
            `).join('');
        }
    } catch (error) {
        showNotification('Error loading bookings', 'error');
    }
}

async function approveDriver(driverId) {
    try {
        const response = await fetch(`/api/admin/drivers/${driverId}/approve`, {
            method: 'PUT'
        });
        const result = await response.json();

        if (result.success) {
            showNotification('Driver approved successfully', 'success');
            loadAdminDrivers();
        } else {
            showNotification(result.message || 'Error approving driver', 'error');
        }
    } catch (error) {
        console.error('Error approving driver:', error);
        showNotification('Error approving driver', 'error');
    }
}

async function toggleDriverStatus(driverId) {
    try {
        const response = await fetch(`/api/admin/drivers/${driverId}/toggle-status`, {
            method: 'PUT'
        });
        const result = await response.json();

        if (result.success) {
            showNotification('Driver status updated successfully', 'success');
            loadAdminDrivers();
        } else {
            showNotification(result.message || 'Error updating driver status', 'error');
        }
    } catch (error) {
        console.error('Error updating driver status:', error);
        showNotification('Error updating driver status', 'error');
    }
}

async function toggleUserStatus(userId) {
    try {
        const response = await fetch(`/api/admin/users/${userId}/toggle-status`, {
            method: 'PUT'
        });
        const result = await response.json();

        if (result.success) {
            showNotification('User status updated successfully', 'success');
            loadAdminUsers();
        } else {
            showNotification(result.message || 'Error updating user status', 'error');
        }
    } catch (error) {
        console.error('Error updating user status:', error);
        showNotification('Error updating user status', 'error');
    }
}

// Notification function
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    // Remove notification after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Logout handler
function handleLogout() {
    currentUser = null;
    currentRole = null;
    localStorage.removeItem('user');
    showNotification('Logged out successfully', 'success');
    updateNavigation();
    showSection('home');
}