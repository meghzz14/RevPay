package com.revature.revpay.service;

import com.revature.revpay.dao.MoneyRequestDAO;
import com.revature.revpay.dao.UserDAO;
import com.revature.revpay.dao.impl.MoneyRequestDAOImpl;
import com.revature.revpay.dao.impl.UserDAOImpl;
import com.revature.revpay.model.MoneyRequest;
import com.revature.revpay.model.User;
import com.revature.revpay.service.impl.TransactionServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MoneyRequestService {
    private final MoneyRequestDAO moneyRequestDAO;
    private final UserDAO userDAO;
    private final TransactionService transactionService;
    
    public MoneyRequestService() {
        this.moneyRequestDAO = new MoneyRequestDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.transactionService = new TransactionServiceImpl();
    }
    
    public MoneyRequest createMoneyRequest(Long requesterId, String requestedFromIdentifier, 
                                          BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        // Find user to request from
        Optional<User> requestedFromOpt = userDAO.findByEmail(requestedFromIdentifier);
        if (requestedFromOpt.isEmpty()) {
            requestedFromOpt = userDAO.findByPhoneNumber(requestedFromIdentifier);
        }
        if (requestedFromOpt.isEmpty()) {
            try {
                Long userId = Long.parseLong(requestedFromIdentifier);
                requestedFromOpt = userDAO.findById(userId);
            } catch (NumberFormatException e) {
                // Not a valid user ID
            }
        }
        
        if (requestedFromOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User requestedFrom = requestedFromOpt.get();
        
        if (requesterId.equals(requestedFrom.getUserId())) {
            throw new IllegalArgumentException("Cannot request money from yourself");
        }
        
        MoneyRequest request = new MoneyRequest(requesterId, requestedFrom.getUserId(), amount, description);
        return moneyRequestDAO.save(request);
    }
    
    public List<MoneyRequest> getMyRequests(Long userId) {
        return moneyRequestDAO.findByRequesterId(userId);
    }
    
    public List<MoneyRequest> getPendingRequestsForMe(Long userId) {
        return moneyRequestDAO.findPendingRequestsForUser(userId);
    }
    
    public boolean acceptRequest(Long requestId, Long userId) {
        Optional<MoneyRequest> requestOpt = moneyRequestDAO.findById(requestId);
        if (requestOpt.isEmpty()) {
            return false;
        }
        
        MoneyRequest request = requestOpt.get();
        
        if (!request.getRequestedFromId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to accept this request");
        }
        
        if (request.getStatus() != MoneyRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }
        
        try {
            // Send money
            transactionService.sendMoney(userId, String.valueOf(request.getRequesterId()), 
                                       request.getAmount(), "Payment for request: " + request.getDescription());
            
            // Update request status
            request.setStatus(MoneyRequest.RequestStatus.ACCEPTED);
            request.setRespondedAt(LocalDateTime.now());
            moneyRequestDAO.update(request);
            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to accept request: " + e.getMessage());
        }
    }
    
    public boolean declineRequest(Long requestId, Long userId) {
        Optional<MoneyRequest> requestOpt = moneyRequestDAO.findById(requestId);
        if (requestOpt.isEmpty()) {
            return false;
        }
        
        MoneyRequest request = requestOpt.get();
        
        if (!request.getRequestedFromId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to decline this request");
        }
        
        if (request.getStatus() != MoneyRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }
        
        request.setStatus(MoneyRequest.RequestStatus.DECLINED);
        request.setRespondedAt(LocalDateTime.now());
        return moneyRequestDAO.update(request);
    }
    
    public boolean cancelRequest(Long requestId, Long userId) {
        Optional<MoneyRequest> requestOpt = moneyRequestDAO.findById(requestId);
        if (requestOpt.isEmpty()) {
            return false;
        }
        
        MoneyRequest request = requestOpt.get();
        
        if (!request.getRequesterId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to cancel this request");
        }
        
        if (request.getStatus() != MoneyRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }
        
        request.setStatus(MoneyRequest.RequestStatus.CANCELLED);
        request.setRespondedAt(LocalDateTime.now());
        return moneyRequestDAO.update(request);
    }
}
