# RentEasy - PG & Hostel Room Rent Payment Tracker

A production-ready Spring Boot backend and interactive web dashboard built to solve the real-world challenge faced by PG and hostel owners who track cash collections and payments in paper notebooks, causing confusion over who has paid for which month.

---

## 🌟 Real-World Problem & Solution

### The Problem
PG and hostel owners collect rent in cash or UPI transfers and record them informally in notebooks. This leads to:
* Confusion over whether a tenant has paid for a particular calendar month.
* Inaccurate pending due calculations when tenants stay for several months.
* Double-booking or assigning newly arrived tenants to already occupied rooms.
* Difficulty tracking vacant vs. occupied beds in shared and single rooms.
* Failure to follow up promptly with tenants who have not paid for the current month.

### The RentEasy Solution
RentEasy automates room occupancy, locks in tenant rental agreements, tracks month-by-month rent payments, enforces strict business rules in the service layer, and calculates outstanding dues using the exact formula:

$$\text{Pending Dues} = (\text{Months Elapsed Since Move-In} - \text{Months Paid}) \times \text{Monthly Rent}$$

---

## 🏗️ System Architecture & Database Design

### Entity-Relationship (ER) Overview
```
┌─────────────────────────────────┐           ┌─────────────────────────────────┐
│              ROOM               │           │             TENANT              │
├─────────────────────────────────┤           ├─────────────────────────────────┤
│ id (PK, Long)                   │ 1       * │ id (PK, Long)                   │
│ room_number (Unique, String)    │───────────│ full_name (String)              │
│ room_type (SINGLE/DOUBLE/etc.)  │           │ phone (String)                  │
│ capacity (Int)                  │           │ email (String)                  │
│ current_occupancy (Int)         │           │ move_in_date (LocalDate)        │
│ monthly_rent (Double)           │           │ move_out_date (LocalDate)       │
│ is_occupied (Boolean)           │           │ monthly_rent (Double)           │
└─────────────────────────────────┘           │ security_deposit (Double)       │
                                              │ status (ACTIVE / VACATED)       │
                                              │ room_id (FK -> rooms.id)        │
                                              └─────────────────────────────────┘
                                                              │ 1
                                                              │
                                                              │ *
                                              ┌─────────────────────────────────┐
                                              │          RENT_PAYMENT           │
                                              ├─────────────────────────────────┤
                                              │ id (PK, Long)                   │
                                              │ tenant_id (FK -> tenants.id)    │
                                              │ amount (Double)                 │
                                              │ payment_month (String: YYYY-MM) │
                                              │ payment_date (LocalDate)        │
                                              │ payment_mode (CASH/UPI/etc.)    │
                                              │ receipt_number (Unique, String) │
                                              │ transaction_reference (String)  │
                                              │ notes (String)                  │
                                              │ CONSTRAINT (tenant_id, month)   │
                                              └─────────────────────────────────┘
```

---

## ⚖️ Business Rules Enforced in Service Layer

1. **Room Occupancy Rule**:
   * A room cannot be assigned to a new tenant while it is occupied (`isOccupied == true` or `currentOccupancy >= capacity`).
   * Evaluated **before saving** in [TenantService.java](file:///c:/Users/tamil/OneDrive/Documents/Desktop/PG%20Hostel%20Room%20Rent%20Payment%20Tracker/src/main/java/com/renteasy/service/TenantService.java#L47-L54).
   * Throws `RoomAlreadyOccupiedException` (returns HTTP `409 CONFLICT`).

2. **Pending Dues Calculation Rule**:
   * Pending dues are calculated dynamically as **months since move-in minus months paid**.
   * Identifies all eligible calendar months from `moveInDate` up to the current month (or `moveOutDate` if vacated).
   * Computes exact itemized lists of `paidMonths` and `unpaidMonths`.
   * Throws no stack traces; returns structured JSON with total outstanding dues.

3. **Duplicate Payment Prevention Rule**:
   * A tenant cannot pay twice for the same calendar month (`paymentMonth` format: `YYYY-MM`).
   * Checked in [PaymentService.java](file:///c:/Users/tamil/OneDrive/Documents/Desktop/PG%20Hostel%20Room%20Rent%20Payment%20Tracker/src/main/java/com/renteasy/service/PaymentService.java#L58-L64) and backed by database unique constraint `(tenant_id, payment_month)`.
   * Throws `DuplicatePaymentException` (returns HTTP `409 CONFLICT`).

4. **Vacating and Freeing Room Rule**:
   * When vacating a tenant, their status changes to `VACATED`, `moveOutDate` is recorded, and the room's occupancy is immediately decremented (`isOccupied` set to `false`).
   * The freed room immediately becomes available for assignment to new tenants.

---

## 🚀 Core Features & REST API Endpoints

| # | Feature | HTTP Method | Endpoint | Description |
|---|---|---|---|---|
| **1** | **Register Tenant** | `POST` | `/api/tenants` | Registers tenant, locks monthly rent, validates room availability, marks room occupied. |
| **2** | **Log Rent Payment** | `POST` | `/api/payments` | Records payment for month (`YYYY-MM`), rejects duplicates, generates receipt. |
| **3** | **View Pending Dues** | `GET` | `/api/tenants/{id}/dues` | Calculates months elapsed minus months paid & itemized unpaid months. |
| **4** | **List Unpaid This Month** | `GET` | `/api/tenants/unpaid-current-month` | Lists all active tenants without a payment entry for current month. |
| **5** | **Vacate Tenant** | `PUT` | `/api/tenants/{id}/vacate` | Sets status to VACATED, records move-out date, and frees room immediately. |
| **+** | **Dashboard Stats** | `GET` | `/api/dashboard/stats` | Aggregated counts, collections, unpaid count, and total pending dues. |
| **+** | **List / Search Tenants** | `GET` | `/api/tenants?keyword=...&status=ACTIVE` | Search tenants by name or phone with status filter. |
| **+** | **Manage Rooms** | `GET` / `POST` | `/api/rooms` | Create rooms and filter rooms by vacancy (`?isOccupied=false`). |
| **+** | **Payment Ledger** | `GET` | `/api/payments?month=YYYY-MM` | View transaction history and receipts. |

---

## 💻 Interactive Web Dashboard

Open your browser to:
```
http://localhost:8080/
```
The embedded single-page application features:
* **Metric Cards**: Total Rooms, Occupied/Vacant Beds, Active Tenants, Current Month Collection, Total Pending Dues.
* **⚠️ Unpaid This Month Panel**: Direct listing of defaulters with 1-click **"Collect Rent"** action.
* **Tenant Directory & Registration**: Modal for registering tenants with automatic dropdown of vacant rooms only.
* **Pending Dues Breakdown**: Visual ledger showing every paid month (green chip) vs. unpaid month (red chip) and total calculation.
* **Printable Rent Receipts**: Automatically formatted receipt cards for cash/UPI payments.
* **Room Grid**: Visual cards for room inventory status.

---

## 🛠️ How to Run Locally

### Option A: Using the Windows Batch Script
Double-click `run.bat` or run:
```cmd
run.bat
```

### Option B: Using PowerShell
Run:
```powershell
.\run.ps1
```

### Option C: Using Maven Directly
```bash
mvn spring-boot:run
```

Once running:
* **Web UI Dashboard**: [http://localhost:8080/](http://localhost:8080/)
* **H2 In-Memory DB Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  * JDBC URL: `jdbc:h2:mem:renteasydb`
  * User: `sa`
  * Password: *(empty)*

---

## 🧪 Testing with Postman & Unit Tests

### Running Unit Tests:
```bash
mvn test
```
All 5 automated tests verify:
* Business Rule 1: Attempting to register in an occupied room throws `RoomAlreadyOccupiedException`.
* Business Rule 2: Pending dues formula strictly verified.
* Feature 5: Vacating a tenant decrements occupancy and frees the room.

### Postman Collection:
Import [RentEasy_API_Collection.postman_collection.json](file:///c:/Users/tamil/OneDrive/Documents/Desktop/PG%20Hostel%20Room%20Rent%20Payment%20Tracker/RentEasy_API_Collection.postman_collection.json) into Postman to run pre-configured requests covering:
1. Normal success paths for all 5 core features.
2. Edge cases (409 Conflict for occupied room assignment).
3. Duplicate payment rejection (409 Conflict).
4. Unpaid current month filtering.
