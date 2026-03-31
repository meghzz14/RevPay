package com.revature.revpay.service.impl;

import com.revature.revpay.dao.WalletDAO;
import com.revature.revpay.dao.impl.WalletDAOImpl;
import com.revature.revpay.model.Wallet;
import com.revature.revpay.service.WalletService;

import java.math.BigDecimal;
import java.util.Optional;

public class WalletServiceImpl implements WalletService {
    private final WalletDAO walletDAO;
    
    public WalletServiceImpl() {
        this.walletDAO = new WalletDAOImpl();
    }
    
    @Override
    public Optional<Wallet> getWallet(Long userId) {
        return walletDAO.findByUserId(userId);
    }
    
    @Override
    public boolean addMoney(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        Optional<Wallet> walletOpt = walletDAO.findByUserId(userId);
        if (walletOpt.isEmpty()) {
            return false;
        }
        
        Wallet wallet = walletOpt.get();
        BigDecimal newBalance = wallet.getBalance().add(amount);
        
        return walletDAO.updateBalance(userId, newBalance);
    }
    
    @Override
    public boolean withdrawMoney(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        Optional<Wallet> walletOpt = walletDAO.findByUserId(userId);
        if (walletOpt.isEmpty()) {
            return false;
        }
        
        Wallet wallet = walletOpt.get();
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        
        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        return walletDAO.updateBalance(userId, newBalance);
    }
    
    @Override
    public BigDecimal getBalance(Long userId) {
        Optional<Wallet> walletOpt = walletDAO.findByUserId(userId);
        return walletOpt.map(Wallet::getBalance).orElse(BigDecimal.ZERO);
    }
}
