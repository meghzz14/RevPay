package com.revature.revpay.dao;

import com.revature.revpay.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User save(User user);
    Optional<User> findById(Long userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findByFullNameContaining(String name);
    boolean update(User user);
    boolean delete(Long userId);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    void incrementFailedLoginAttempts(Long userId);
    void resetFailedLoginAttempts(Long userId);
    void lockAccount(Long userId);
    void unlockAccount(Long userId);
}