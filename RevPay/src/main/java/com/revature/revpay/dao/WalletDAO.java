package com.revature.revpay.dao;

import com.revature.revpay.model.Wallet;
import java.math.BigDecimal;
import java.util.Optional;

public interface WalletDAO {
    Optional<Wallet> findByUserId(Long userId);
    boolean updateBalance(Long userId, BigDecimal newBalance);
}
