// RentEasy Frontend Application Logic
const API_BASE = '/api';

// State
let appData = {
    stats: null,
    unpaidTenants: [],
    tenants: [],
    rooms: [],
    payments: []
};

// Current month helper YYYY-MM
function getCurrentMonthString() {
    const d = new Date();
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    return `${year}-${month}`;
}

// Current date helper YYYY-MM-DD
function getTodayString() {
    const d = new Date();
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// Currency formatter
function formatCurrency(amount) {
    if (amount === undefined || amount === null) return '₹0';
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        maximumFractionDigits: 0
    }).format(amount);
}

// UI Alert Helper
function showAlert(message, type = 'success') {
    const banner = document.getElementById('alert-banner');
    banner.className = `alert-banner ${type}`;
    banner.innerHTML = `<span>${type === 'success' ? '✅' : '❌'} ${message}</span><button onclick="this.parentElement.classList.add('hidden')" style="background:none;border:none;cursor:pointer;font-weight:bold;">&times;</button>`;
    banner.classList.remove('hidden');
    setTimeout(() => {
        banner.classList.add('hidden');
    }, 6000);
}

// Global Fetch Wrapper
async function fetchApi(endpoint, options = {}) {
    try {
        const response = await fetch(API_BASE + endpoint, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });

        const data = await response.json();

        if (!response.ok) {
            let errorMsg = data.message || `Request failed with status ${response.status}`;
            if (data.fieldErrors) {
                const fields = Object.entries(data.fieldErrors).map(([f, msg]) => `${f}: ${msg}`).join(', ');
                errorMsg += ` (${fields})`;
            }
            throw new Error(errorMsg);
        }

        return data;
    } catch (err) {
        console.error('API Error:', err);
        throw err;
    }
}

// -------------------------------------------------------------
// Initialization
// -------------------------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initModals();
    initForms();
    loadAllData();

    // Default dates in forms
    document.getElementById('reg-moveInDate').value = getTodayString();
    document.getElementById('pay-paymentDate').value = getTodayString();
    document.getElementById('pay-paymentMonth').value = getCurrentMonthString();
    document.getElementById('unpaid-section-month').innerText = getCurrentMonthString();
    document.getElementById('stat-current-month-name').innerText = getCurrentMonthString();

    document.getElementById('btn-refresh').addEventListener('click', loadAllData);
});

// Tab Navigation
function initNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    const panes = document.querySelectorAll('.tab-pane');

    navItems.forEach(item => {
        item.addEventListener('click', () => {
            const targetTab = item.getAttribute('data-tab');
            navItems.forEach(n => n.classList.remove('active'));
            panes.forEach(p => p.classList.remove('active'));

            item.classList.add('active');
            const targetPane = document.getElementById(targetTab);
            if (targetPane) targetPane.classList.add('active');

            // Update headings
            const headings = {
                'dashboard-tab': ['Hostel Rent Dashboard', 'Track cash collections, tenant rooms, and eliminate notebook confusion.'],
                'unpaid-tab': ['Unpaid Tenants for This Month', 'Actionable ledger of residents who have not paid rent for the current month.'],
                'tenants-tab': ['Tenant Directory', 'Manage active and vacated tenants, assign rooms, and inspect dues.'],
                'rooms-tab': ['Room & Bed Inventory', 'Monitor occupancy and configure room pricing and capacity.'],
                'payments-tab': ['Rent Payments Ledger', 'Searchable record of cash, UPI, and bank transfer receipts.']
            };
            if (headings[targetTab]) {
                document.getElementById('page-heading').innerText = headings[targetTab][0];
                document.getElementById('page-subheading').innerText = headings[targetTab][1];
            }
        });
    });

    document.getElementById('btn-view-all-unpaid').addEventListener('click', () => {
        document.getElementById('nav-unpaid').click();
    });
}

// Modals Setup
function initModals() {
    const modals = [
        { btn: 'btn-open-tenant-modal', modal: 'modal-register-tenant' },
        { btn: 'btn-tab-register-tenant', modal: 'modal-register-tenant' },
        { btn: 'btn-open-payment-modal', modal: 'modal-log-payment' },
        { btn: 'btn-tab-log-payment', modal: 'modal-log-payment' },
        { btn: 'btn-open-room-modal', modal: 'modal-add-room' },
        { btn: 'btn-tab-add-room', modal: 'modal-add-room' }
    ];

    modals.forEach(({ btn, modal }) => {
        const b = document.getElementById(btn);
        const m = document.getElementById(modal);
        if (b && m) {
            b.addEventListener('click', () => {
                m.classList.remove('hidden');
                if (modal === 'modal-register-tenant') populateRoomDropdown();
                if (modal === 'modal-log-payment') populateTenantDropdown();
            });
        }
    });

    // Close buttons
    document.querySelectorAll('.close-modal').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const modal = e.target.closest('.modal-overlay');
            if (modal) modal.classList.add('hidden');
        });
    });

    // Close on clicking backdrop
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) overlay.classList.add('hidden');
        });
    });
}

// -------------------------------------------------------------
// Data Loading & Rendering
// -------------------------------------------------------------
async function loadAllData() {
    try {
        await Promise.all([
            loadDashboardStats(),
            loadUnpaidTenants(),
            loadTenants(),
            loadRooms(),
            loadPayments()
        ]);
    } catch (err) {
        console.error('Error refreshing data:', err);
    }
}

async function loadDashboardStats() {
    try {
        const res = await fetchApi('/dashboard/stats');
        const stats = res.data;
        appData.stats = stats;

        document.getElementById('stat-total-rooms').innerText = stats.totalRooms;
        document.getElementById('stat-vacant-rooms').innerText = `${stats.vacantRooms} vacant`;
        document.getElementById('stat-occupied-rooms').innerText = `${stats.occupiedRooms} occupied`;

        document.getElementById('stat-active-tenants').innerText = stats.activeTenants;
        document.getElementById('stat-vacated-tenants').innerText = stats.vacatedTenants;

        document.getElementById('stat-unpaid-this-month').innerText = stats.currentMonthUnpaidTenants;
        document.getElementById('badge-unpaid-count').innerText = stats.currentMonthUnpaidTenants;

        document.getElementById('stat-month-collection').innerText = formatCurrency(stats.currentMonthCollection);
        document.getElementById('stat-total-collection').innerText = formatCurrency(stats.totalLifetimeCollection);
        document.getElementById('stat-total-pending-dues').innerText = formatCurrency(stats.totalPendingDues);
    } catch (err) {
        console.error('Failed to load stats:', err);
    }
}

async function loadUnpaidTenants() {
    try {
        const res = await fetchApi('/tenants/unpaid-current-month');
        appData.unpaidTenants = res.data || [];
        renderUnpaidTables(appData.unpaidTenants);
    } catch (err) {
        console.error('Failed to load unpaid tenants:', err);
    }
}

function renderUnpaidTables(unpaidList) {
    const previewBody = document.querySelector('#table-unpaid-preview tbody');
    const fullBody = document.querySelector('#table-unpaid-full tbody');

    if (!unpaidList || unpaidList.length === 0) {
        const emptyRow = '<tr><td colspan="6" class="text-center text-success">🎉 All active tenants have paid for the current month!</td></tr>';
        const fullEmptyRow = '<tr><td colspan="9" class="text-center text-success">🎉 All active tenants have cleared rent for this month!</td></tr>';
        if (previewBody) previewBody.innerHTML = emptyRow;
        if (fullBody) fullBody.innerHTML = fullEmptyRow;
        return;
    }

    // Preview table on dashboard
    if (previewBody) {
        previewBody.innerHTML = unpaidList.slice(0, 5).map(t => `
            <tr>
                <td><strong>${escapeHtml(t.tenantName)}</strong><br><small class="text-secondary">${t.phone}</small></td>
                <td><span class="badge badge-primary">${escapeHtml(t.roomNumber)}</span></td>
                <td>${formatCurrency(t.monthlyRent)}</td>
                <td><span class="badge badge-danger">${t.pendingMonthsCount} mos pending</span></td>
                <td><strong class="text-danger">${formatCurrency(t.totalPendingDues)}</strong></td>
                <td>
                    <button class="btn btn-sm btn-secondary" onclick="openLogPaymentForTenant(${t.tenantId}, '${t.monthlyRent}')">💵 Collect</button>
                </td>
            </tr>
        `).join('');
    }

    // Full table on unpaid tab
    if (fullBody) {
        fullBody.innerHTML = unpaidList.map(t => `
            <tr>
                <td>#${t.tenantId}</td>
                <td><strong>${escapeHtml(t.tenantName)}</strong></td>
                <td>${t.phone}</td>
                <td><span class="badge badge-primary">${escapeHtml(t.roomNumber)}</span></td>
                <td>${t.moveInDate}</td>
                <td>${formatCurrency(t.monthlyRent)}</td>
                <td><span class="badge badge-danger">${t.pendingMonthsCount} month(s)</span></td>
                <td><strong class="text-danger">${formatCurrency(t.totalPendingDues)}</strong></td>
                <td>
                    <button class="btn btn-sm btn-secondary" onclick="openLogPaymentForTenant(${t.tenantId}, '${t.monthlyRent}')">💵 Collect Rent</button>
                    <button class="btn btn-sm btn-outline" onclick="viewTenantDues(${t.tenantId})">📊 Ledger</button>
                </td>
            </tr>
        `).join('');
    }
}

async function loadTenants() {
    try {
        const status = document.getElementById('tenant-status-filter').value;
        const keyword = document.getElementById('tenant-search-input').value;
        let url = '/tenants?';
        if (status) url += `status=${status}&`;
        if (keyword) url += `keyword=${encodeURIComponent(keyword)}&`;

        const res = await fetchApi(url);
        appData.tenants = res.data || [];
        renderTenantsTable(appData.tenants);
    } catch (err) {
        console.error('Failed to load tenants:', err);
    }
}

function renderTenantsTable(tenants) {
    const tbody = document.querySelector('#table-tenants tbody');
    if (!tbody) return;

    if (!tenants || tenants.length === 0) {
        tbody.innerHTML = '<tr><td colspan="10" class="text-center">No tenants found.</td></tr>';
        return;
    }

    tbody.innerHTML = tenants.map(t => `
        <tr>
            <td>#${t.id}</td>
            <td><strong>${escapeHtml(t.fullName)}</strong></td>
            <td>${t.phone}</td>
            <td><span class="badge badge-primary">${t.roomNumber || 'None'}</span></td>
            <td>${t.roomType || '-'}</td>
            <td>${t.moveInDate}</td>
            <td>${formatCurrency(t.monthlyRent)}</td>
            <td>${formatCurrency(t.securityDeposit)}</td>
            <td>
                <span class="badge ${t.status === 'ACTIVE' ? 'badge-success' : 'badge-danger'}">${t.status}</span>
            </td>
            <td>
                <div style="display:flex;gap:6px;">
                    <button class="btn btn-sm btn-outline" onclick="viewTenantDues(${t.id})" title="View calculation of dues">📊 Dues</button>
                    ${t.status === 'ACTIVE' ? `
                        <button class="btn btn-sm btn-secondary" onclick="openLogPaymentForTenant(${t.id}, '${t.monthlyRent}')" title="Log rent payment">💵 Pay</button>
                        <button class="btn btn-sm btn-outline text-danger" onclick="vacateTenant(${t.id}, '${escapeHtml(t.fullName)}')" title="Vacate tenant & free room">🚪 Vacate</button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `).join('');
}

async function loadRooms() {
    try {
        const filterOccupied = document.getElementById('room-filter-status').value;
        let url = '/rooms';
        if (filterOccupied !== '') {
            url += `?isOccupied=${filterOccupied}`;
        }

        const res = await fetchApi(url);
        appData.rooms = res.data || [];
        renderRooms(appData.rooms);
    } catch (err) {
        console.error('Failed to load rooms:', err);
    }
}

function renderRooms(rooms) {
    // Quick grid on Dashboard
    const quickGrid = document.getElementById('room-quick-grid');
    if (quickGrid) {
        quickGrid.innerHTML = rooms.map(r => `
            <div class="room-mini-card ${r.isOccupied ? 'occupied' : 'vacant'}">
                <div class="room-num">${escapeHtml(r.roomNumber)}</div>
                <div class="room-type-tag">${r.roomType}</div>
                <div><span class="badge ${r.isOccupied ? 'badge-danger' : 'badge-success'}">${r.isOccupied ? 'FULL' : `${r.availableBeds} Free`}</span></div>
            </div>
        `).join('');
    }

    // Detailed Grid on Rooms Tab
    const detailedGrid = document.getElementById('rooms-detailed-grid');
    if (detailedGrid) {
        detailedGrid.innerHTML = rooms.map(r => `
            <div class="room-card-box ${r.isOccupied ? 'occupied' : 'vacant'}">
                <div class="room-card-head">
                    <h4>${escapeHtml(r.roomNumber)}</h4>
                    <span class="badge ${r.isOccupied ? 'badge-danger' : 'badge-success'}">${r.isOccupied ? 'OCCUPIED' : 'VACANT'}</span>
                </div>
                <div class="room-meta-item">
                    <span>Type:</span>
                    <strong>${r.roomType}</strong>
                </div>
                <div class="room-meta-item">
                    <span>Capacity / Beds:</span>
                    <strong>${r.capacity} Beds</strong>
                </div>
                <div class="room-meta-item">
                    <span>Current Occupancy:</span>
                    <strong>${r.currentOccupancy} Occupied (${r.availableBeds} Available)</strong>
                </div>
                <div class="room-meta-item">
                    <span>Monthly Rent:</span>
                    <strong class="text-primary">${formatCurrency(r.monthlyRent)} / mo</strong>
                </div>
            </div>
        `).join('');
    }
}

async function loadPayments() {
    try {
        const monthFilter = document.getElementById('payment-month-filter').value;
        let url = '/payments';
        if (monthFilter) {
            url += `?month=${monthFilter}`;
        }

        const res = await fetchApi(url);
        appData.payments = res.data || [];
        renderPaymentsTable(appData.payments);
    } catch (err) {
        console.error('Failed to load payments:', err);
    }
}

function renderPaymentsTable(payments) {
    const tbody = document.querySelector('#table-payments tbody');
    if (!tbody) return;

    if (!payments || payments.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center">No payment transactions recorded for selected period.</td></tr>';
        return;
    }

    tbody.innerHTML = payments.map(p => `
        <tr>
            <td><code>${escapeHtml(p.receiptNumber || 'N/A')}</code></td>
            <td><strong>${escapeHtml(p.tenantName || 'N/A')}</strong></td>
            <td><span class="badge badge-primary">${escapeHtml(p.roomNumber || 'N/A')}</span></td>
            <td><strong>${p.paymentMonth}</strong></td>
            <td><strong class="text-success">${formatCurrency(p.amount)}</strong></td>
            <td>${p.paymentDate}</td>
            <td><span class="badge badge-outline">${p.paymentMode}</span></td>
            <td>${escapeHtml(p.transactionReference || p.notes || '-')}</td>
            <td>
                <button class="btn btn-sm btn-outline" onclick="showReceiptModal(${JSON.stringify(p).replace(/"/g, '&quot;')})">🧾 Receipt</button>
            </td>
        </tr>
    `).join('');
}

// -------------------------------------------------------------
// Core Business Actions
// -------------------------------------------------------------

// Populate Room dropdown for Tenant Registration (enforces Rule 1)
function populateRoomDropdown() {
    const select = document.getElementById('reg-roomId');
    select.innerHTML = '<option value="">Select an available room...</option>';

    // Filter available rooms
    const availableRooms = appData.rooms.filter(r => !r.isOccupied);

    if (availableRooms.length === 0) {
        select.innerHTML = '<option value="">No vacant rooms available! (All occupied)</option>';
        return;
    }

    availableRooms.forEach(r => {
        const opt = document.createElement('option');
        opt.value = r.id;
        opt.textContent = `${r.roomNumber} (${r.roomType} - ${formatCurrency(r.monthlyRent)}/mo, ${r.availableBeds} bed(s) free)`;
        opt.dataset.rent = r.monthlyRent;
        select.appendChild(opt);
    });

    select.onchange = () => {
        const selectedOpt = select.options[select.selectedIndex];
        if (selectedOpt && selectedOpt.dataset.rent) {
            document.getElementById('reg-monthlyRent').value = selectedOpt.dataset.rent;
        }
    };
}

// Populate Tenant dropdown for logging payment
function populateTenantDropdown(preselectedTenantId = null) {
    const select = document.getElementById('pay-tenantId');
    select.innerHTML = '<option value="">Select active tenant...</option>';

    const activeTenants = appData.tenants.filter(t => t.status === 'ACTIVE');

    activeTenants.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t.id;
        opt.textContent = `${t.fullName} (Room: ${t.roomNumber || 'N/A'}, Rent: ${formatCurrency(t.monthlyRent)})`;
        opt.dataset.rent = t.monthlyRent;
        if (preselectedTenantId && String(t.id) === String(preselectedTenantId)) {
            opt.selected = true;
        }
        select.appendChild(opt);
    });

    select.onchange = () => {
        const opt = select.options[select.selectedIndex];
        if (opt && opt.dataset.rent) {
            document.getElementById('pay-amount').value = opt.dataset.rent;
        }
    };
}

// Open log payment pre-filled for a specific tenant
window.openLogPaymentForTenant = function(tenantId, monthlyRent) {
    const modal = document.getElementById('modal-log-payment');
    populateTenantDropdown(tenantId);
    document.getElementById('pay-tenantId').value = tenantId;
    document.getElementById('pay-amount').value = monthlyRent || '';
    document.getElementById('pay-paymentMonth').value = getCurrentMonthString();
    document.getElementById('pay-paymentDate').value = getTodayString();
    modal.classList.remove('hidden');
};

// View Pending Dues per Tenant (Core Feature 3 & Rule 2)
window.viewTenantDues = async function(tenantId) {
    try {
        const res = await fetchApi(`/tenants/${tenantId}/dues`);
        const dues = res.data;

        const content = document.getElementById('dues-content');
        content.innerHTML = `
            <div class="dues-summary-box">
                <div class="dues-box-item">
                    <div class="title">Tenant & Room</div>
                    <div class="val">${escapeHtml(dues.tenantName)}</div>
                    <small>Room: ${escapeHtml(dues.roomNumber)} | Rent: ${formatCurrency(dues.monthlyRent)}/mo</small>
                </div>
                <div class="dues-box-item">
                    <div class="title">Move-In Date</div>
                    <div class="val">${dues.moveInDate}</div>
                    <small>Total Elapsed: ${dues.totalMonthsElapsed} month(s)</small>
                </div>
                <div class="dues-box-item">
                    <div class="title">Current Status</div>
                    <div class="val ${dues.currentMonthPaid ? 'text-success' : 'text-danger'}">
                        ${dues.currentMonthPaid ? '✅ Paid This Month' : '⚠️ Unpaid This Month'}
                    </div>
                    <small>${dues.monthsPaidCount} paid / ${dues.pendingMonthsCount} pending</small>
                </div>
            </div>

            <div style="background:#fff;border:1px solid #e2e8f0;border-radius:8px;padding:16px;margin-bottom:16px;">
                <div style="display:flex;justify-content:space-between;align-items:center;">
                    <div>
                        <h4 style="margin-bottom:4px;">Pending Dues Formula (Business Rule 2):</h4>
                        <p style="font-size:12px;color:#64748b;">Pending Dues = (Months Since Move-In [${dues.totalMonthsElapsed}] - Months Paid [${dues.monthsPaidCount}]) &times; Monthly Rent [${formatCurrency(dues.monthlyRent)}]</p>
                    </div>
                    <div style="text-align:right;">
                        <span style="font-size:12px;color:#64748b;display:block;">Total Pending Dues</span>
                        <span style="font-size:24px;font-weight:800;" class="${dues.totalPendingDues > 0 ? 'text-danger' : 'text-success'}">
                            ${formatCurrency(dues.totalPendingDues)}
                        </span>
                    </div>
                </div>
            </div>

            <div style="margin-bottom:14px;">
                <label style="font-weight:700;font-size:12px;text-transform:uppercase;color:#475569;">Unpaid Months (${dues.unpaidMonths.length}):</label>
                <div class="month-chips">
                    ${dues.unpaidMonths.length > 0 ? dues.unpaidMonths.map(m => `
                        <span class="month-chip unpaid">❌ ${m} (${formatCurrency(dues.monthlyRent)})</span>
                    `).join('') : '<span class="text-success" style="font-size:13px;">No unpaid months! Rent is up to date.</span>'}
                </div>
            </div>

            <div>
                <label style="font-weight:700;font-size:12px;text-transform:uppercase;color:#475569;">Cleared / Paid Months (${dues.paidMonths.length}):</label>
                <div class="month-chips">
                    ${dues.paidMonths.length > 0 ? dues.paidMonths.map(m => `
                        <span class="month-chip paid">✓ ${m}</span>
                    `).join('') : '<span class="text-muted" style="font-size:13px;">No payments recorded yet.</span>'}
                </div>
            </div>
        `;

        const payNowBtn = document.getElementById('btn-dues-pay-now');
        if (dues.unpaidMonths.length > 0 && dues.status === 'ACTIVE') {
            payNowBtn.style.display = 'inline-flex';
            payNowBtn.onclick = () => {
                document.getElementById('modal-dues-breakdown').classList.add('hidden');
                openLogPaymentForTenant(dues.tenantId, dues.monthlyRent);
                document.getElementById('pay-paymentMonth').value = dues.unpaidMonths[0];
            };
        } else {
            payNowBtn.style.display = 'none';
        }

        document.getElementById('modal-dues-breakdown').classList.remove('hidden');
    } catch (err) {
        showAlert(err.message, 'error');
    }
};

// Vacate Tenant (Core Feature 5)
window.vacateTenant = async function(tenantId, tenantName) {
    if (!confirm(`Are you sure you want to vacate ${tenantName}?\nThis will mark the tenant as VACATED and immediately free up the room slot for new residents.`)) {
        return;
    }

    try {
        const res = await fetchApi(`/tenants/${tenantId}/vacate`, {
            method: 'PUT'
        });
        showAlert(res.message || 'Tenant vacated and room freed successfully!', 'success');
        loadAllData();
    } catch (err) {
        showAlert(err.message, 'error');
    }
};

// Show Receipt Modal
window.showReceiptModal = function(payment) {
    const card = document.getElementById('receipt-card-content');
    card.innerHTML = `
        <div class="receipt-head">
            <h3 style="font-size:18px;margin-bottom:4px;">RentEasy Hostel & PG</h3>
            <p style="font-size:11px;color:#64748b;">OFFICIAL RENT PAYMENT RECEIPT</p>
        </div>
        <div class="receipt-row">
            <span>Receipt Number:</span>
            <strong>${escapeHtml(payment.receiptNumber || 'N/A')}</strong>
        </div>
        <div class="receipt-row">
            <span>Payment Date:</span>
            <span>${payment.paymentDate}</span>
        </div>
        <div class="receipt-row">
            <span>Tenant Name:</span>
            <strong>${escapeHtml(payment.tenantName)}</strong>
        </div>
        <div class="receipt-row">
            <span>Room Number:</span>
            <span>Room ${escapeHtml(payment.roomNumber || 'N/A')}</span>
        </div>
        <div class="receipt-row">
            <span>Rent For Month:</span>
            <strong>${payment.paymentMonth}</strong>
        </div>
        <div class="receipt-row">
            <span>Payment Mode:</span>
            <span>${payment.paymentMode}</span>
        </div>
        ${payment.transactionReference ? `
        <div class="receipt-row">
            <span>Txn Reference:</span>
            <span>${escapeHtml(payment.transactionReference)}</span>
        </div>` : ''}
        <div style="border-top:2px dashed #cbd5e1;margin:12px 0;"></div>
        <div class="receipt-row" style="font-size:16px;">
            <span>Total Paid:</span>
            <strong style="color:#10b981;">${formatCurrency(payment.amount)}</strong>
        </div>
        <div style="text-align:center;margin-top:16px;font-size:11px;color:#94a3b8;">
            Thank you for paying on time! Status: PAID (Verified)
        </div>
    `;
    document.getElementById('modal-receipt').classList.remove('hidden');
};

// -------------------------------------------------------------
// Form Submissions
// -------------------------------------------------------------
function initForms() {
    // 1. Register Tenant Form
    document.getElementById('form-register-tenant').addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            fullName: document.getElementById('reg-fullName').value.trim(),
            phone: document.getElementById('reg-phone').value.trim(),
            email: document.getElementById('reg-email').value.trim() || null,
            roomId: parseInt(document.getElementById('reg-roomId').value),
            moveInDate: document.getElementById('reg-moveInDate').value,
            monthlyRent: document.getElementById('reg-monthlyRent').value ? parseFloat(document.getElementById('reg-monthlyRent').value) : null,
            securityDeposit: parseFloat(document.getElementById('reg-securityDeposit').value || 0)
        };

        try {
            const res = await fetchApi('/tenants', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            showAlert(res.message || 'Tenant registered successfully!', 'success');
            document.getElementById('modal-register-tenant').classList.add('hidden');
            document.getElementById('form-register-tenant').reset();
            loadAllData();
        } catch (err) {
            showAlert(err.message, 'error');
        }
    });

    // 2. Log Payment Form
    document.getElementById('form-log-payment').addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            tenantId: parseInt(document.getElementById('pay-tenantId').value),
            paymentMonth: document.getElementById('pay-paymentMonth').value,
            amount: parseFloat(document.getElementById('pay-amount').value),
            paymentDate: document.getElementById('pay-paymentDate').value,
            paymentMode: document.getElementById('pay-paymentMode').value,
            transactionReference: document.getElementById('pay-reference').value.trim() || null,
            notes: null
        };

        try {
            const res = await fetchApi('/payments', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            showAlert(res.message || 'Payment logged successfully!', 'success');
            document.getElementById('modal-log-payment').classList.add('hidden');
            document.getElementById('form-log-payment').reset();
            loadAllData();

            // Offer instant receipt preview
            if (res.data) {
                showReceiptModal(res.data);
            }
        } catch (err) {
            showAlert(err.message, 'error');
        }
    });

    // 3. Add Room Form
    document.getElementById('form-add-room').addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            roomNumber: document.getElementById('room-number').value.trim(),
            roomType: document.getElementById('room-type').value,
            capacity: parseInt(document.getElementById('room-capacity').value),
            monthlyRent: parseFloat(document.getElementById('room-rent').value)
        };

        try {
            const res = await fetchApi('/rooms', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            showAlert(res.message || 'Room created successfully!', 'success');
            document.getElementById('modal-add-room').classList.add('hidden');
            document.getElementById('form-add-room').reset();
            loadAllData();
        } catch (err) {
            showAlert(err.message, 'error');
        }
    });

    // Search and filter listeners
    document.getElementById('tenant-search-input').addEventListener('input', debounce(loadTenants, 300));
    document.getElementById('tenant-status-filter').addEventListener('change', loadTenants);
    document.getElementById('room-filter-status').addEventListener('change', loadRooms);
    document.getElementById('payment-month-filter').addEventListener('change', loadPayments);
    document.getElementById('btn-clear-payment-filter').addEventListener('click', () => {
        document.getElementById('payment-month-filter').value = '';
        loadPayments();
    });
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"']/g, function(m) {
        return {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        }[m];
    });
}
