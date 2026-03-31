package com.revature.revpay.service;

import com.revature.revpay.model.Wallet;
import java.math.BigDecimal;
import java.util.Optional;

public interface WalletService {
    Optional<Wallet> getWallet(Long userId);
    boolean addMoney(Long userId, BigDecimal amount);
    boolean withdrawMoney(Long userId, BigDecimal amount);
    BigDecimal getBalance(Long userId);
}
