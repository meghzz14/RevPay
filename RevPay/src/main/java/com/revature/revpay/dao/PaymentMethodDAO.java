package com.revature.revpay.dao;

import com.revature.revpay.model.PaymentMethod;
import java.util.List;
import java.util.Optional;

public interface PaymentMethodDAO {
    PaymentMethod save(PaymentMethod paymentMethod);
    Optional<PaymentMethod> findById(Long paymentMethodId);
    List<PaymentMethod> findByUserId(Long userId);
    Optional<PaymentMethod> findDefaultByUserId(Long userId);
    boolean update(PaymentMethod paymentMethod);
    boolean delete(Long paymentMethodId);
    boolean setDefaultPaymentMethod(Long userId, Long paymentMethodId);
}
