package com.revature.revpay.service;

import com.revature.revpay.dao.PaymentMethodDAO;
import com.revature.revpay.dao.impl.PaymentMethodDAOImpl;
import com.revature.revpay.model.PaymentMethod;
import com.revature.revpay.util.SecurityUtil;

import java.util.List;
import java.util.Optional;

public class PaymentMethodService {
    private final PaymentMethodDAO paymentMethodDAO;
    
    public PaymentMethodService() {
        this.paymentMethodDAO = new PaymentMethodDAOImpl();
    }
    
    public PaymentMethod addCard(Long userId, PaymentMethod.PaymentType type, String cardNumber, 
                                String cardHolderName, String expiryMonth, String expiryYear, String cvv) {
        // Validate card number
        if (cardNumber == null || cardNumber.length() < 13 || cardNumber.length() > 19) {
            throw new IllegalArgumentException("Invalid card number");
        }
        
        // Encrypt sensitive data
        String encryptedCardNumber = SecurityUtil.encrypt(cardNumber);
        String encryptedCvv = SecurityUtil.encrypt(cvv);
        
        PaymentMethod paymentMethod = new PaymentMethod(userId, type, cardHolderName);
        paymentMethod.setEncryptedCardNumber(encryptedCardNumber);
        paymentMethod.setExpiryMonth(expiryMonth);
        paymentMethod.setExpiryYear(expiryYear);
        paymentMethod.setEncryptedCvv(encryptedCvv);
        
        // Set as default if it's the first payment method
        List<PaymentMethod> existingMethods = paymentMethodDAO.findByUserId(userId);
        if (existingMethods.isEmpty()) {
            paymentMethod.setDefault(true);
        }
        
        return paymentMethodDAO.save(paymentMethod);
    }
    
    public PaymentMethod addBankAccount(Long userId, String bankName, String accountNumber, String routingNumber) {
        PaymentMethod paymentMethod = new PaymentMethod(userId, PaymentMethod.PaymentType.BANK_ACCOUNT, bankName);
        paymentMethod.setBankName(bankName);
        paymentMethod.setAccountNumber(accountNumber);
        paymentMethod.setRoutingNumber(routingNumber);
        
        // Set as default if it's the first payment method
        List<PaymentMethod> existingMethods = paymentMethodDAO.findByUserId(userId);
        if (existingMethods.isEmpty()) {
            paymentMethod.setDefault(true);
        }
        
        return paymentMethodDAO.save(paymentMethod);
    }
    
    public List<PaymentMethod> getPaymentMethods(Long userId) {
        return paymentMethodDAO.findByUserId(userId);
    }
    
    public Optional<PaymentMethod> getDefaultPaymentMethod(Long userId) {
        return paymentMethodDAO.findDefaultByUserId(userId);
    }
    
    public boolean setDefaultPaymentMethod(Long userId, Long paymentMethodId) {
        return paymentMethodDAO.setDefaultPaymentMethod(userId, paymentMethodId);
    }
    
    public boolean removePaymentMethod(Long paymentMethodId) {
        return paymentMethodDAO.delete(paymentMethodId);
    }
}
