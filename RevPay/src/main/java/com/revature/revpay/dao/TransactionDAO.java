package com.revature.revpay.dao;

import com.revature.revpay.model.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Long transactionId);
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByFromUserId(Long fromUserId);
    List<Transaction> findByToUserId(Long toUserId);
    boolean update(Transaction transaction);
}
