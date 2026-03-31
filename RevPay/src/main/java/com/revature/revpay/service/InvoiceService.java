package com.revature.revpay.service;

import com.revature.revpay.model.Invoice;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(Long businessUserId, String customerIdentifier, 
                         BigDecimal amount, String description, String dueDate);
    List<Invoice> getInvoicesByIssuer(Long businessUserId);
    List<Invoice> getInvoicesByCustomer(Long customerId);
    boolean payInvoice(Long invoiceId, Long payerId);
    boolean cancelInvoice(Long invoiceId, Long businessUserId);
}
