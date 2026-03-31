-- Create sequences
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE wallet_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE payment_method_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE transaction_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE money_request_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE notification_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE invoice_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE loan_seq START WITH 1 INCREMENT BY 1;

-- Users table
CREATE TABLE users (
    user_id NUMBER PRIMARY KEY,
    full_name VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) UNIQUE NOT NULL,
    phone_number VARCHAR2(20) UNIQUE NOT NULL,
    password_hash VARCHAR2(500) NOT NULL,
    transaction_pin VARCHAR2(500) NOT NULL,
    security_question1 VARCHAR2(200),
    security_answer1 VARCHAR2(500),
    security_question2 VARCHAR2(200),
    security_answer2 VARCHAR2(500),
    account_type VARCHAR2(20) NOT NULL,
    status VARCHAR2(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    failed_login_attempts NUMBER DEFAULT 0,
    is_locked NUMBER(1) DEFAULT 0,
    business_name VARCHAR2(200),
    business_type VARCHAR2(100),
    tax_id VARCHAR2(50),
    business_address VARCHAR2(500)
);

CREATE TABLE wallets (
    wallet_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    balance NUMBER(15,2) DEFAULT 0.00,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE payment_methods (
    payment_method_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    type VARCHAR2(20) NOT NULL,
    encrypted_card_number VARCHAR2(500),
    card_holder_name VARCHAR2(100),
    expiry_month VARCHAR2(2),
    expiry_year VARCHAR2(4),
    encrypted_cvv VARCHAR2(500),
    bank_name VARCHAR2(100),
    account_number VARCHAR2(100),
    routing_number VARCHAR2(20),
    is_default NUMBER(1) DEFAULT 0,
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE transactions (
    transaction_id NUMBER PRIMARY KEY,
    from_user_id NUMBER,
    to_user_id NUMBER,
    amount NUMBER(15,2) NOT NULL,
    type VARCHAR2(30) NOT NULL,
    status VARCHAR2(20) DEFAULT 'PENDING',
    description VARCHAR2(500),
    note VARCHAR2(1000),
    reference_number VARCHAR2(50) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (from_user_id) REFERENCES users(user_id),
    FOREIGN KEY (to_user_id) REFERENCES users(user_id)
);

CREATE TABLE money_requests (
    request_id NUMBER PRIMARY KEY,
    requester_id NUMBER NOT NULL,
    requested_from_id NUMBER NOT NULL,
    amount NUMBER(15,2) NOT NULL,
    description VARCHAR2(500),
    status VARCHAR2(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users(user_id),
    FOREIGN KEY (requested_from_id) REFERENCES users(user_id)
);

CREATE TABLE notifications (
    notification_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    type VARCHAR2(30) NOT NULL,
    title VARCHAR2(200) NOT NULL,
    message VARCHAR2(1000) NOT NULL,
    is_read NUMBER(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE invoices (
    invoice_id NUMBER PRIMARY KEY,
    business_user_id NUMBER NOT NULL,
    customer_id NUMBER,
    customer_email VARCHAR2(100),
    customer_name VARCHAR2(100),
    invoice_number VARCHAR2(50) UNIQUE NOT NULL,
    amount NUMBER(15,2) NOT NULL,
    description VARCHAR2(1000),
    status VARCHAR2(20) DEFAULT 'DRAFT',
    due_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP,
    FOREIGN KEY (business_user_id) REFERENCES users(user_id),
    FOREIGN KEY (customer_id) REFERENCES users(user_id)
);

CREATE TABLE loans (
    loan_id NUMBER PRIMARY KEY,
    business_user_id NUMBER NOT NULL,
    loan_amount NUMBER(15,2) NOT NULL,
    purpose VARCHAR2(500),
    status VARCHAR2(20) DEFAULT 'APPLIED',
    interest_rate NUMBER(5,2),
    term_months NUMBER,
    monthly_payment NUMBER(15,2),
    outstanding_balance NUMBER(15,2),
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    disbursed_at TIMESTAMP,
    FOREIGN KEY (business_user_id) REFERENCES users(user_id)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_transactions_from_user ON transactions(from_user_id);
CREATE INDEX idx_transactions_to_user ON transactions(to_user_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_wallets_user_id ON wallets(user_id);
CREATE INDEX idx_payment_methods_user_id ON payment_methods(user_id);

CREATE OR REPLACE TRIGGER trg_user_id
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    SELECT user_seq.NEXTVAL INTO :NEW.user_id FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER trg_wallet_id
BEFORE INSERT ON wallets
FOR EACH ROW
BEGIN
    SELECT wallet_seq.NEXTVAL INTO :NEW.wallet_id FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER trg_payment_method_id
BEFORE INSERT ON payment_methods
FOR EACH ROW
BEGIN
    SELECT payment_method_seq.NEXTVAL INTO :NEW.payment_method_id FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER trg_transaction_id
BEFORE INSERT ON transactions
FOR EACH ROW
BEGIN
    SELECT transaction_seq.NEXTVAL INTO :NEW.transaction_id FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER trg_money_request_id
BEFORE INSERT ON money_requests
FOR EACH ROW
BEGIN
    SELECT money_request_seq.NEXTVAL INTO :NEW.request_id FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER trg_notification_id
BEFORE INSERT ON notifications
FOR EACH ROW
BEGIN
    SELECT notification_seq.NEXTVAL INTO :NEW.notification_id FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER trg_invoice_id
BEFORE INSERT ON invoices
FOR EACH ROW
BEGIN
    SELECT invoice_seq.NEXTVAL INTO :NEW.invoice_id FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER trg_loan_id
BEFORE INSERT ON loans
FOR EACH ROW
BEGIN
    SELECT loan_seq.NEXTVAL INTO :NEW.loan_id FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER trg_create_wallet
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    INSERT INTO wallets (user_id, balance) VALUES (:NEW.user_id, 0.00);
END;
/

COMMIT;
