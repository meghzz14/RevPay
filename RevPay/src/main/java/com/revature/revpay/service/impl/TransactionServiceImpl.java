package com.revature.revpay.service.impl;

import com.revature.revpay.dao.TransactionDAO;
import com.revature.revpay.dao.UserDAO;
import com.revature.revpay.dao.WalletDAO;
import com.revature.revpay.dao.impl.TransactionDAOImpl;
import com.revature.revpay.dao.impl.UserDAOImpl;
import com.revature.revpay.dao.impl.WalletDAOImpl;
import com.revature.revpay.model.Transaction;
import com.revature.revpay.model.User;
import com.revature.revpay.model.Wallet;
import com.revature.revpay.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TransactionServiceImpl implements TransactionService {
    private final TransactionDAO transactionDAO;
    private final WalletDAO walletDAO;
    private final UserDAO userDAO;
    
    public TransactionServiceImpl() {
        this.transactionDAO = new TransactionDAOImpl();
        this.walletDAO = new WalletDAOImpl();
        this.userDAO = new UserDAOImpl();
    }
    
    @Override
    public Transaction sendMoney(Long fromUserId, String toUserIdentifier, BigDecimal amount, String note) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        Optional<User> toUserOpt = userDAO.findByEmail(toUserIdentifier);
        if (toUserOpt.isEmpty()) {
            toUserOpt = userDAO.findByPhoneNumber(toUserIdentifier);
        }
        if (toUserOpt.isEmpty()) {
            try {
                Long userId = Long.parseLong(toUserIdentifier);
                toUserOpt = userDAO.findById(userId);
            } catch (NumberFormatException e) {
                // Not a valid user ID
            }
        }
        
        if (toUserOpt.isEmpty()) {
            throw new IllegalArgumentException("Recipient not found");
        }
        
        User toUser = toUserOpt.get();
        
        if (fromUserId.equals(toUser.getUserId())) {
            throw new IllegalArgumentException("Cannot send money to yourself");
        }
        
        Optional<Wallet> fromWalletOpt = walletDAO.findByUserId(fromUserId);
        Optional<Wallet> toWalletOpt = walletDAO.findByUserId(toUser.getUserId());
        
        if (fromWalletOpt.isEmpty() || toWalletOpt.isEmpty()) {
            throw new IllegalStateException("Wallet not found");
        }
        
        Wallet fromWallet = fromWalletOpt.get();
        Wallet toWallet = toWalletOpt.get();
        
        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        
        Transaction transaction = new Transaction(fromUserId, toUser.getUserId(), amount, 
                                                 Transaction.TransactionType.SEND_MONEY, 
                                                 "Money sent to " + toUser.getFullName());
        transaction.setNote(note);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());
        
        BigDecimal newFromBalance = fromWallet.getBalance().subtract(amount);
        BigDecimal newToBalance = toWallet.getBalance().add(amount);
        
        walletDAO.updateBalance(fromUserId, newFromBalance);
        walletDAO.updateBalance(toUser.getUserId(), newToBalance);
        
        return transactionDAO.save(transaction);
    }
    
    @Override
    public List<Transaction> getTransactionHistory(Long userId) {
        return transactionDAO.findByUserId(userId);
    }
    
    @Override
    public Optional<Wallet> getWallet(Long userId) {
        return walletDAO.findByUserId(userId);
    }
    
    @Override
    public boolean addMoneyToWallet(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        Optional<Wallet> walletOpt = walletDAO.findByUserId(userId);
        if (walletOpt.isEmpty()) {
            return false;
        }
        
        Wallet wallet = walletOpt.get();
        BigDecimal newBalance = wallet.getBalance().add(amount);
        
        Transaction transaction = new Transaction(null, userId, amount, 
                                                 Transaction.TransactionType.ADD_MONEY, 
                                                 "Money added to wallet");
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());
        transactionDAO.save(transaction);
        
        return walletDAO.updateBalance(userId, newBalance);
    }
}
