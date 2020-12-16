package com.proj.msorder.services.order;

import com.kombucha.model.order.KombuchaOrderDto;
import com.kombucha.model.order.KombuchaOrderPagedList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface KombuchaOrderService {
    KombuchaOrderPagedList listOrders(UUID customerId, Pageable pageable);

    KombuchaOrderDto createNewOrder(UUID customerId, KombuchaOrderDto kombOrderDto);

    KombuchaOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);

    void cancelOrder(UUID customerId, UUID orderId);
}
