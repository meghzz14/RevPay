package com.revature.revpay.dao.impl;

import com.revature.revpay.dao.InvoiceDAO;
import com.revature.revpay.model.Invoice;
import com.revature.revpay.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceDAOImpl implements InvoiceDAO {
    
    @Override
    public Invoice save(Invoice invoice) {
        String sql = "INSERT INTO invoices (invoice_id, business_user_id, customer_id, customer_email, customer_name, " +
                    "invoice_number, amount, description, status, due_date) " +
                    "VALUES (invoices_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String getIdSql = "SELECT invoices_seq.CURRVAL FROM DUAL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, invoice.getBusinessUserId());
            stmt.setLong(2, invoice.getCustomerId());
            stmt.setString(3, invoice.getCustomerEmail());
            stmt.setString(4, invoice.getCustomerName());
            stmt.setString(5, invoice.getInvoiceNumber());
            stmt.setBigDecimal(6, invoice.getAmount());
            stmt.setString(7, invoice.getDescription());
            stmt.setString(8, invoice.getStatus().name());
            stmt.setDate(9, Date.valueOf(invoice.getDueDate()));
            
            stmt.executeUpdate();
            
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    invoice.setInvoiceId(rs.getLong(1));
                }
            }
            
            return invoice;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving invoice", e);
        }
    }
    
    @Override
    public Optional<Invoice> findById(Long invoiceId) {
        String sql = "SELECT * FROM invoices WHERE invoice_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToInvoice(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoice", e);
        }
    }
    
    @Override
    public List<Invoice> findByBusinessUserId(Long businessUserId) {
        String sql = "SELECT * FROM invoices WHERE business_user_id = ? ORDER BY created_at DESC";
        List<Invoice> invoices = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, businessUserId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
            return invoices;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoices", e);
        }
    }
    
    @Override
    public List<Invoice> findByCustomerId(Long customerId) {
        String sql = "SELECT * FROM invoices WHERE customer_id = ? ORDER BY created_at DESC";
        List<Invoice> invoices = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
            return invoices;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoices by customer", e);
        }
    }
    
    @Override
    public boolean update(Invoice invoice) {
        String sql = "UPDATE invoices SET status = ?, paid_at = ? WHERE invoice_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, invoice.getStatus().name());
            stmt.setTimestamp(2, invoice.getPaidAt() != null ? Timestamp.valueOf(invoice.getPaidAt()) : null);
            stmt.setLong(3, invoice.getInvoiceId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating invoice", e);
        }
    }
    
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(rs.getLong("invoice_id"));
        invoice.setBusinessUserId(rs.getLong("business_user_id"));
        invoice.setCustomerId(rs.getLong("customer_id"));
        invoice.setCustomerEmail(rs.getString("customer_email"));
        invoice.setCustomerName(rs.getString("customer_name"));
        invoice.setInvoiceNumber(rs.getString("invoice_number"));
        invoice.setAmount(rs.getBigDecimal("amount"));
        invoice.setDescription(rs.getString("description"));
        invoice.setStatus(Invoice.InvoiceStatus.valueOf(rs.getString("status")));
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            invoice.setDueDate(dueDate.toLocalDate());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            invoice.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp paidAt = rs.getTimestamp("paid_at");
        if (paidAt != null) {
            invoice.setPaidAt(paidAt.toLocalDateTime());
        }
        
        return invoice;
    }
}
