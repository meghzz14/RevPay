# RevPay - Digital Payment Application

## Overview
RevPay is a console-based financial application that enables secure digital payments and money management for both personal and business users. The application provides comprehensive features for sending/requesting money, managing payment methods, transaction tracking, and business-specific functionalities.

## Features

### Personal Account Features
- **User Registration & Authentication**
  - Secure registration with email/phone verification
  - Password hashing with bcrypt
  - Two-factor authentication simulation
  - Security questions for password recovery
  - Account lockout after failed login attempts

- **Money Management**
  - Send money to other users (by username, email, phone, or account ID)
  - Request money from other users
  - Wallet balance management
  - Add money from cards or withdraw to bank accounts

- **Payment Methods**
  - Add/manage credit/debit cards with AES-256 encryption
  - Set default payment methods
  - Secure card information storage

- **Transaction History**
  - Comprehensive transaction tracking
  - Filter by type, date range, amount, and status
  - Search and export capabilities

- **Notifications**
  - In-app notifications for all activities
  - Categorized notifications (transactions, requests, alerts)
  - Read/unread status tracking

### Business Account Features
All personal features plus:

- **Business Registration**
  - Business name, type, tax ID, and address
  - Document verification simulation

- **Payment Processing**
  - Accept payments from customers
  - Generate payment requests and invoices
  - Multiple customer lookup methods

- **Invoice Management**
  - Create itemized invoices
  - Track paid/unpaid status
  - Customer information management
  - Payment terms configuration

- **Business Loans**
  - Loan application with financial information
  - Application status tracking
  - Repayment management

- **Business Analytics**
  - Transaction summaries and revenue reports
  - Outstanding invoice tracking
  - Payment trends analysis
  - Top customer identification

## Technology Stack
- **Java 11** - Core application development
- **Oracle Database** - Data persistence
- **JDBC** - Database connectivity
- **Maven** - Dependency management and build tool
- **JUnit 5** - Unit testing framework
- **Log4J 2** - Logging framework
- **AES-256** - Data encryption
- **SHA-256** - Password hashing

## Project Structure
```
RevPay/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/revature/revpay/
│   │   │       ├── console/          # Console interface
│   │   │       ├── dao/              # Data Access Objects
│   │   │       │   └── impl/         # DAO implementations
│   │   │       ├── model/            # Entity classes
│   │   │       ├── service/          # Business logic
│   │   │       └── util/             # Utility classes
│   │   └── resources/
│   │       ├── schema.sql            # Database schema
│   │       └── log4j2.xml           # Logging configuration
│   └── test/
│       └── java/                     # Unit tests
├── pom.xml                          # Maven configuration
└── README.md                        # Project documentation
```

## Database Schema
The application uses Oracle Database with the following main tables:
- `users` - User account information
- `wallets` - User wallet balances
- `transactions` - All financial transactions
- `payment_methods` - User payment methods (encrypted)
- `money_requests` - Money request tracking
- `notifications` - In-app notifications
- `invoices` - Business invoices
- `loans` - Business loan applications

## Security Features
- **Password Security**: SHA-256 hashing with salt
- **Data Encryption**: AES-256 for sensitive data (card numbers, CVV)
- **Account Protection**: Lockout after failed login attempts
- **Transaction Security**: Separate transaction PIN
- **Two-Factor Authentication**: Simulated security codes
- **Session Management**: Timeout and secure logout

## Setup Instructions

### Prerequisites
- Java 11 or higher
- Oracle Database (XE recommended for development)
- Maven 3.6+

### Database Setup
1. Install Oracle Database XE
2. Create a user named `revpay` with password `password`
3. Grant necessary privileges to the user
4. Run the schema.sql file to create tables and sample data

### Application Setup
1. Clone or download the project
2. Update database connection details in `DatabaseUtil.java` if needed
3. Compile and run using Maven:
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.revature.revpay.console.RevPayApplication"
   ```

### Building Executable JAR
```bash
mvn clean package
java -jar target/revpay-1.0.0.jar
```

## Usage

### Running the Application
1. Start the application
2. Choose from the main menu:
   - Login with existing account
   - Register new personal account
   - Register new business account
   - Recover forgotten password

### Sample Accounts
The database includes sample accounts for testing:
- **Personal Account**: john.doe@email.com / password123
- **Business Account**: jane.smith@business.com / password123

## Testing
Run unit tests using Maven:
```bash
mvn test
```

## Logging
The application uses Log4J 2 for comprehensive logging:
- Console output for user interactions
- File logging for debugging and audit trails
- Rolling file appenders for log management

## Future Enhancements
- Web-based interface using Spring Boot
- Mobile application development
- Real payment gateway integration
- Advanced analytics and reporting
- Microservices architecture migration
- Real-time notifications
- Enhanced security features

## Contributing
1. Fork the repository
2. Create a feature branch
3. Implement changes with proper testing
4. Submit a pull request

## License
This project is developed for educational purposes as part of Revature training program.

## Contact
For questions or support, please contact the development team.