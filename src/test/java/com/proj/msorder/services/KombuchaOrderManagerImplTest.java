package com.proj.msorder.services;

import com.kombucha.model.order.AllocateOrderRequest;
import com.kombucha.model.order.KombuchaOrderDto;
import com.proj.msorder.config.JmsConfig;
import com.proj.msorder.domain.KombuchaOrder;
import com.proj.msorder.domain.KombuchaOrderStatusEnum;
import com.proj.msorder.repositories.KombuchaOrderRepository;
import com.proj.msorder.services.order.KombuchaOrderManager;
import com.proj.msorder.web.mappers.KombuchaOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
class KombuchaOrderManagerImplTest {

    final UUID CASE1_UUID = UUID.fromString("e3a9ded3-85d4-4e54-b237-2e6a659ba0e6");
    final UUID CASE2_UUID = UUID.fromString("f3a9ded3-85d4-4e54-b237-2e6a659ba0e6");
    final UUID CASE3_UUID = UUID.fromString("03a9ded3-85d4-4e54-b237-2e6a659ba0e6");
//    final UUID OTHER_UUID = UUID.fromString("8a9ded4-85d4-4e54-b237-2e6a659ba0e6");


    @Autowired
    KombuchaOrderManager manager;

    @Autowired
    KombuchaOrderRepository kombOrderRepository;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    KombuchaOrderMapper mapper;

    @BeforeEach
    void setUp() {
    }


    @Test
    @DisplayName("Invalid order should get VALIDATION_EXCEPTION status")
    void testNewToValidationException() {
        /*
         KombuchaOrderManagerImpl#newKombuchaOrder sets orderId to null -> will put CASE#_UUID in order.customerRef
         */
        KombuchaOrder testOrder = new KombuchaOrder();
        testOrder.setCustomerRef(CASE2_UUID.toString());

        KombuchaOrder newOrder = manager.newKombuchaOrder(testOrder);


        await().untilAsserted(() -> {
            KombuchaOrder foundOrder = kombOrderRepository.findById(newOrder.getId()).get();

            assertEquals(KombuchaOrderStatusEnum.VALIDATION_EXCEPTION, foundOrder.getOrderStatus());

        });


    }

    @Test
    @DisplayName("Testing order from NEW to ALLOCATED")
    void testNewToAllocated() {
        /*
         KombuchaOrderManagerImpl#newKombuchaOrder sets orderId to null -> will put CASE#_UUID in order.customerRef
         */
        KombuchaOrder testOrder = new KombuchaOrder();

        testOrder.setCustomerRef(CASE1_UUID.toString());

        KombuchaOrder newOrder = manager.newKombuchaOrder(testOrder);


        await().untilAsserted(() -> {
            KombuchaOrder foundOrder = kombOrderRepository.findById(newOrder.getId()).get();

            assertEquals(KombuchaOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());

        });
    }

    @Test
    @DisplayName("Allocated Order should get ALLOCATED status")
    void testKombuchaOrderAllocationPassed() {


        KombuchaOrder savedOrder = kombOrderRepository.save(KombuchaOrder.builder()
                .customerRef(CASE1_UUID.toString())
                .orderStatus(KombuchaOrderStatusEnum.ALLOCATION_PENDING)
                .build());

        KombuchaOrderDto validatedOrderDto = mapper.kombuchaOrderToKombuchaOrderDto(savedOrder);

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE,
                AllocateOrderRequest.builder()
                        .kombuchaOrderDto(validatedOrderDto)
                        .build()
        );

        await().untilAsserted(() -> {
            KombuchaOrder foundOrder = kombOrderRepository.findById(savedOrder.getId()).get();

            assertEquals(KombuchaOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());

        });

    }

    @Test
    @DisplayName("Order with allocation_error should get ALLOCATION_EXCEPTION status")
    void testKombuchaOrderAllocationFailed() {

        KombuchaOrder savedOrder = kombOrderRepository.save(KombuchaOrder.builder()
                .customerRef(CASE3_UUID.toString())
                .orderStatus(KombuchaOrderStatusEnum.ALLOCATION_PENDING)
                .build());

        KombuchaOrderDto validatedOrderDto = mapper.kombuchaOrderToKombuchaOrderDto(savedOrder);

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE,
                AllocateOrderRequest.builder()
                        .kombuchaOrderDto(validatedOrderDto)
                        .build()
        );

        await().untilAsserted(() -> {
            KombuchaOrder foundOrder = kombOrderRepository.findById(savedOrder.getId()).get();

            assertEquals(KombuchaOrderStatusEnum.ALLOCATION_EXCEPTION, foundOrder.getOrderStatus());

        });

    }

    @Test
    @DisplayName("Partially Allocated Order should get PENDING_INVENTORY status")
    void testKombuchaOrderAllocationPendingInventory() {

        KombuchaOrder savedOrder = kombOrderRepository.save(KombuchaOrder.builder()
                .customerRef(CASE2_UUID.toString())
                .orderStatus(KombuchaOrderStatusEnum.ALLOCATION_PENDING)
                .build());

        KombuchaOrderDto validatedOrderDto = mapper.kombuchaOrderToKombuchaOrderDto(savedOrder);

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE,
                AllocateOrderRequest.builder()
                        .kombuchaOrderDto(validatedOrderDto)
                        .build()
        );

        await().untilAsserted(() -> {
            KombuchaOrder foundOrder = kombOrderRepository.findById(savedOrder.getId()).get();

            assertEquals(KombuchaOrderStatusEnum.PENDING_INVENTORY, foundOrder.getOrderStatus());

        });

    }

    @Test
    @DisplayName("Should attempt to allocate partially allocated Order")
    void testProcessPartiallyAllocatedOrders() {
        List<KombuchaOrder> incompleteOrders = new ArrayList<>(3);

        incompleteOrders.add(kombOrderRepository.save(KombuchaOrder.builder()
                .customerRef(CASE1_UUID.toString())
                .orderStatus(KombuchaOrderStatusEnum.PENDING_INVENTORY)
                .build())
        );
//        incompleteOrders.add(kombOrderRepository.save(KombuchaOrder.builder()
//                .customerRef(CASE2_UUID.toString())
//                .orderStatus(KombuchaOrderStatusEnum.PENDING_INVENTORY)
//                .build())
//        );
//        incompleteOrders.add(kombOrderRepository.save(KombuchaOrder.builder()
//                .customerRef(CASE3_UUID.toString())
//                .orderStatus(KombuchaOrderStatusEnum.PENDING_INVENTORY)
//                .build())
//        );

        manager.processPartiallyAllocatedOrders(incompleteOrders);


        await().untilAsserted(() -> {//should now have been allocated
            UUID orderId = incompleteOrders.get(0).getId();
            KombuchaOrder foundOrder = kombOrderRepository.findById(orderId).get();

            assertEquals(KombuchaOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());

        });
//        await().untilAsserted(() -> {//should still be pending
//            UUID orderId = incompleteOrders.get(1).getId();
//            KombuchaOrder foundOrder = kombOrderRepository.findById(orderId).get();
//
//            assertEquals(KombuchaOrderStatusEnum.PENDING_INVENTORY, foundOrder.getOrderStatus());
//
//        });
//        await().untilAsserted(() -> {//should now get allocation error
//            UUID orderId = incompleteOrders.get(2).getId();
//            KombuchaOrder foundOrder = kombOrderRepository.findById(orderId).get();
//
//            assertEquals(KombuchaOrderStatusEnum.ALLOCATION_EXCEPTION, foundOrder.getOrderStatus());
//
//        });
    }

    @Test
    @DisplayName("testing transition from ALLOCATED to PICKED_UP")
    void testKombuchaOrderPickedUp() {
        KombuchaOrder savedOrder = kombOrderRepository.save(KombuchaOrder.builder()
                .customerRef(CASE1_UUID.toString())
                .orderStatus(KombuchaOrderStatusEnum.ALLOCATED)
                .build());

        manager.pickUpOrder(savedOrder.getId());

        await().untilAsserted(() -> {
            KombuchaOrder foundOrder = kombOrderRepository.findById(savedOrder.getId()).get();

            assertEquals(KombuchaOrderStatusEnum.PICKED_UP, foundOrder.getOrderStatus());

        });
    }

    @Test
    void testCancelOrder() {
    }


}