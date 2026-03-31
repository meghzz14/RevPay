package com.revature.revpay.service;

import com.revature.revpay.model.Transaction;
import com.revature.revpay.model.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction sendMoney(Long fromUserId, String toUserIdentifier, BigDecimal amount, String note);
    List<Transaction> getTransactionHistory(Long userId);
    Optional<Wallet> getWallet(Long userId);
    boolean addMoneyToWallet(Long userId, BigDecimal amount);
}
