package com.revpay.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.model.Transaction;
import com.revpay.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * SEND MONEY
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendMoney(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam BigDecimal amount,
            @RequestParam String transactionPin,
            @RequestParam(required = false) String remarks) {

        transactionService.sendMoney(
                senderId,
                receiverId,
                amount,
                transactionPin,
                remarks
        );

        return ResponseEntity.ok("Money sent successfully");
    }

    /**
     * TRANSACTION HISTORY
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(
            @PathVariable Long userId) {

        List<Transaction> transactions =
                transactionService.getTransactionsForUser(userId);

        return ResponseEntity.ok(transactions);
    }
}