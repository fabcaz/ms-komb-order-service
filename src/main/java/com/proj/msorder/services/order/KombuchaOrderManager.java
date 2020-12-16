package com.proj.msorder.services.order;

import com.kombucha.model.order.KombuchaOrderDto;
import com.proj.msorder.domain.KombuchaOrder;

import java.util.List;
import java.util.UUID;


public interface KombuchaOrderManager {
    
    KombuchaOrder newKombuchaOrder(KombuchaOrder kombuchaOrderOrder);

    void processValidationResult(UUID kombuchaOrderId, Boolean isValid);

    void kombuchaOrderAllocationPassed(KombuchaOrderDto kombuchaOrder);

    void kombuchaOrderAllocationPendingInventory(KombuchaOrderDto kombuchaOrder);

    void kombuchaOrderAllocationFailed(KombuchaOrderDto kombuchaOrder);

    void processPartiallyAllocatedOrders(List<KombuchaOrder> incompleteOrders);

    void pickUpOrder(UUID id);

    void cancelOrder(UUID id);
}
