# RevPay Entity Relationship Diagram (ERD)

## Database Design Overview

The RevPay application uses a relational database design with the following entities and relationships:

## Entities and Attributes

### 1. USERS
**Primary Key:** user_id (NUMBER, Auto-generated)
- full_name (VARCHAR2(100)) - User's full name
- email (VARCHAR2(100), UNIQUE) - Email address for login
- phone_number (VARCHAR2(20), UNIQUE) - Phone number for login
- password_hash (VARCHAR2(500)) - Encrypted password with salt
- transaction_pin (VARCHAR2(500)) - Encrypted transaction PIN with salt
- security_question1 (VARCHAR2(200)) - First security question
- security_answer1 (VARCHAR2(500)) - Encrypted answer to first question
- security_question2 (VARCHAR2(200)) - Second security question
- security_answer2 (VARCHAR2(500)) - Encrypted answer to second question
- account_type (VARCHAR2(20)) - PERSONAL or BUSINESS
- status (VARCHAR2(20)) - ACTIVE, INACTIVE, or SUSPENDED
- created_at (TIMESTAMP) - Account creation timestamp
- last_login (TIMESTAMP) - Last successful login
- failed_login_attempts (NUMBER) - Count of failed login attempts
- is_locked (NUMBER(1)) - Account lock status (0/1)
- business_name (VARCHAR2(200)) - Business name (for business accounts)
- business_type (VARCHAR2(100)) - Type of business
- tax_id (VARCHAR2(50)) - Business tax identification
- business_address (VARCHAR2(500)) - Business address

### 2. WALLETS 
**Primary Key:** wallet_id (NUMBER, Auto-generated)
**Foreign Key:** user_id → USERS(user_id)
- balance (NUMBER(15,2)) - Current wallet balance
- last_updated (TIMESTAMP) - Last balance update timestamp

### 3. PAYMENT_METHODS
**Primary Key:** payment_method_id (NUMBER, Auto-generated)
**Foreign Key:** user_id → USERS(user_id)
- type (VARCHAR2(20)) - CREDIT_CARD, DEBIT_CARD, or BANK_ACCOUNT
- encrypted_card_number (VARCHAR2(500)) - AES-256 encrypted card number
- card_holder_name (VARCHAR2(100)) - Name on card
- expiry_month (VARCHAR2(2)) - Card expiry month
- expiry_year (VARCHAR2(4)) - Card expiry year
- encrypted_cvv (VARCHAR2(500)) - AES-256 encrypted CVV
- bank_name (VARCHAR2(100)) - Bank name for bank accounts
- account_number (VARCHAR2(100)) - Bank account number
- routing_number (VARCHAR2(20)) - Bank routing number
- is_default (NUMBER(1)) - Default payment method flag
- is_active (NUMBER(1)) - Active status flag
- created_at (TIMESTAMP) - Creation timestamp

### 4. TRANSACTIONS
**Primary Key:** transaction_id (NUMBER, Auto-generated)
**Foreign Keys:** 
- from_user_id → USERS(user_id)
- to_user_id → USERS(user_id)
- amount (NUMBER(15,2)) - Transaction amount
- type (VARCHAR2(30)) - Transaction type (SEND_MONEY, REQUEST_MONEY, etc.)
- status (VARCHAR2(20)) - PENDING, COMPLETED, FAILED, CANCELLED, DECLINED
- description (VARCHAR2(500)) - Transaction description
- note (VARCHAR2(1000)) - Optional user note
- reference_number (VARCHAR2(50), UNIQUE) - Unique transaction reference
- created_at (TIMESTAMP) - Transaction creation time
- completed_at (TIMESTAMP) - Transaction completion time

### 5. MONEY_REQUESTS
**Primary Key:** request_id (NUMBER, Auto-generated)
**Foreign Keys:**
- requester_id → USERS(user_id)
- requested_from_id → USERS(user_id)
- amount (NUMBER(15,2)) - Requested amount
- description (VARCHAR2(500)) - Request description
- status (VARCHAR2(20)) - PENDING, ACCEPTED, DECLINED, CANCELLED
- created_at (TIMESTAMP) - Request creation time
- responded_at (TIMESTAMP) - Response timestamp

### 6. NOTIFICATIONS
**Primary Key:** notification_id (NUMBER, Auto-generated)
**Foreign Key:** user_id → USERS(user_id)
- type (VARCHAR2(30)) - Notification type
- title (VARCHAR2(200)) - Notification title
- message (VARCHAR2(1000)) - Notification message
- is_read (NUMBER(1)) - Read status flag
- created_at (TIMESTAMP) - Creation timestamp

### 7. INVOICES (Business Accounts Only)
**Primary Key:** invoice_id (NUMBER, Auto-generated)
**Foreign Key:** business_user_id → USERS(user_id)
- customer_email (VARCHAR2(100)) - Customer email
- customer_name (VARCHAR2(100)) - Customer name
- invoice_number (VARCHAR2(50), UNIQUE) - Unique invoice number
- amount (NUMBER(15,2)) - Invoice amount
- description (VARCHAR2(1000)) - Invoice description
- status (VARCHAR2(20)) - DRAFT, SENT, PAID, OVERDUE, CANCELLED
- due_date (DATE) - Payment due date
- created_at (TIMESTAMP) - Invoice creation time
- paid_at (TIMESTAMP) - Payment timestamp

### 8. LOANS (Business Accounts Only)
**Primary Key:** loan_id (NUMBER, Auto-generated)
**Foreign Key:** business_user_id → USERS(user_id)
- loan_amount (NUMBER(15,2)) - Requested loan amount
- purpose (VARCHAR2(500)) - Loan purpose description
- status (VARCHAR2(20)) - Application status
- interest_rate (NUMBER(5,2)) - Annual interest rate
- term_months (NUMBER) - Loan term in months
- monthly_payment (NUMBER(15,2)) - Monthly payment amount
- outstanding_balance (NUMBER(15,2)) - Remaining balance
- applied_at (TIMESTAMP) - Application timestamp
- approved_at (TIMESTAMP) - Approval timestamp
- disbursed_at (TIMESTAMP) - Disbursement timestamp

## Relationships

### One-to-One Relationships
- USERS ↔ WALLETS (Each user has exactly one wallet)

### One-to-Many Relationships
- USERS → PAYMENT_METHODS (One user can have multiple payment methods)
- USERS → TRANSACTIONS (as from_user_id - One user can initiate multiple transactions)
- USERS → TRANSACTIONS (as to_user_id - One user can receive multiple transactions)
- USERS → MONEY_REQUESTS (as requester_id - One user can make multiple requests)
- USERS → MONEY_REQUESTS (as requested_from_id - One user can receive multiple requests)
- USERS → NOTIFICATIONS (One user can have multiple notifications)
- USERS → INVOICES (One business user can create multiple invoices)
- USERS → LOANS (One business user can apply for multiple loans)

## Indexes for Performance
- idx_users_email ON users(email)
- idx_users_phone ON users(phone_number)
- idx_transactions_from_user ON transactions(from_user_id)
- idx_transactions_to_user ON transactions(to_user_id)
- idx_transactions_created_at ON transactions(created_at)
- idx_notifications_user_id ON notifications(user_id)
- idx_wallets_user_id ON wallets(user_id)
- idx_payment_methods_user_id ON payment_methods(user_id)

## Database Triggers
- trg_create_wallet: Automatically creates a wallet when a new user is registered

## Security Considerations
- All sensitive data (passwords, PINs, card numbers, CVV) are encrypted
- Passwords use SHA-256 hashing with salt
- Card information uses AES-256 encryption
- Foreign key constraints ensure data integrity
- Unique constraints prevent duplicate emails and phone numbers

## Business Rules Enforced by Database
- Account types are restricted to PERSONAL or BUSINESS
- Transaction statuses follow defined workflow
- Payment method types are validated
- Notification types are categorized
- Invoice and loan statuses follow business workflows

This ERD design supports all the functional requirements of the RevPay application while maintaining data integrity, security, and performance.