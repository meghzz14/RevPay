package com.revpay.repository;

import com.revpay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.model.SecurityQuestion;

import java.util.Optional;

@Repository
public interface SecurityQuestionRepository
        extends JpaRepository<SecurityQuestion, Long> {
    Optional<SecurityQuestion> findByUser(User user);
}