package com.revature.revpay.dao;

import com.revature.revpay.model.MoneyRequest;
import java.util.List;
import java.util.Optional;

public interface MoneyRequestDAO {
    MoneyRequest save(MoneyRequest request);
    Optional<MoneyRequest> findById(Long requestId);
    List<MoneyRequest> findByRequesterId(Long requesterId);
    List<MoneyRequest> findByRequestedFromId(Long requestedFromId);
    List<MoneyRequest> findPendingRequestsForUser(Long userId);
    boolean update(MoneyRequest request);
}
