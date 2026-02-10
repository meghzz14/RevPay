package com.revature.revpay.console;

import com.revature.revpay.model.Invoice;
import com.revature.revpay.model.Loan;
import com.revature.revpay.model.MoneyRequest;
import com.revature.revpay.model.Notification;
import com.revature.revpay.model.PaymentMethod;
import com.revature.revpay.model.Transaction;
import com.revature.revpay.model.User;
import com.revature.revpay.model.Wallet;
import com.revature.revpay.service.InvoiceService;
import com.revature.revpay.service.LoanService;
import com.revature.revpay.service.MoneyRequestService;
import com.revature.revpay.service.NotificationService;
import com.revature.revpay.service.PaymentMethodService;
import com.revature.revpay.service.TransactionService;
import com.revature.revpay.service.UserService;
import com.revature.revpay.service.impl.TransactionServiceImpl;
import com.revature.revpay.service.WalletService;
import com.revature.revpay.service.impl.InvoiceServiceImpl;
import com.revature.revpay.service.impl.LoanServiceImpl;
import com.revature.revpay.service.impl.WalletServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class RevPayApplication {
    private final Scanner scanner;
    private final UserService userService;
    private final TransactionService transactionService;
    private final MoneyRequestService moneyRequestService;
    private final PaymentMethodService paymentMethodService;
    private final NotificationService notificationService;
    private final WalletService walletService;
    private final InvoiceService invoiceService;
    private final LoanService loanService;
    private User currentUser;
    
    public RevPayApplication() {
        this.scanner = new Scanner(System.in);
        this.userService = new UserService();
        this.transactionService = new TransactionServiceImpl();
        this.moneyRequestService = new MoneyRequestService();
        this.paymentMethodService = new PaymentMethodService();
        this.notificationService = new NotificationService();
        this.walletService = new WalletServiceImpl();
        this.invoiceService = new InvoiceServiceImpl();
        this.loanService = new LoanServiceImpl();
    }
    
    public void start() {
        System.out.println("=================================");
        System.out.println("    Welcome to RevPay System    ");
        System.out.println("=================================");
        
        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                showUserDashboard();
            }
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Register Personal Account");
        System.out.println("3. Register Business Account");
        System.out.println("4. Forgot Password");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handlePersonalRegistration();
                break;
            case 3:
                handleBusinessRegistration();
                break;
            case 4:
                handleForgotPassword();
                break;
            case 5:
                System.out.println("Thank you for using RevPay!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    private void showUserDashboard() {
        System.out.println("\n--- RevPay Dashboard ---");
        System.out.println("Welcome, " + currentUser.getFullName() + "!");
        System.out.println("Account Type: " + currentUser.getAccountType());
        
        System.out.println("\n1. Send Money");
        System.out.println("2. Request Money");
        System.out.println("3. View Transaction History");
        System.out.println("4. Manage Payment Methods");
        System.out.println("5. Wallet Management");
        System.out.println("6. Notifications");
        System.out.println("7. Change Password");
        
        if (currentUser.getAccountType() == User.AccountType.BUSINESS) {
            System.out.println("8. Business Features");
        }
        
        System.out.println("9. Logout");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                handleSendMoney();
                break;
            case 2:
                handleRequestMoney();
                break;
            case 3:
                handleTransactionHistory();
                break;
            case 4:
                handlePaymentMethods();
                break;
            case 5:
                handleWalletManagement();
                break;
            case 6:
                handleNotifications();
                break;
            case 7:
                handleChangePassword();
                break;
            case 8:
                if (currentUser.getAccountType() == User.AccountType.BUSINESS) {
                    handleBusinessFeatures();
                } else {
                    System.out.println("Invalid option.");
                }
                break;
            case 9:
                currentUser = null;
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    private void handleLogin() {
        System.out.println("\n--- Login ---");
        System.out.print("Enter email or phone number: ");
        String emailOrPhone = scanner.nextLine().trim();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        try {
            Optional<User> userOpt = userService.login(emailOrPhone, password);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                System.out.println("Login successful!");
                
                // Simulate 2FA
                String securityCode = generateSecurityCode();
                System.out.println("Security Code sent to your registered phone: " + securityCode);
                System.out.print("Enter security code: ");
                String enteredCode = scanner.nextLine();
                
                if (!securityCode.equals(enteredCode)) {
                    System.out.println("Invalid security code. Login failed.");
                    currentUser = null;
                }
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void handlePersonalRegistration() {
        System.out.println("\n--- Personal Account Registration ---");
        
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();
        
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Phone Number: ");
        String phoneNumber = scanner.nextLine().trim();
        
        System.out.print("Password (min 8 chars, uppercase, lowercase, digit): ");
        String password = scanner.nextLine().trim();
        
        System.out.print("Transaction PIN (4-6 digits): ");
        String transactionPin = scanner.nextLine().trim();
        
        System.out.print("Security Question 1: ");
        String securityQuestion1 = scanner.nextLine().trim();
        
        System.out.print("Security Answer 1: ");
        String securityAnswer1 = scanner.nextLine().trim();
        
        System.out.print("Security Question 2: ");
        String securityQuestion2 = scanner.nextLine().trim();
        
        System.out.print("Security Answer 2: ");
        String securityAnswer2 = scanner.nextLine().trim();
        
        try {
            User user = userService.registerPersonalUser(fullName, email, phoneNumber, password, 
                                                        transactionPin, securityQuestion1, securityAnswer1,
                                                        securityQuestion2, securityAnswer2);
            System.out.println("\nPersonal account registered successfully! User ID: " + user.getUserId());
        } catch (IllegalArgumentException e) {
            System.out.println("\nRegistration failed: " + e.getMessage());
        }
    }
    
    private void handleBusinessRegistration() {
        System.out.println("\n--- Business Account Registration ---");
        
        // Get personal details first
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();
        
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Phone Number: ");
        String phoneNumber = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        System.out.print("Transaction PIN: ");
        String transactionPin = scanner.nextLine().trim();
        
        System.out.print("Security Question 1: ");
        String securityQuestion1 = scanner.nextLine().trim();
        
        System.out.print("Security Answer 1: ");
        String securityAnswer1 = scanner.nextLine().trim();
        
        System.out.print("Security Question 2: ");
        String securityQuestion2 = scanner.nextLine().trim();
        
        System.out.print("Security Answer 2: ");
        String securityAnswer2 = scanner.nextLine().trim();
        
        // Get business details
        System.out.print("Business Name: ");
        String businessName = scanner.nextLine().trim();
        
        System.out.print("Business Type: ");
        String businessType = scanner.nextLine().trim();
        
        System.out.print("Tax ID: ");
        String taxId = scanner.nextLine().trim();
        
        System.out.print("Business Address: ");
        String businessAddress = scanner.nextLine().trim();
        
        try {
            User user = userService.registerBusinessUser(fullName, email, phoneNumber, password, 
                                                       transactionPin, securityQuestion1, securityAnswer1,
                                                       securityQuestion2, securityAnswer2, businessName, 
                                                       businessType, taxId, businessAddress);
            System.out.println("\nBusiness account registered successfully! User ID: " + user.getUserId());
        } catch (IllegalArgumentException e) {
            System.out.println("\nRegistration failed: " + e.getMessage());
        }
    }
    
    private void handleForgotPassword() {
        System.out.println("\n--- Password Recovery ---");
        System.out.print("Enter email or phone number: ");
        String emailOrPhone = scanner.nextLine().trim();
        
        // Find user and display security questions
        Optional<User> userOpt = userService.findUserByEmail(emailOrPhone);
        if (userOpt.isEmpty()) {
            userOpt = userService.findUserByPhone(emailOrPhone);
        }
        
        if (userOpt.isEmpty()) {
            System.out.println("User not found.");
            return;
        }
        
        User user = userOpt.get();
        System.out.println("\n" + user.getSecurityQuestion1());
        System.out.print("Answer: ");
        String answer1 = scanner.nextLine().trim();
        
        System.out.println("\n" + user.getSecurityQuestion2());
        System.out.print("Answer: ");
        String answer2 = scanner.nextLine().trim();
        
        System.out.print("\nEnter new password: ");
        String newPassword = scanner.nextLine();
        
        if (userService.resetPasswordWithSecurityQuestions(emailOrPhone, answer1, answer2, newPassword)) {
            System.out.println("\nPassword reset successfully!");
        } else {
            System.out.println("\nPassword reset failed. Please check your answers.");
        }
    }
    
    private void handleSendMoney() {
        System.out.println("\n--- Send Money ---");
        
        System.out.print("Enter recipient (email/phone/user ID): ");
        String recipient = scanner.nextLine().trim();
        
        System.out.print("Enter amount: ");
        String amountStr = scanner.nextLine().trim();
        
        System.out.print("Enter note (optional): ");
        String note = scanner.nextLine().trim();
        
        System.out.print("Enter transaction PIN: ");
        String pin = scanner.nextLine().trim();
        
        // Verify PIN
        if (!userService.verifyTransactionPin(currentUser.getUserId(), pin)) {
            System.out.println("\nInvalid transaction PIN!");
            return;
        }
        
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            Transaction transaction = transactionService.sendMoney(currentUser.getUserId(), recipient, amount, note);
            
            // Create notifications
            String senderMessage = "You sent $" + amount + " to " + recipient;
            if (note != null && !note.isEmpty()) {
                senderMessage += " (Note: " + note + ")";
            }
            notificationService.createNotification(currentUser.getUserId(), 
                Notification.NotificationType.TRANSACTION, 
                "Money Sent", 
                senderMessage);
            
            String receiverMessage = "You received $" + amount + " from " + currentUser.getFullName();
            if (note != null && !note.isEmpty()) {
                receiverMessage += " (Note: " + note + ")";
            }
            notificationService.createNotification(transaction.getToUserId(), 
                Notification.NotificationType.TRANSACTION, 
                "Money Received", 
                receiverMessage);
            
            System.out.println("\n✓ Money sent successfully!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("Reference: " + transaction.getReferenceNumber());
            System.out.println("Amount: $" + amount);
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid amount format!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\nTransaction failed: " + e.getMessage());
        }
    }
    
    private void handleRequestMoney() {
        System.out.println("\n--- Request Money ---");
        System.out.println("1. Create Money Request");
        System.out.println("2. View My Requests");
        System.out.println("3. View Pending Requests for Me");
        System.out.println("4. Back");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                createMoneyRequest();
                break;
            case 2:
                viewMyRequests();
                break;
            case 3:
                viewPendingRequestsForMe();
                break;
            case 4:
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void createMoneyRequest() {
        System.out.println("\n--- Create Money Request ---");
        
        System.out.print("Request from (email/phone/user ID): ");
        String requestFrom = scanner.nextLine().trim();
        
        System.out.print("Enter amount: ");
        String amountStr = scanner.nextLine().trim();
        
        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();
        
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            MoneyRequest request = moneyRequestService.createMoneyRequest(
                currentUser.getUserId(), requestFrom, amount, description);
            
            // Create notification for requester
            notificationService.createNotification(currentUser.getUserId(), 
                Notification.NotificationType.MONEY_REQUEST, 
                "Money Request Sent", 
                "You requested $" + amount + " from " + requestFrom + " (" + description + ")");
            
            // Create notification for recipient
            notificationService.createNotification(request.getRequestedFromId(), 
                Notification.NotificationType.MONEY_REQUEST, 
                "Money Request Received", 
                currentUser.getFullName() + " requested $" + amount + " from you (" + description + ")");
            
            System.out.println("\n✓ Money request created successfully!");
            System.out.println("Request ID: " + request.getRequestId());
            System.out.println("Amount: $" + amount);
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid amount format!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nRequest failed: " + e.getMessage());
        }
    }
    
    private void viewMyRequests() {
        System.out.println("\n--- My Money Requests ---");
        
        List<MoneyRequest> requests = moneyRequestService.getMyRequests(currentUser.getUserId());
        
        if (requests.isEmpty()) {
            System.out.println("No1 requests found.");
            return;
        }
        
        System.out.println("\nTotal Requests: " + requests.size());
        System.out.println("\n" + "=".repeat(80));
        
        for (MoneyRequest req : requests) {
            System.out.println("Request ID: " + req.getRequestId());
            System.out.println("Amount: $" + req.getAmount());
            System.out.println("Description: " + req.getDescription());
            System.out.println("Status: " + req.getStatus());
            System.out.println("Created: " + req.getCreatedAt());
            System.out.println("-".repeat(80));
        }
        
        if (requests.stream().anyMatch(r -> r.getStatus() == MoneyRequest.RequestStatus.PENDING)) {
            System.out.print("\nCancel a request? (Enter Request ID or 0 to skip): ");
            String input = scanner.nextLine().trim();
            try {
                Long requestId = Long.parseLong(input);
                if (requestId > 0) {
                    if (moneyRequestService.cancelRequest(requestId, currentUser.getUserId())) {
                        System.out.println("Request cancelled successfully!");
                    } else {
                        System.out.println("Failed to cancel request.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void viewPendingRequestsForMe() {
        System.out.println("\n--- Pending Requests for Me ---");
        
        List<MoneyRequest> requests = moneyRequestService.getPendingRequestsForMe(currentUser.getUserId());
        
        if (requests.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }
        
        System.out.println("\nTotal Pending Requests: " + requests.size());
        System.out.println("\n" + "=".repeat(80));
        
        for (MoneyRequest req : requests) {
            System.out.println("Request ID: " + req.getRequestId());
            System.out.println("Amount: $" + req.getAmount());
            System.out.println("Description: " + req.getDescription());
            System.out.println("Created: " + req.getCreatedAt());
            System.out.println("-".repeat(80));
        }
        
        System.out.print("\nRespond to a request? (Enter Request ID or 0 to skip): ");
        String input = scanner.nextLine().trim();
        try {
            Long requestId = Long.parseLong(input);
            if (requestId > 0) {
                System.out.print("Accept or Decline? (A/D): ");
                String response = scanner.nextLine().trim().toUpperCase();
                
                if (response.equals("A")) {
                    System.out.print("Enter transaction PIN: ");
                    String pin = scanner.nextLine().trim();
                    
                    if (!userService.verifyTransactionPin(currentUser.getUserId(), pin)) {
                        System.out.println("\nInvalid transaction PIN!");
                        return;
                    }
                    
                    if (moneyRequestService.acceptRequest(requestId, currentUser.getUserId())) {
                        System.out.println("\n✓ Request accepted and money sent!");
                    } else {
                        System.out.println("\nFailed to accept request.");
                    }
                } else if (response.equals("D")) {
                    if (moneyRequestService.declineRequest(requestId, currentUser.getUserId())) {
                        System.out.println("\nRequest declined.");
                    } else {
                        System.out.println("\nFailed to decline request.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    private void handleTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        
        System.out.print("Enter transaction PIN: ");
        String pin = scanner.nextLine().trim();
        
        // Verify PIN
        if (!userService.verifyTransactionPin(currentUser.getUserId(), pin)) {
            System.out.println("\nInvalid transaction PIN!");
            return;
        }
        
        List<Transaction> transactions = transactionService.getTransactionHistory(currentUser.getUserId());
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        System.out.println("\nTotal Transactions: " + transactions.size());
        System.out.println("\n" + "=".repeat(80));
        
        for (Transaction txn : transactions) {
            String direction = txn.getFromUserId() != null && txn.getFromUserId().equals(currentUser.getUserId()) 
                             ? "SENT" : "RECEIVED";
            
            System.out.println("ID: " + txn.getTransactionId() + " | " + direction + " | $" + txn.getAmount());
            System.out.println("Type: " + txn.getType() + " | Status: " + txn.getStatus());
            System.out.println("Description: " + txn.getDescription());
            if (txn.getNote() != null && !txn.getNote().isEmpty()) {
                System.out.println("Note: " + txn.getNote());
            }
            System.out.println("Date: " + txn.getCreatedAt());
            System.out.println("Reference: " + txn.getReferenceNumber());
            System.out.println("-".repeat(80));
        }
    }
    
    private void handlePaymentMethods() {
        System.out.println("\n--- Payment Methods ---");
        System.out.println("1. View Payment Methods");
        System.out.println("2. Add Credit/Debit Card");
        System.out.println("3. Add Bank Account");
        System.out.println("4. Set Default Payment Method");
        System.out.println("5. Remove Payment Method");
        System.out.println("6. Back");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                viewPaymentMethods();
                break;
            case 2:
                addCard();
                break;
            case 3:
                addBankAccount();
                break;
            case 4:
                setDefaultPaymentMethod();
                break;
            case 5:
                removePaymentMethod();
                break;
            case 6:
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void viewPaymentMethods() {
        System.out.println("\n--- My Payment Methods ---");
        
        List<PaymentMethod> methods = paymentMethodService.getPaymentMethods(currentUser.getUserId());
        
        if (methods.isEmpty()) {
            System.out.println("No payment methods found.");
            return;
        }
        
        System.out.println("\nTotal Payment Methods: " + methods.size());
        System.out.println("\n" + "=".repeat(80));
        
        for (PaymentMethod method : methods) {
            System.out.println("ID: " + method.getPaymentMethodId());
            System.out.println("Type: " + method.getType());
            
            if (method.getType() == PaymentMethod.PaymentType.CREDIT_CARD || 
                method.getType() == PaymentMethod.PaymentType.DEBIT_CARD) {
                System.out.println("Card Holder: " + method.getCardHolderName());
                System.out.println("Card Number: " + method.getMaskedCardNumber());
                System.out.println("Expiry: " + method.getExpiryMonth() + "/" + method.getExpiryYear());
            } else if (method.getType() == PaymentMethod.PaymentType.BANK_ACCOUNT) {
                System.out.println("Bank: " + method.getBankName());
                System.out.println("Account: ****" + method.getAccountNumber().substring(Math.max(0, method.getAccountNumber().length() - 4)));
            }
            
            System.out.println("Default: " + (method.isDefault() ? "Yes" : "No"));
            System.out.println("Added: " + method.getCreatedAt());
            System.out.println("-".repeat(80));
        }
    }
    
    private void addCard() {
        System.out.println("\n--- Add Credit/Debit Card ---");
        
        System.out.print("Card Type (1=Credit, 2=Debit): ");
        int typeChoice = getIntInput();
        PaymentMethod.PaymentType type = typeChoice == 1 ? 
            PaymentMethod.PaymentType.CREDIT_CARD : PaymentMethod.PaymentType.DEBIT_CARD;
        
        System.out.print("Card Holder Name: ");
        String cardHolderName = scanner.nextLine().trim();
        
        System.out.print("Card Number (13-19 digits): ");
        String cardNumber = scanner.nextLine().trim().replaceAll("\\s+", "");
        
        System.out.print("Expiry Month (MM): ");
        String expiryMonth = scanner.nextLine().trim();
        
        System.out.print("Expiry Year (YYYY): ");
        String expiryYear = scanner.nextLine().trim();
        
        System.out.print("CVV: ");
        String cvv = scanner.nextLine().trim();
        
        try {
            PaymentMethod method = paymentMethodService.addCard(currentUser.getUserId(), type, 
                cardNumber, cardHolderName, expiryMonth, expiryYear, cvv);
            
            // Create notification
            notificationService.createNotification(currentUser.getUserId(), 
                Notification.NotificationType.CARD_CHANGE, 
                "Payment Method Added", 
                "A new " + type + " has been added to your account");
            
            System.out.println("\n✓ Card added successfully!");
            System.out.println("Payment Method ID: " + method.getPaymentMethodId());
            System.out.println("Card: " + method.getMaskedCardNumber());
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    private void addBankAccount() {
        System.out.println("\n--- Add Bank Account ---");
        
        System.out.print("Bank Name: ");
        String bankName = scanner.nextLine().trim();
        
        System.out.print("Account Number: ");
        String accountNumber = scanner.nextLine().trim();
        
        System.out.print("Routing Number: ");
        String routingNumber = scanner.nextLine().trim();
        
        try {
            PaymentMethod method = paymentMethodService.addBankAccount(currentUser.getUserId(), 
                bankName, accountNumber, routingNumber);
            
            System.out.println("\n✓ Bank account added successfully!");
            System.out.println("Payment Method ID: " + method.getPaymentMethodId());
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    private void setDefaultPaymentMethod() {
        viewPaymentMethods();
        
        System.out.print("\nEnter Payment Method ID to set as default: ");
        String input = scanner.nextLine().trim();
        
        try {
            Long paymentMethodId = Long.parseLong(input);
            if (paymentMethodService.setDefaultPaymentMethod(currentUser.getUserId(), paymentMethodId)) {
                System.out.println("\n✓ Default payment method updated!");
            } else {
                System.out.println("\nFailed to update default payment method.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input.");
        }
    }
    
    private void removePaymentMethod() {
        viewPaymentMethods();
        
        System.out.print("\nEnter Payment Method ID to remove: ");
        String input = scanner.nextLine().trim();
        
        try {
            Long paymentMethodId = Long.parseLong(input);
            if (paymentMethodService.removePaymentMethod(paymentMethodId)) {
                System.out.println("\n✓ Payment method removed!");
            } else {
                System.out.println("\nFailed to remove payment method.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input.");
        }
    }
    
    private void handleWalletManagement() {
        System.out.println("\n--- Wallet Management ---");
        
        Optional<Wallet> walletOpt = transactionService.getWallet(currentUser.getUserId());
        if (walletOpt.isEmpty()) {
            System.out.println("Wallet not found!");
            return;
        }
        
        Wallet wallet = walletOpt.get();
        System.out.println("\nCurrent Balance: $" + wallet.getBalance());
        System.out.println("Last Updated: " + wallet.getLastUpdated());
        
        System.out.println("\n1. Add Money");
        System.out.println("2. Withdraw Money");
        System.out.println("3. View Balance");
        System.out.println("4. Back");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        if (choice == 1) {
            System.out.print("\nEnter amount to add: ");
            String amountStr = scanner.nextLine().trim();
            
            System.out.print("Enter transaction PIN: ");
            String pin = scanner.nextLine().trim();
            
            // Verify PIN
            if (!userService.verifyTransactionPin(currentUser.getUserId(), pin)) {
                System.out.println("\nInvalid transaction PIN!");
                return;
            }
            
            try {
                BigDecimal amount = new BigDecimal(amountStr);
                if (transactionService.addMoneyToWallet(currentUser.getUserId(), amount)) {
                    // Create notification
                    notificationService.createNotification(currentUser.getUserId(), 
                        Notification.NotificationType.TRANSACTION, 
                        "Money Added", 
                        "You added $" + amount + " to your wallet");
                    
                    System.out.println("\n✓ Money added successfully!");
                    System.out.println("New Balance: $" + wallet.getBalance().add(amount));
                } else {
                    System.out.println("\nFailed to add money.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid amount format!");
            } catch (IllegalArgumentException e) {
                System.out.println("\nError: " + e.getMessage());
            }
        } else if (choice == 2) {
            System.out.print("\nEnter amount to withdraw: ");
            String amountStr = scanner.nextLine().trim();
            
            System.out.print("Enter transaction PIN: ");
            String pin = scanner.nextLine().trim();
            
            if (!userService.verifyTransactionPin(currentUser.getUserId(), pin)) {
                System.out.println("\nInvalid transaction PIN!");
                return;
            }
            
            try {
                BigDecimal amount = new BigDecimal(amountStr);
                if (walletService.withdrawMoney(currentUser.getUserId(), amount)) {
                    notificationService.createNotification(currentUser.getUserId(), 
                        Notification.NotificationType.TRANSACTION, 
                        "Money Withdrawn", 
                        "You withdrew $" + amount + " from your wallet");
                    
                    System.out.println("\n✓ Money withdrawn successfully!");
                    walletOpt = transactionService.getWallet(currentUser.getUserId());
                    if (walletOpt.isPresent()) {
                        System.out.println("New Balance: $" + walletOpt.get().getBalance());
                    }
                } else {
                    System.out.println("\nFailed to withdraw money.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid amount format!");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("\nError: " + e.getMessage());
            }
        } else if (choice == 3) {
            System.out.print("\nEnter transaction PIN: ");
            String pin = scanner.nextLine().trim();
            
            // Verify PIN
            if (!userService.verifyTransactionPin(currentUser.getUserId(), pin)) {
                System.out.println("\nInvalid transaction PIN!");
                return;
            }
            
            // Refresh wallet balance
            walletOpt = transactionService.getWallet(currentUser.getUserId());
            if (walletOpt.isPresent()) {
                wallet = walletOpt.get();
                System.out.println("\n--- Current Balance ---");
                System.out.println("Balance: $" + wallet.getBalance());
                System.out.println("Last Updated: " + wallet.getLastUpdated());
            }
        }
    }
    
    private void handleNotifications() {
        System.out.println("\n--- Notifications ---");
        System.out.println("1. View All Notifications");
        System.out.println("2. View Unread Notifications");
        System.out.println("3. Mark All as Read");
        System.out.println("4. Back");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                viewAllNotifications();
                break;
            case 2:
                viewUnreadNotifications();
                break;
            case 3:
                markAllNotificationsAsRead();
                break;
            case 4:
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void viewAllNotifications() {
        System.out.println("\n--- All Notifications ---");
        
        List<Notification> notifications = notificationService.getAllNotifications(currentUser.getUserId());
        
        if (notifications.isEmpty()) {
            System.out.println("No notifications.");
            return;
        }
        
        int unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
        System.out.println("\nTotal: " + notifications.size() + " | Unread: " + unreadCount);
        System.out.println("\n" + "=".repeat(80));
        
        for (Notification notif : notifications) {
            String status = notif.isRead() ? "[READ]" : "[UNREAD]";
            System.out.println(status + " " + notif.getTitle());
            System.out.println("Type: " + notif.getType());
            System.out.println("Message: " + notif.getMessage());
            System.out.println("Time: " + notif.getCreatedAt());
            System.out.println("-".repeat(80));
        }
    }
    
    private void viewUnreadNotifications() {
        System.out.println("\n--- Unread Notifications ---");
        
        List<Notification> notifications = notificationService.getUnreadNotifications(currentUser.getUserId());
        
        if (notifications.isEmpty()) {
            System.out.println("No unread notifications.");
            return;
        }
        
        System.out.println("\nUnread Notifications: " + notifications.size());
        System.out.println("\n" + "=".repeat(80));
        
        for (Notification notif : notifications) {
            System.out.println("[NEW] " + notif.getTitle());
            System.out.println("Type: " + notif.getType());
            System.out.println("Message: " + notif.getMessage());
            System.out.println("Time: " + notif.getCreatedAt());
            System.out.println("-".repeat(80));
        }
        
        System.out.print("\nMark notification as read? (Enter Notification ID or 0 to skip): ");
        String input = scanner.nextLine().trim();
        try {
            Long notificationId = Long.parseLong(input);
            if (notificationId > 0) {
                if (notificationService.markAsRead(notificationId)) {
                    System.out.println("\n✓ Notification marked as read!");
                } else {
                    System.out.println("\nFailed to mark notification as read.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input.");
        }
    }
    
    private void markAllNotificationsAsRead() {
        if (notificationService.markAllAsRead(currentUser.getUserId())) {
            System.out.println("\n✓ All notifications marked as read!");
        } else {
            System.out.println("\nNo unread notifications to mark.");
        }
    }
    
    private void handleChangePassword() {
        System.out.println("\n--- Change Password ---");
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        
        System.out.print("Enter transaction PIN: ");
        String transactionPin = scanner.nextLine();
        
        try {
            if (userService.changePassword(currentUser.getUserId(), currentPassword, newPassword, transactionPin)) {
                System.out.println("Password changed successfully!");
            } else {
                System.out.println("Password change failed. Please check your credentials.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void handleBusinessFeatures() {
        System.out.println("\n--- Business Features ---");
        System.out.println("1. Invoice Management");
        System.out.println("2. Loan Management");
        System.out.println("3. Back to Dashboard");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                handleInvoiceManagement();
                break;
            case 2:
                handleLoanManagement();
                break;
            case 3:
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void handleInvoiceManagement() {
        System.out.println("\n--- Invoice Management ---");
        System.out.println("1. Create Invoice");
        System.out.println("2. View My Invoices");
        System.out.println("3. View Received Invoices");
        System.out.println("4. Pay Invoice");
        System.out.println("5. Cancel Invoice");
        System.out.println("6. Back");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                createInvoice();
                break;
            case 2:
                viewMyInvoices();
                break;
            case 3:
                viewReceivedInvoices();
                break;
            case 4:
                payInvoice();
                break;
            case 5:
                cancelInvoice();
                break;
            case 6:
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void createInvoice() {
        System.out.println("\n--- Create Invoice ---");
        
        System.out.print("Customer (email/phone/user ID): ");
        String customer = scanner.nextLine().trim();
        
        System.out.print("Amount: ");
        String amountStr = scanner.nextLine().trim();
        
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        
        System.out.print("Due date (YYYY-MM-DD): ");
        String dueDate = scanner.nextLine().trim();
        
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            Invoice invoice = invoiceService.createInvoice(currentUser.getUserId(), customer, amount, description, dueDate);
            System.out.println("\n✓ Invoice created successfully!");
            System.out.println("Invoice ID: " + invoice.getInvoiceId());
            System.out.println("Invoice Number: " + invoice.getInvoiceNumber());
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    private void viewMyInvoices() {
        System.out.println("\n--- My Invoices ---");
        
        List<Invoice> invoices = invoiceService.getInvoicesByIssuer(currentUser.getUserId());
        
        if (invoices.isEmpty()) {
            System.out.println("No invoices found.");
            return;
        }
        
        System.out.println("\nTotal Invoices: " + invoices.size());
        System.out.println("\n" + "=".repeat(80));
        
        for (Invoice inv : invoices) {
            System.out.println("Invoice #" + inv.getInvoiceNumber() + " | ID: " + inv.getInvoiceId());
            System.out.println("Amount: $" + inv.getAmount() + " | Status: " + inv.getStatus());
            System.out.println("Description: " + inv.getDescription());
            System.out.println("Due Date: " + inv.getDueDate());
            System.out.println("Created: " + inv.getCreatedAt());
            System.out.println("-".repeat(80));
        }
    }
    
    private void viewReceivedInvoices() {
        System.out.println("\n--- Received Invoices ---");
        
        List<Invoice> invoices = invoiceService.getInvoicesByCustomer(currentUser.getUserId());
        
        if (invoices.isEmpty()) {
            System.out.println("No invoices found.");
            return;
        }
        
        System.out.println("\nTotal Invoices: " + invoices.size());
        System.out.println("\n" + "=".repeat(80));
        
        for (Invoice inv : invoices) {
            System.out.println("Invoice #" + inv.getInvoiceNumber() + " | ID: " + inv.getInvoiceId());
            System.out.println("Amount: $" + inv.getAmount() + " | Status: " + inv.getStatus());
            System.out.println("Description: " + inv.getDescription());
            System.out.println("Due Date: " + inv.getDueDate());
            System.out.println("Created: " + inv.getCreatedAt());
            System.out.println("-".repeat(80));
        }
    }
    
    private void payInvoice() {
        System.out.println("\n--- Pay Invoice ---");
        
        System.out.print("Enter Invoice ID: ");
        String input = scanner.nextLine().trim();
        
        System.out.print("Enter transaction PIN: ");
        String pin = scanner.nextLine().trim();
        
        if (!userService.verifyTransactionPin(currentUser.getUserId(), pin)) {
            System.out.println("\nInvalid transaction PIN!");
            return;
        }
        
        try {
            Long invoiceId = Long.parseLong(input);
            if (invoiceService.payInvoice(invoiceId, currentUser.getUserId())) {
                System.out.println("\n✓ Invoice paid successfully!");
            } else {
                System.out.println("\nFailed to pay invoice.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    private void cancelInvoice() {
        System.out.println("\n--- Cancel Invoice ---");
        
        System.out.print("Enter Invoice ID: ");
        String input = scanner.nextLine().trim();
        
        try {
            Long invoiceId = Long.parseLong(input);
            if (invoiceService.cancelInvoice(invoiceId, currentUser.getUserId())) {
                System.out.println("\n✓ Invoice cancelled successfully!");
            } else {
                System.out.println("\nFailed to cancel invoice.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    private void handleLoanManagement() {
        System.out.println("\n--- Loan Management ---");
        System.out.println("1. Apply for Loan");
        System.out.println("2. View My Loans");
        System.out.println("3. Repay Loan");
        System.out.println("4. Back");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                applyForLoan();
                break;
            case 2:
                viewMyLoans();
                break;
            case 3:
                repayLoan();
                break;
            case 4:
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void applyForLoan() {
        System.out.println("\n--- Apply for Loan ---");
        
        System.out.print("Loan Amount: ");
        String amountStr = scanner.nextLine().trim();
        
        System.out.print("Interest Rate (%): ");
        String rateStr = scanner.nextLine().trim();
        
        System.out.print("Term (months): ");
        String termStr = scanner.nextLine().trim();
        
        System.out.print("Purpose: ");
        String purpose = scanner.nextLine().trim();
        
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            BigDecimal rate = new BigDecimal(rateStr);
            int term = Integer.parseInt(termStr);
            
            Loan loan = loanService.applyForLoan(currentUser.getUserId(), amount, rate, term, purpose);
            System.out.println("\n✓ Loan application submitted successfully!");
            System.out.println("Loan ID: " + loan.getLoanId());
            System.out.println("Amount: $" + loan.getLoanAmount());
            System.out.println("Total Repayment: $" + loan.getTotalAmount());
            System.out.println("Status: " + loan.getStatus());
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    private void viewMyLoans() {
        System.out.println("\n--- My Loans ---");
        
        List<Loan> loans = loanService.getLoansByUser(currentUser.getUserId());
        
        if (loans.isEmpty()) {
            System.out.println("No loans found.");
            return;
        }
        
        System.out.println("\nTotal Loans: " + loans.size());
        System.out.println("\n" + "=".repeat(80));
        
        for (Loan loan : loans) {
            System.out.println("Loan ID: " + loan.getLoanId());
            System.out.println("Amount: $" + loan.getLoanAmount() + " | Status: " + loan.getStatus());
            System.out.println("Interest Rate: " + loan.getInterestRate() + "% | Term: " + loan.getTermMonths() + " months");
            System.out.println("Total Amount: $" + loan.getTotalAmount());
            System.out.println("Remaining: $" + loan.getRemainingAmount());
            System.out.println("Purpose: " + loan.getPurpose());
            System.out.println("Applied: " + loan.getCreatedAt());
            System.out.println("-".repeat(80));
        }
    }
    
    private void repayLoan() {
        System.out.println("\n--- Repay Loan ---");
        
        System.out.print("Enter Loan ID: ");
        String loanIdStr = scanner.nextLine().trim();
        
        System.out.print("Enter repayment amount: ");
        String amountStr = scanner.nextLine().trim();
        
        System.out.print("Enter transaction PIN: ");
        String pin = scanner.nextLine().trim();
        
        if (!userService.verifyTransactionPin(currentUser.getUserId(), pin)) {
            System.out.println("\nInvalid transaction PIN!");
            return;
        }
        
        try {
            Long loanId = Long.parseLong(loanIdStr);
            BigDecimal amount = new BigDecimal(amountStr);
            
            if (loanService.repayLoan(loanId, currentUser.getUserId(), amount)) {
                System.out.println("\n✓ Loan repayment successful!");
            } else {
                System.out.println("\nFailed to process loan repayment.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    private String generateSecurityCode() {
        return String.valueOf(100000 + (int)(Math.random() * 900000));
    }
    
    private int getIntInput() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public static void main(String[] args) {
        new RevPayApplication().start();
    }
}