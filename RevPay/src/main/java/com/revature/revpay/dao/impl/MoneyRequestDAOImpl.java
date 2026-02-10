package com.revature.revpay.dao.impl;

import com.revature.revpay.dao.MoneyRequestDAO;
import com.revature.revpay.model.MoneyRequest;
import com.revature.revpay.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoneyRequestDAOImpl implements MoneyRequestDAO {
    
    @Override
    public MoneyRequest save(MoneyRequest request) {
        String sql = "INSERT INTO money_requests (request_id, requester_id, requested_from_id, amount, description, status) " +
                    "VALUES (money_requests_seq.NEXTVAL, ?, ?, ?, ?, ?)";
        String getIdSql = "SELECT money_requests_seq.CURRVAL FROM DUAL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, request.getRequesterId());
            stmt.setLong(2, request.getRequestedFromId());
            stmt.setBigDecimal(3, request.getAmount());
            stmt.setString(4, request.getDescription());
            stmt.setString(5, request.getStatus().name());
            
            stmt.executeUpdate();
            
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    request.setRequestId(rs.getLong(1));
                }
            }
            
            return request;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving money request", e);
        }
    }
    
    @Override
    public Optional<MoneyRequest> findById(Long requestId) {
        String sql = "SELECT * FROM money_requests WHERE request_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, requestId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToMoneyRequest(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding money request", e);
        }
    }
    
    @Override
    public List<MoneyRequest> findByRequesterId(Long requesterId) {
        String sql = "SELECT * FROM money_requests WHERE requester_id = ? ORDER BY created_at DESC";
        List<MoneyRequest> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, requesterId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                requests.add(mapResultSetToMoneyRequest(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding money requests", e);
        }
    }
    
    @Override
    public List<MoneyRequest> findByRequestedFromId(Long requestedFromId) {
        String sql = "SELECT * FROM money_requests WHERE requested_from_id = ? ORDER BY created_at DESC";
        List<MoneyRequest> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, requestedFromId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                requests.add(mapResultSetToMoneyRequest(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding money requests", e);
        }
    }
    
    @Override
    public List<MoneyRequest> findPendingRequestsForUser(Long userId) {
        String sql = "SELECT * FROM money_requests WHERE requested_from_id = ? AND status = 'PENDING' ORDER BY created_at DESC";
        List<MoneyRequest> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                requests.add(mapResultSetToMoneyRequest(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding pending requests", e);
        }
    }
    
    @Override
    public boolean update(MoneyRequest request) {
        String sql = "UPDATE money_requests SET status = ?, responded_at = ? WHERE request_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, request.getStatus().name());
            stmt.setTimestamp(2, request.getRespondedAt() != null ? 
                             Timestamp.valueOf(request.getRespondedAt()) : null);
            stmt.setLong(3, request.getRequestId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating money request", e);
        }
    }
    
    private MoneyRequest mapResultSetToMoneyRequest(ResultSet rs) throws SQLException {
        MoneyRequest request = new MoneyRequest();
        request.setRequestId(rs.getLong("request_id"));
        request.setRequesterId(rs.getLong("requester_id"));
        request.setRequestedFromId(rs.getLong("requested_from_id"));
        request.setAmount(rs.getBigDecimal("amount"));
        request.setDescription(rs.getString("description"));
        request.setStatus(MoneyRequest.RequestStatus.valueOf(rs.getString("status")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            request.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp respondedAt = rs.getTimestamp("responded_at");
        if (respondedAt != null) {
            request.setRespondedAt(respondedAt.toLocalDateTime());
        }
        
        return request;
    }
}
