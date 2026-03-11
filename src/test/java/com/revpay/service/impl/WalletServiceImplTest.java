package com.revpay.service.impl;

import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.model.Wallet;
import com.revpay.model.enums.TransactionStatus;
import com.revpay.model.enums.TransactionType;
import com.revpay.repository.TransactionRepository;
import com.revpay.repository.UserRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);

        wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("10000"));
    }

    // ------------------------
    // GET BALANCE
    // ------------------------

    @Test
    void testGetBalanceSuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        BigDecimal balance = walletService.getBalance(1L);

        assertEquals(new BigDecimal("10000"), balance);
    }

    @Test
    void testGetBalanceUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> walletService.getBalance(1L));
    }

    // ------------------------
    // ADD MONEY
    // ------------------------

    @Test
    void testAddMoneySuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(authService.verifyTransactionPin(user, "1234")).thenReturn(true);

        walletService.addMoney(1L, new BigDecimal("5000"), "1234");

        verify(walletRepository).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));

        assertEquals(new BigDecimal("15000"), wallet.getBalance());
    }

    @Test
    void testAddMoneyInvalidAmount() {

        assertThrows(IllegalArgumentException.class,
                () -> walletService.addMoney(1L, BigDecimal.ZERO, "1234"));
    }

    @Test
    void testAddMoneyInvalidPin() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.verifyTransactionPin(user, "1111")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> walletService.addMoney(1L, new BigDecimal("5000"), "1111"));
    }

    // ------------------------
    // WITHDRAW MONEY
    // ------------------------

    @Test
    void testWithdrawMoneySuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(authService.verifyTransactionPin(user, "1234")).thenReturn(true);

        walletService.withdrawMoney(1L, new BigDecimal("3000"), "1234");

        verify(walletRepository).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));

        assertEquals(new BigDecimal("7000"), wallet.getBalance());
    }

    @Test
    void testWithdrawMoneyInsufficientBalance() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(authService.verifyTransactionPin(user, "1234")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> walletService.withdrawMoney(1L, new BigDecimal("20000"), "1234"));
    }

    @Test
    void testWithdrawMoneyInvalidPin() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.verifyTransactionPin(user, "9999")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> walletService.withdrawMoney(1L, new BigDecimal("2000"), "9999"));
    }
}