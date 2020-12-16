package com.proj.msorder.services.order;


import com.proj.msorder.domain.KombuchaOrder;
import com.proj.msorder.domain.KombuchaOrderStatusEnum;
import com.proj.msorder.repositories.KombuchaOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A service responsible for ensuring that every order is fully allocated.
 */
@Profile("!test")
@Slf4j
@RequiredArgsConstructor
@Service
public class FulfillPartiallyAllocatedOrderService {

    private final KombuchaOrderRepository kombuchaOrderRepository;
    private final KombuchaOrderManager orderManager;

    /**
     * Periodically retrieves all partially allocated orders and reattempts to allocate inventory for each order.
     */
    @Scheduled(fixedRate = 5000)
    public void fulfillPartiallyAllocatedOrder() {
        log.debug("Checking for partially allocated orders");
        List<KombuchaOrder> partiallyAllocatedOrders = kombuchaOrderRepository.findAll().stream()
                .filter(order -> order.getOrderStatus().equals(KombuchaOrderStatusEnum.PENDING_INVENTORY))
                .collect(Collectors.toList());
        log.debug("found "+partiallyAllocatedOrders.size()+" unfulfilled orders");
        orderManager.processPartiallyAllocatedOrders(partiallyAllocatedOrders);

    }

}
