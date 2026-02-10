package com.revature.revpay.service;

import com.revature.revpay.dao.UserDAO;
import com.revature.revpay.dao.impl.UserDAOImpl;
import com.revature.revpay.model.User;
import com.revature.revpay.util.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    
    public UserService() {
        this.userDAO = new UserDAOImpl();
    }
    
    public User registerPersonalUser(String fullName, String email, String phoneNumber, 
                                   String password, String transactionPin, 
                                   String securityQuestion1, String securityAnswer1,
                                   String securityQuestion2, String securityAnswer2) {
        
        // Validate input
        if (!SecurityUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 8 characters with uppercase, lowercase, and digit");
        }
        
        if (!SecurityUtil.isValidPin(transactionPin)) {
            throw new IllegalArgumentException("Transaction PIN must be 4-6 digits");
        }
        
        // Check if user already exists
        if (userDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        if (userDAO.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already registered");
        }
        
        // Create user
        String salt = SecurityUtil.generateSalt();
        String hashedPassword = SecurityUtil.hashPassword(password, salt);
        String hashedPin = SecurityUtil.hashPassword(transactionPin, salt);
        
        User user = new User(fullName, email, phoneNumber, hashedPassword + ":" + salt, 
                           hashedPin + ":" + salt, User.AccountType.PERSONAL);
        user.setSecurityQuestion1(securityQuestion1);
        user.setSecurityAnswer1(SecurityUtil.hashPassword(securityAnswer1.toLowerCase(), salt));
        user.setSecurityQuestion2(securityQuestion2);
        user.setSecurityAnswer2(SecurityUtil.hashPassword(securityAnswer2.toLowerCase(), salt));
        
        return userDAO.save(user);
    }
    
    public User registerBusinessUser(String fullName, String email, String phoneNumber, 
                                   String password, String transactionPin,
                                   String securityQuestion1, String securityAnswer1,
                                   String securityQuestion2, String securityAnswer2,
                                   String businessName, String businessType, 
                                   String taxId, String businessAddress) {
        
        // Validate input
        if (!SecurityUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 8 characters with uppercase, lowercase, and digit");
        }
        
        if (!SecurityUtil.isValidPin(transactionPin)) {
            throw new IllegalArgumentException("Transaction PIN must be 4-6 digits");
        }
        
        // Check if user already exists
        if (userDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        if (userDAO.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already registered");
        }
        
        // Create business user
        String salt = SecurityUtil.generateSalt();
        String hashedPassword = SecurityUtil.hashPassword(password, salt);
        String hashedPin = SecurityUtil.hashPassword(transactionPin, salt);
        
        User user = new User(fullName, email, phoneNumber, hashedPassword + ":" + salt, 
                           hashedPin + ":" + salt, User.AccountType.BUSINESS);
        user.setSecurityQuestion1(securityQuestion1);
        user.setSecurityAnswer1(SecurityUtil.hashPassword(securityAnswer1.toLowerCase(), salt));
        user.setSecurityQuestion2(securityQuestion2);
        user.setSecurityAnswer2(SecurityUtil.hashPassword(securityAnswer2.toLowerCase(), salt));
        user.setBusinessName(businessName);
        user.setBusinessType(businessType);
        user.setTaxId(taxId);
        user.setBusinessAddress(businessAddress);
        
        return userDAO.save(user);
    }
    
    public Optional<User> login(String emailOrPhone, String password) {
        Optional<User> userOpt = userDAO.findByEmail(emailOrPhone);
        if (userOpt.isEmpty()) {
            userOpt = userDAO.findByPhoneNumber(emailOrPhone);
        }
        
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // Check if account is locked
        if (user.isLocked()) {
            throw new IllegalStateException("Account is locked due to multiple failed login attempts");
        }
        
        // Verify password
        String[] passwordParts = user.getPasswordHash().split(":");
        String hashedPassword = passwordParts[0];
        String salt = passwordParts[1];
        
        if (SecurityUtil.verifyPassword(password, salt, hashedPassword)) {
            // Successful login
            user.setLastLogin(LocalDateTime.now());
            userDAO.resetFailedLoginAttempts(user.getUserId());
            userDAO.update(user);
            return Optional.of(user);
        } else {
            // Failed login
            userDAO.incrementFailedLoginAttempts(user.getUserId());
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            
            if (user.getFailedLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                userDAO.lockAccount(user.getUserId());
                throw new IllegalStateException("Account locked due to multiple failed login attempts");
            }
            
            return Optional.empty();
        }
    }
    
    public boolean verifyTransactionPin(Long userId, String pin) {
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        String[] pinParts = user.getTransactionPin().split(":");
        String hashedPin = pinParts[0];
        String salt = pinParts[1];
        
        return SecurityUtil.verifyPassword(pin, salt, hashedPin);
    }
    
    public boolean changePassword(Long userId, String currentPassword, String newPassword, String transactionPin) {
        if (!SecurityUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("New password must be at least 8 characters with uppercase, lowercase, and digit");
        }
        
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // Verify current password and transaction PIN
        String[] passwordParts = user.getPasswordHash().split(":");
        String currentHashedPassword = passwordParts[0];
        String salt = passwordParts[1];
        
        if (!SecurityUtil.verifyPassword(currentPassword, salt, currentHashedPassword)) {
            return false;
        }
        
        if (!verifyTransactionPin(userId, transactionPin)) {
            return false;
        }
        
        // Update password
        String newSalt = SecurityUtil.generateSalt();
        String newHashedPassword = SecurityUtil.hashPassword(newPassword, newSalt);
        user.setPasswordHash(newHashedPassword + ":" + newSalt);
        
        return userDAO.update(user);
    }
    
    public boolean resetPasswordWithSecurityQuestions(String emailOrPhone, String answer1, String answer2, String newPassword) {
        Optional<User> userOpt = userDAO.findByEmail(emailOrPhone);
        if (userOpt.isEmpty()) {
            userOpt = userDAO.findByPhoneNumber(emailOrPhone);
        }
        
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // Verify security answers
        String[] passwordParts = user.getPasswordHash().split(":");
        String salt = passwordParts[1];
        
        String hashedAnswer1 = SecurityUtil.hashPassword(answer1.toLowerCase(), salt);
        String hashedAnswer2 = SecurityUtil.hashPassword(answer2.toLowerCase(), salt);
        
        if (!hashedAnswer1.equals(user.getSecurityAnswer1()) || !hashedAnswer2.equals(user.getSecurityAnswer2())) {
            return false;
        }
        
        // Reset password
        String newSalt = SecurityUtil.generateSalt();
        String newHashedPassword = SecurityUtil.hashPassword(newPassword, newSalt);
        user.setPasswordHash(newHashedPassword + ":" + newSalt);
        
        // Unlock account if locked
        if (user.isLocked()) {
            user.setLocked(false);
            user.setFailedLoginAttempts(0);
        }
        
        return userDAO.update(user);
    }
    
    public List<User> searchUsers(String searchTerm) {
        return userDAO.findByFullNameContaining(searchTerm);
    }
    
    public Optional<User> findUserById(Long userId) {
        return userDAO.findById(userId);
    }
    
    public Optional<User> findUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }
    
    public Optional<User> findUserByPhone(String phone) {
        return userDAO.findByPhoneNumber(phone);
    }
}