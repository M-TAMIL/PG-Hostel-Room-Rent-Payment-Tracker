-- =====================================================================
-- RentEasy - PG / Hostel Room Rent Payment Tracker Database Schema
-- Database: MySQL / PostgreSQL Compatible
-- =====================================================================

CREATE DATABASE IF NOT EXISTS renteasy_db;
USE renteasy_db;

-- 1. Table: rooms
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(50) NOT NULL UNIQUE,
    room_type VARCHAR(20) NOT NULL, -- SINGLE, DOUBLE, TRIPLE, DELUXE
    capacity INT NOT NULL DEFAULT 1,
    current_occupancy INT NOT NULL DEFAULT 0,
    monthly_rent DOUBLE NOT NULL,
    is_occupied BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_room_number (room_number),
    INDEX idx_room_occupied (is_occupied)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Table: tenants
CREATE TABLE IF NOT EXISTS tenants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    move_in_date DATE NOT NULL,
    move_out_date DATE,
    monthly_rent DOUBLE NOT NULL,
    security_deposit DOUBLE DEFAULT 0.0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, VACATED
    room_id BIGINT,
    INDEX idx_tenant_status (status),
    INDEX idx_tenant_phone (phone),
    CONSTRAINT fk_tenants_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Table: rent_payments
CREATE TABLE IF NOT EXISTS rent_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    payment_month VARCHAR(7) NOT NULL, -- Format: YYYY-MM
    payment_date DATE NOT NULL,
    payment_mode VARCHAR(20) NOT NULL DEFAULT 'CASH', -- CASH, UPI, BANK_TRANSFER, CHEQUE
    receipt_number VARCHAR(50) UNIQUE,
    transaction_reference VARCHAR(100),
    notes VARCHAR(255),
    CONSTRAINT uq_tenant_month UNIQUE (tenant_id, payment_month),
    CONSTRAINT fk_payments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    INDEX idx_payment_month (payment_month),
    INDEX idx_payment_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
