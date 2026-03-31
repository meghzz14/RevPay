# RevPay Project - Core Features & File Locations

## Project Structure
```
RevPay/
├── src/
│   ├── main/
│   │   ├── java/com/revature/revpay/
│   │   │   ├── console/
│   │   │   │   └── RevPayApplication.java (Main Entry Point)
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   ├── TransactionService.java
│   │   │   │   ├── WalletService.java
│   │   │   │   ├── MoneyRequestService.java
│   │   │   │   ├── PaymentMethodService.java
│   │   │   │   ├── NotificationService.java
│   │   │   │   ├── InvoiceService.java
│   │   │   │   └── LoanService.java
│   │   │   ├── dao/
│   │   │   │   └── impl/
│   │   │   │       ├── UserDAOImpl.java
│   │   │   │       ├── TransactionDAOImpl.java
│   │   │   │       ├── WalletDAOImpl.java
│   │   │   │       ├── MoneyRequestDAOImpl.java
│   │   │   │       ├── PaymentMethodDAOImpl.java
│   │   │   │       ├── NotificationDAOImpl.java
│   │   │   │       ├── InvoiceDAOImpl.java
│   │   │   │       └── LoanDAOImpl.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Wallet.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── PaymentMethod.java
│   │   │   │   ├── MoneyRequest.java
│   │   │   │   ├── Notification.java
│   │   │   │   ├── Invoice.java
│   │   │   │   └── Loan.java
│   │   │   └── util/
│   │   │       ├── DatabaseUtil.java
│   │   │       └── SecurityUtil.java
│   │   └── resources/
│   │       ├── schema.sql
│   │       ├── schema_compatible.sql
│   │       └── log4j2.xml
│   └── test/
│       └── java/com/revature/revpay/service/
│           └── UserServiceTest.java
└── pom.xml
```

---

## 1. USER REGISTRATION

### Console Layer
**File:** `RevPayApplication.java`
- **Line 195** - `handlePersonalRegistration()` - Personal account registration
- **Line 226** - `handleBusinessRegistration()` - Business account registration

### Service Layer
**File:** `UserService.java`
- **Line 18** - `registerPersonalUser()` - Validates and creates personal account
- **Line 58** - `registerBusinessUser()` - Validates and creates business account

### DAO Layer
**File:** `UserDAOImpl.java`
- **Line 13** - `save(User user)` - Inserts user into database

### Database
**File:** `schema_compatible.sql`
- **Line 11** - `CREATE TABLE users` - User table definition
- **Line 9** - `CREATE SEQUENCE user_seq` - Auto-increment user IDs

### Security
**File:** `SecurityUtil.java`
- `hashPassword()` - Hash password with salt
- `isValidPassword()` - Validate password strength (min 8 chars, uppercase, lowercase, digit)
- `isValidPin()` - Validate PIN (4-6 digits)

---

## 2. USER LOGIN

### Console Layer
**File:** `RevPayApplication.java`
- **Line 148** - `handleLogin()` - Login with email/phone and password
- **Line 165** - 2FA security code verification

### Service Layer
**File:** `UserService.java`
- **Line 98** - `login(String emailOrPhone, String password)` - Authenticate user
- **Line 138** - `verifyTransactionPin()` - Verify transaction PIN

### DAO Layer
**File:** `UserDAOImpl.java`
- **Line 78** - `findByEmail()` - Find user by email
- **Line 97** - `findByPhoneNumber()` - Find user by phone
- **Line 233** - `incrementFailedLoginAttempts()` - Track failed logins
- **Line 247** - `resetFailedLoginAttempts()` - Reset on successful login
- **Line 261** - `lockAccount()` - Lock after 3 failed attempts
- **Line 275** - `unlockAccount()` - Unlock account

### Features
- Password verification with salt
- Account locking after 3 failed attempts
- 2FA with security code
- Last login timestamp tracking

---

## 3. SEND MONEY

### Console Layer
**File:** `RevPayApplication.java`
- **Line 330** - `handleSendMoney()` - Send money to another user
- **Line 336** - Enter recipient (email/phone/user ID)
- **Line 342** - Enter amount and note
- **Line 345** - Verify transaction PIN

### Service Layer
**File:** `TransactionServiceImpl.java`
- **Line 45** - `sendMoney()` - Process money transfer
  - Validate amount
  - Find recipient
  - Check sender balance
  - Deduct from sender wallet
  - Add to recipient wallet
  - Create transaction record
  - Generate reference number

### DAO Layer
**File:** `TransactionDAOImpl.java`
- **Line 13** - `save(Transaction)` - Save transaction record

**File:** `WalletDAOImpl.java`
- **Line 50** - `updateBalance()` - Update wallet balance

### Database
**File:** `schema_compatible.sql`
- **Line 56** - `CREATE TABLE transactions` - Transaction records
- **Line 24** - `CREATE TABLE wallets` - User wallet balances

### Features
- Real-time balance validation
- Atomic transaction (both wallets updated together)
- Transaction reference number generation
- Notification to both sender and receiver

---

## 4. REQUEST MONEY

### Console Layer
**File:** `RevPayApplication.java`
- **Line 382** - `handleRequestMoney()` - Money request menu
- **Line 401** - `createMoneyRequest()` - Create new request
- **Line 430** - `viewMyRequests()` - View sent requests
- **Line 467** - `viewPendingRequestsForMe()` - View received requests
- **Line 489** - Accept/Decline request

### Service Layer
**File:** `MoneyRequestService.java`
- `createMoneyRequest()` - Create money request
- `acceptRequest()` - Accept and transfer money
- `declineRequest()` - Decline request
- `cancelRequest()` - Cancel sent request
- `getMyRequests()` - Get requests I sent
- `getPendingRequestsForMe()` - Get requests I received

### DAO Layer
**File:** `MoneyRequestDAOImpl.java`
- `save()` - Save money request
- `findById()` - Find request by ID
- `updateStatus()` - Update request status (PENDING/ACCEPTED/DECLINED/CANCELLED)

### Database
**File:** `schema_compatible.sql`
- **Line 73** - `CREATE TABLE money_requests` - Money request records

### Features
- Request money from any user
- View all sent/received requests
- Accept/decline/cancel requests
- Automatic money transfer on acceptance

---

## 5. WALLET MANAGEMENT

### Console Layer
**File:** `RevPayApplication.java`
- **Line 729** - `handleWalletManagement()` - Wallet menu
- **Line 752** - Add money to wallet
- **Line 783** - Withdraw money from wallet
- **Line 810** - View current balance

### Service Layer
**File:** `TransactionServiceImpl.java`
- **Line 103** - `addMoneyToWallet()` - Add money to wallet
- **Line 28** - `getWallet()` - Get wallet details

**File:** `WalletServiceImpl.java`
- **Line 45** - `withdrawMoney()` - Withdraw money from wallet

### DAO Layer
**File:** `WalletDAOImpl.java`
- **Line 13** - `findByUserId()` - Get user's wallet
- **Line 50** - `updateBalance()` - Update wallet balance

### Database
**File:** `schema_compatible.sql`
- **Line 24** - `CREATE TABLE wallets` - Wallet table
- **Line 155** - `CREATE TRIGGER trg_create_wallet` - Auto-create wallet on user registration

### Features
- Add money from external source
- Withdraw money to bank account
- Real-time balance display
- Transaction PIN verification required
- Automatic wallet creation on registration

---

## 6. PAYMENT METHODS

### Console Layer
**File:** `RevPayApplication.java`
- **Line 639** - `handlePaymentMethods()` - Payment methods menu
- **Line 668** - `viewPaymentMethods()` - View all saved payment methods
- **Line 702** - `addCard()` - Add credit/debit card
- **Line 738** - `addBankAccount()` - Add bank account
- **Line 758** - `setDefaultPaymentMethod()` - Set default payment method
- **Line 776** - `removePaymentMethod()` - Remove payment method

### Service Layer
**File:** `PaymentMethodService.java`
- `addCard()` - Add and encrypt card details
- `addBankAccount()` - Add bank account
- `getPaymentMethods()` - Get all payment methods
- `setDefaultPaymentMethod()` - Set default
- `removePaymentMethod()` - Delete payment method

### DAO Layer
**File:** `PaymentMethodDAOImpl.java`
- `save()` - Save payment method
- `findByUserId()` - Get user's payment methods
- `update()` - Update payment method
- `delete()` - Remove payment method

### Database
**File:** `schema_compatible.sql`
- **Line 32** - `CREATE TABLE payment_methods` - Payment methods table

### Features
- Store multiple cards and bank accounts
- Encrypted card number and CVV storage
- Masked card display (****1234)
- Set default payment method
- Support for CREDIT_CARD, DEBIT_CARD, BANK_ACCOUNT

---

## 7. TRANSACTION HISTORY

### Console Layer
**File:** `RevPayApplication.java`
- **Line 545** - `handleTransactionHistory()` - View transaction history
- **Line 551** - Verify transaction PIN before viewing
- **Line 559** - Display all transactions with details

### Service Layer
**File:** `TransactionServiceImpl.java`
- **Line 133** - `getTransactionHistory()` - Get all user transactions

### DAO Layer
**File:** `TransactionDAOImpl.java`
- **Line 116** - `findByUserId()` - Get transactions by user ID

### Database
**File:** `schema_compatible.sql`
- **Line 56** - `CREATE TABLE transactions`
- **Line 142** - `CREATE INDEX idx_transactions_from_user` - Fast lookup
- **Line 143** - `CREATE INDEX idx_transactions_to_user` - Fast lookup

### Features
- View all sent and received transactions
- Transaction details: amount, type, status, date, reference number
- PIN verification required for security
- Shows direction (SENT/RECEIVED)

---

## 8. NOTIFICATIONS

### Console Layer
**File:** `RevPayApplication.java`
- **Line 827** - `handleNotifications()` - Notifications menu
- **Line 847** - `viewAllNotifications()` - View all notifications
- **Line 873** - `viewUnreadNotifications()` - View unread only
- **Line 906** - `markAllNotificationsAsRead()` - Mark all as read

### Service Layer
**File:** `NotificationService.java`
- `createNotification()` - Create new notification
- `getAllNotifications()` - Get all notifications
- `getUnreadNotifications()` - Get unread notifications
- `getUnreadCount()` - Count unread notifications
- `markAsRead()` - Mark single notification as read
- `markAllAsRead()` - Mark all as read

### DAO Layer
**File:** `NotificationDAOImpl.java`
- `save()` - Save notification
- `findByUserId()` - Get user's notifications
- `update()` - Update notification status

### Database
**File:** `schema_compatible.sql`
- **Line 85** - `CREATE TABLE notifications` - Notifications table

### Notification Types
- TRANSACTION - Money sent/received
- MONEY_REQUEST - Request sent/received
- CARD_CHANGE - Payment method added/removed
- LOW_BALANCE - Wallet balance low
- INVOICE - Invoice created/paid
- LOAN_UPDATE - Loan status change
- SECURITY_ALERT - Security events

---

## 9. CHANGE PASSWORD

### Console Layer
**File:** `RevPayApplication.java`
- **Line 916** - `handleChangePassword()` - Change password
- **Line 919** - Enter current password
- **Line 922** - Enter new password
- **Line 925** - Verify transaction PIN

### Service Layer
**File:** `UserService.java`
- **Line 157** - `changePassword()` - Change user password
  - Verify current password
  - Verify transaction PIN
  - Validate new password strength
  - Hash new password with new salt
  - Update in database

### DAO Layer
**File:** `UserDAOImpl.java`
- **Line 145** - `update(User)` - Update user record

### Features
- Requires current password verification
- Requires transaction PIN verification
- New password strength validation
- New salt generated for security

---

## 10. FORGOT PASSWORD

### Console Layer
**File:** `RevPayApplication.java`
- **Line 279** - `handleForgotPassword()` - Password recovery
- **Line 282** - Enter email or phone
- **Line 295** - Answer security questions
- **Line 304** - Set new password

### Service Layer
**File:** `UserService.java`
- **Line 189** - `resetPasswordWithSecurityQuestions()` - Reset password
  - Find user by email/phone
  - Verify security question answers
  - Reset password
  - Unlock account if locked

### Features
- Two security questions verification
- Automatic account unlock on successful reset
- No PIN required (user forgot password)

---

## 11. INVOICE MANAGEMENT (Business Accounts Only)

### Console Layer
**File:** `RevPayApplication.java`
- **Line 960** - `handleInvoiceManagement()` - Invoice menu
- **Line 982** - `createInvoice()` - Create new invoice
- **Line 1003** - `viewMyInvoices()` - View issued invoices
- **Line 1027** - `viewReceivedInvoices()` - View invoices to pay
- **Line 1051** - `payInvoice()` - Pay an invoice
- **Line 1076** - `cancelInvoice()` - Cancel invoice

### Service Layer
**File:** `InvoiceServiceImpl.java`
- `createInvoice()` - Create invoice for customer
- `getInvoicesByIssuer()` - Get invoices I created
- `getInvoicesByCustomer()` - Get invoices I received
- `payInvoice()` - Pay invoice (transfers money)
- `cancelInvoice()` - Cancel unpaid invoice

### DAO Layer
**File:** `InvoiceDAOImpl.java`
- `save()` - Save invoice
- `findByBusinessUserId()` - Get business invoices
- `findByCustomerId()` - Get customer invoices
- `update()` - Update invoice status

### Database
**File:** `schema_compatible.sql`
- **Line 103** - `CREATE TABLE invoices` - Invoice records

### Invoice Status
- DRAFT - Created but not sent
- SENT - Sent to customer
- PAID - Customer paid
- OVERDUE - Past due date
- CANCELLED - Cancelled by business

### Features
- Create invoices with due dates
- Send to customers by email/phone/user ID
- Track invoice status
- Automatic payment processing
- Invoice number generation

---

## 12. LOAN MANAGEMENT (Business Accounts Only)

### Console Layer
**File:** `RevPayApplication.java`
- **Line 1095** - `handleLoanManagement()` - Loan menu
- **Line 1117** - `applyForLoan()` - Apply for business loan
- **Line 1145** - `viewMyLoans()` - View all loans
- **Line 1173** - `repayLoan()` - Make loan repayment

### Service Layer
**File:** `LoanServiceImpl.java`
- `applyForLoan()` - Submit loan application
  - Calculate total amount with interest
  - Calculate monthly payment
  - Set outstanding balance
- `getLoansByUser()` - Get user's loans
- `repayLoan()` - Process loan repayment
  - Deduct from wallet
  - Update outstanding balance
  - Mark as REPAID if fully paid

### DAO Layer
**File:** `LoanDAOImpl.java`
- `save()` - Save loan application
- `findByUserId()` - Get user's loans
- `update()` - Update loan status

### Database
**File:** `schema_compatible.sql`
- **Line 121** - `CREATE TABLE loans` - Loan records

### Loan Status
- APPLIED - Application submitted
- UNDER_REVIEW - Being reviewed
- APPROVED - Loan approved
- REJECTED - Application rejected
- DISBURSED - Money disbursed to account
- REPAID - Fully repaid

### Features
- Apply for business loans
- Interest rate calculation
- Monthly payment calculation
- Track outstanding balance
- Partial and full repayments

---

## 13. SECURITY UTILITIES

### File: `SecurityUtil.java`

**Password Hashing:**
- `hashPassword(String password, String salt)` - Hash password with SHA-256
- `verifyPassword(String password, String salt, String hashedPassword)` - Verify password
- `generateSalt()` - Generate random 16-byte salt

**Validation:**
- `isValidPassword(String password)` - Validate password strength
  - Minimum 8 characters
  - At least one uppercase letter
  - At least one lowercase letter
  - At least one digit
- `isValidPin(String pin)` - Validate PIN format (4-6 digits)

**Encryption:**
- `encryptCardNumber(String cardNumber)` - Encrypt card number
- `decryptCardNumber(String encryptedCardNumber)` - Decrypt card number
- `getMaskedCardNumber(String cardNumber)` - Mask card (**** **** **** 1234)

### Security Features
- SHA-256 password hashing
- Unique salt per user
- Encrypted card storage
- Transaction PIN verification
- Account locking mechanism

---

## 14. DATABASE CONNECTION

### File: `DatabaseUtil.java`

**Methods:**
- **Line 21** - `getConnection()` - Get Oracle database connection
- **Line 26** - `closeConnection()` - Close database connection

**Configuration:**
- **Line 9** - URL: `jdbc:oracle:thin:@localhost:1521/XE`
- **Line 10** - Username: `SYSTEM`
- **Line 11** - Password: `12345`

**Driver:**
- **Line 15** - Oracle JDBC Driver: `oracle.jdbc.driver.OracleDriver`

### Connection Pool
- Static connection management
- Automatic driver loading
- Exception handling for connection errors

---

## 15. DATA MODELS

### Location: `src/main/java/com/revature/revpay/model/`

**User.java**
- User account information
- Account type (PERSONAL/BUSINESS)
- Status (ACTIVE/INACTIVE/SUSPENDED)
- Security questions and answers
- Business details (for business accounts)

**Wallet.java**
- User wallet balance
- Last updated timestamp

**Transaction.java**
- Transaction details
- Type (SEND_MONEY, REQUEST_MONEY, ADD_MONEY, etc.)
- Status (PENDING, COMPLETED, FAILED, etc.)
- Reference number

**PaymentMethod.java**
- Payment method details
- Type (CREDIT_CARD, DEBIT_CARD, BANK_ACCOUNT)
- Encrypted card/account information
- Default flag

**MoneyRequest.java**
- Money request details
- Status (PENDING, ACCEPTED, DECLINED, CANCELLED)
- Requester and recipient information

**Notification.java**
- Notification details
- Type (TRANSACTION, MONEY_REQUEST, etc.)
- Read/unread status

**Invoice.java**
- Invoice details
- Status (DRAFT, SENT, PAID, OVERDUE, CANCELLED)
- Business and customer information

**Loan.java**
- Loan application details
- Status (APPLIED, APPROVED, DISBURSED, REPAID, etc.)
- Interest rate and payment information

---

## 16. DATABASE SCHEMA

### File: `schema_compatible.sql`

**Sequences:**
- **Line 2** - `user_seq` - User ID sequence
- **Line 3** - `wallet_seq` - Wallet ID sequence
- **Line 4** - `payment_method_seq` - Payment method ID sequence
- **Line 5** - `transaction_seq` - Transaction ID sequence
- **Line 6** - `money_request_seq` - Money request ID sequence
- **Line 7** - `notification_seq` - Notification ID sequence
- **Line 8** - `invoice_seq` - Invoice ID sequence
- **Line 9** - `loan_seq` - Loan ID sequence

**Tables:**
- **Line 11** - `users` - User accounts
- **Line 24** - `wallets` - User wallets
- **Line 32** - `payment_methods` - Stored payment methods
- **Line 56** - `transactions` - Transaction records
- **Line 73** - `money_requests` - Money requests
- **Line 85** - `notifications` - User notifications
- **Line 103** - `invoices` - Business invoices
- **Line 121** - `loans` - Business loans

**Indexes:**
- **Line 140** - `idx_users_email` - Fast email lookup
- **Line 141** - `idx_users_phone` - Fast phone lookup
- **Line 142-147** - Transaction and notification indexes

**Triggers:**
- **Line 149-154** - Auto-increment triggers for all tables
- **Line 155** - `trg_create_wallet` - Auto-create wallet on user registration

---

## 17. MAIN ENTRY POINT

### File: `RevPayApplication.java`

**Application Start:**
- **Line 1272** - `main(String[] args)` - Application entry point
- **Line 51** - `start()` - Main application loop
- **Line 56** - `showMainMenu()` - Login/Register menu
- **Line 95** - `showUserDashboard()` - User dashboard after login

**Main Menu Options:**
1. Login
2. Register Personal Account
3. Register Business Account
4. Forgot Password
5. Exit

**User Dashboard Options:**
1. Send Money
2. Request Money
3. View Transaction History
4. Manage Payment Methods
5. Wallet Management
6. Notifications
7. Change Password
8. Business Features (if business account)
9. Logout

---

## 18. MAVEN CONFIGURATION

### File: `pom.xml`

**Dependencies:**
- Oracle JDBC Driver (ojdbc8) - Version 21.7.0.0
- Log4j Core - Version 2.20.0
- Log4j API - Version 2.20.0
- JUnit Jupiter - Version 5.9.2 (for testing)
- Mockito - Version 5.1.1 (for testing)

**Build Configuration:**
- Java Version: 11
- Maven Compiler Plugin: 3.11.0
- Maven Surefire Plugin: 3.0.0-M9 (for tests)
- Maven Shade Plugin: 3.4.1 (for executable JAR)

**Main Class:**
- `com.revature.revpay.console.RevPayApplication`

---

## 19. LOGGING CONFIGURATION

### File: `log4j2.xml`

**Location:** `src/main/resources/log4j2.xml`

**Log Levels:**
- ERROR - Critical errors
- WARN - Warnings
- INFO - Information messages
- DEBUG - Debug messages

**Appenders:**
- Console - Output to console
- File - Output to log file

---

## 20. TESTING

### File: `UserServiceTest.java`

**Location:** `src/test/java/com/revature/revpay/service/UserServiceTest.java`

**Test Cases:**
- User registration (valid/invalid)
- Login (success/failure/account locking)
- Password change
- Password reset with security questions
- PIN verification

**Testing Framework:**
- JUnit 5 for test execution
- Mockito for mocking DAO layer

**Note:** Test file currently disabled due to dependency issues

---

## QUICK REFERENCE - FEATURE FLOW

```
User Action (Console)
    ↓
RevPayApplication.java (Presentation Layer)
    ↓
Service Layer (Business Logic & Validation)
    ↓
DAO Layer (Database Operations)
    ↓
DatabaseUtil.java (Connection Management)
    ↓
Oracle Database (Data Storage)
```

---

## INTERVIEW PREPARATION CHECKLIST

### Know These Files Well:
1. ✅ `RevPayApplication.java` - Main application flow
2. ✅ `UserService.java` - User management logic
3. ✅ `TransactionServiceImpl.java` - Transaction processing
4. ✅ `UserDAOImpl.java` - Database operations
5. ✅ `DatabaseUtil.java` - Database connection
6. ✅ `SecurityUtil.java` - Security features
7. ✅ `schema_compatible.sql` - Database schema

### Key Concepts to Explain:
- DAO Pattern (Data Access Object)
- Service Layer Pattern
- Password hashing with salt
- PreparedStatement (SQL injection prevention)
- Transaction management
- Exception handling
- JDBC connection management

### Common Interview Questions:
1. "How does user registration work?" → Explain UserService.registerPersonalUser()
2. "How do you secure passwords?" → Explain SecurityUtil hashing
3. "How do you prevent SQL injection?" → Explain PreparedStatement
4. "What is DAO pattern?" → Explain separation of concerns
5. "How does money transfer work?" → Explain TransactionServiceImpl.sendMoney()

---

## PROJECT STATISTICS

- **Total Java Files:** 30+
- **Total Lines of Code:** ~5000+
- **Database Tables:** 8
- **Core Features:** 12
- **Security Features:** 5
- **Design Patterns:** 3 (DAO, Service Layer, Singleton)

---

**Created for:** Meghana
**Project:** RevPay - Digital Payment Application
**Technology Stack:** Java 11, Oracle Database, JDBC, Maven, Log4j
**Date:** February 2026
