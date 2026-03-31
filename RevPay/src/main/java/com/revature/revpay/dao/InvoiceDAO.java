package com.revature.revpay.dao;

import com.revature.revpay.model.Invoice;
import java.util.List;
import java.util.Optional;

public interface InvoiceDAO {
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(Long invoiceId);
    List<Invoice> findByBusinessUserId(Long businessUserId);
    List<Invoice> findByCustomerId(Long customerId);
    boolean update(Invoice invoice);
}
