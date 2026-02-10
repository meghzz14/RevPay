package com.revature.revpay.service.impl;

import com.revature.revpay.dao.InvoiceDAO;
import com.revature.revpay.dao.UserDAO;
import com.revature.revpay.dao.impl.InvoiceDAOImpl;
import com.revature.revpay.dao.impl.UserDAOImpl;
import com.revature.revpay.model.Invoice;
import com.revature.revpay.model.User;
import com.revature.revpay.service.InvoiceService;
import com.revature.revpay.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceDAO invoiceDAO;
    private final UserDAO userDAO;
    private final TransactionService transactionService;
    
    public InvoiceServiceImpl() {
        this.invoiceDAO = new InvoiceDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.transactionService = new TransactionServiceImpl();
    }
    
    @Override
    public Invoice createInvoice(Long businessUserId, String customerIdentifier,
                                BigDecimal amount, String description, String dueDate) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        // Find customer
        Optional<User> customerOpt = userDAO.findByEmail(customerIdentifier);
        if (customerOpt.isEmpty()) {
            customerOpt = userDAO.findByPhoneNumber(customerIdentifier);
        }
        if (customerOpt.isEmpty()) {
            try {
                Long userId = Long.parseLong(customerIdentifier);
                customerOpt = userDAO.findById(userId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Customer not found");
            }
        }
        
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found");
        }
        
        User customer = customerOpt.get();
        String invoiceNumber = "INV" + System.currentTimeMillis();
        LocalDate dueDateParsed = LocalDate.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE);
        
        Invoice invoice = new Invoice(businessUserId, customer.getEmail(), customer.getFullName(), 
                                      invoiceNumber, amount, description, dueDateParsed);
        invoice.setCustomerId(customer.getUserId());
        invoice.setStatus(Invoice.InvoiceStatus.SENT);
        
        return invoiceDAO.save(invoice);
    }
    
    @Override
    public List<Invoice> getInvoicesByIssuer(Long businessUserId) {
        return invoiceDAO.findByBusinessUserId(businessUserId);
    }
    
    @Override
    public List<Invoice> getInvoicesByCustomer(Long customerId) {
        return invoiceDAO.findByCustomerId(customerId);
    }
    
    @Override
    public boolean payInvoice(Long invoiceId, Long payerId) {
        Optional<Invoice> invoiceOpt = invoiceDAO.findById(invoiceId);
        if (invoiceOpt.isEmpty()) {
            return false;
        }
        
        Invoice invoice = invoiceOpt.get();
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice already paid");
        }
        
        try {
            // Process payment
            transactionService.sendMoney(payerId, String.valueOf(invoice.getBusinessUserId()), 
                                       invoice.getAmount(), "Payment for invoice: " + invoice.getInvoiceNumber());
            
            // Update invoice status
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoice.setPaidAt(LocalDateTime.now());
            invoiceDAO.update(invoice);
            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to pay invoice: " + e.getMessage());
        }
    }
    
    @Override
    public boolean cancelInvoice(Long invoiceId, Long businessUserId) {
        Optional<Invoice> invoiceOpt = invoiceDAO.findById(invoiceId);
        if (invoiceOpt.isEmpty()) {
            return false;
        }
        
        Invoice invoice = invoiceOpt.get();
        
        if (!invoice.getBusinessUserId().equals(businessUserId)) {
            throw new IllegalArgumentException("You are not authorized to cancel this invoice");
        }
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new IllegalStateException("Cannot cancel paid invoice");
        }
        
        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
        return invoiceDAO.update(invoice);
    }
}
