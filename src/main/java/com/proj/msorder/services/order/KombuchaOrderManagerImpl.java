package com.proj.msorder.services.order;

import com.kombucha.model.order.KombuchaOrderDto;
import com.proj.msorder.domain.KombuchaOrder;
import com.proj.msorder.domain.KombuchaOrderEventEnum;
import com.proj.msorder.domain.KombuchaOrderStatusEnum;
import com.proj.msorder.repositories.KombuchaOrderRepository;
import com.proj.msorder.sm.Interceptors.KombuchaOrderStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class KombuchaOrderManagerImpl implements KombuchaOrderManager{

    private final KombuchaOrderRepository kombOrderRepository;
    private final StateMachineFactory<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> stateMachineFactory;
    private final KombuchaOrderStateChangeInterceptor stateChangeInterceptor;

    public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";

    @Transactional
    @Override
    public KombuchaOrder newKombuchaOrder(KombuchaOrder kombuchaOrder) {

        kombuchaOrder.setId(null);
        kombuchaOrder.setOrderStatus(KombuchaOrderStatusEnum.NEW);

        KombuchaOrder savedOrder = kombOrderRepository.save(kombuchaOrder);
        log.debug("######### CREATED NEW ORDER. Id: \""+savedOrder.getId()+"\"");
        sendOrderEvent(savedOrder, KombuchaOrderEventEnum.VALIDATE_ORDER);
        return savedOrder;
    }

    @Transactional
    @Override
    public void processValidationResult(UUID kombuchaOrderId, Boolean isValid) {

        log.debug("Process Validation Result for kombuchaOrderId: \"" + kombuchaOrderId + "\" Valid: " + isValid);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Optional<KombuchaOrder> kombOrderOptional = kombOrderRepository.findById(kombuchaOrderId);

        kombOrderOptional.ifPresentOrElse(kombOrder ->{
            if (isValid){
                sendOrderEvent(kombOrder, KombuchaOrderEventEnum.VALIDATION_PASSED);

                awaitStatusChangeTo(KombuchaOrderStatusEnum.VALIDATED, kombuchaOrderId);

                KombuchaOrder validatedOrder = kombOrderRepository.findById(kombuchaOrderId).get();

                sendOrderEvent(validatedOrder, KombuchaOrderEventEnum.ALLOCATE_ORDER);

            }else {
                sendOrderEvent(kombOrder, KombuchaOrderEventEnum.VALIDATION_FAILED);
            }
        },()->log.error("Order Not Found for Id: \"" + kombuchaOrderId+"\""));

    }



    @Override
    public void kombuchaOrderAllocationPassed(KombuchaOrderDto kombuchaOrder) {
        Optional<KombuchaOrder> kombOrderOptional = kombOrderRepository.findById(kombuchaOrder.getId());

        kombOrderOptional.ifPresentOrElse(order->{
            sendOrderEvent(order, KombuchaOrderEventEnum.ALLOCATION_SUCCESS);
            awaitStatusChangeTo(KombuchaOrderStatusEnum.ALLOCATED, order.getId());
            updateOrderLinesAllocatedQty(kombuchaOrder);
        }, ()->log.error("Order Not Found for Id: \"" + kombuchaOrder.getId()+"\""));

    }



    @Override
    public void kombuchaOrderAllocationPendingInventory(KombuchaOrderDto kombuchaOrder) {
        Optional<KombuchaOrder> kombOrderOptional = kombOrderRepository.findById(kombuchaOrder.getId());

        kombOrderOptional.ifPresentOrElse(order->{
            sendOrderEvent(order, KombuchaOrderEventEnum.ALLOCATION_NO_INVENTORY);
            awaitStatusChangeTo(KombuchaOrderStatusEnum.PENDING_INVENTORY, order.getId());
            updateOrderLinesAllocatedQty(kombuchaOrder);
        }, ()->log.error("Order Not Found for Id: \"" + kombuchaOrder.getId()+"\""));

    }

    @Override
    public void kombuchaOrderAllocationFailed(KombuchaOrderDto kombuchaOrder) {
        Optional<KombuchaOrder> kombOrderOptional = kombOrderRepository.findById(kombuchaOrder.getId());

        kombOrderOptional.ifPresentOrElse(order-> {
            sendOrderEvent(order, KombuchaOrderEventEnum.ALLOCATION_FAILED);
        }, ()->log.error("Order Not Found for Id: \"" + kombuchaOrder.getId()+"\""));
    }

    @Override
    public void processPartiallyAllocatedOrders(List<KombuchaOrder> incompleteOrders) {
        incompleteOrders.forEach(order ->sendOrderEvent(order, KombuchaOrderEventEnum.ALLOCATE_ORDER));

    }

    @Override
    public void pickUpOrder(UUID id) {
        Optional<KombuchaOrder> kombOrderOptional = kombOrderRepository.findById(id);

        kombOrderOptional.ifPresentOrElse(order-> {
            sendOrderEvent(order, KombuchaOrderEventEnum.ORDER_PICKED_UP);
        }, ()->log.error("Order Not Found for Id: \"" + id+"\""));


    }

    @Override
    public void cancelOrder(UUID id) {
        Optional<KombuchaOrder> kombOrderOptional = kombOrderRepository.findById(id);

        kombOrderOptional.ifPresentOrElse(order-> {
            sendOrderEvent(order, KombuchaOrderEventEnum.CANCEL_ORDER);
        }, ()->log.error("Order Not Found for Id: \"" + id+"\""));

    }


    private void sendOrderEvent(KombuchaOrder order, KombuchaOrderEventEnum event) {

        StateMachine<KombuchaOrderStatusEnum,KombuchaOrderEventEnum> sm = createSM(order);

        Message msg = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, order.getId().toString())
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> createSM(KombuchaOrder order) {
        StateMachine<KombuchaOrderStatusEnum,KombuchaOrderEventEnum> sm = stateMachineFactory.getStateMachine(order.getId());

        sm.stop();

        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(stateChangeInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(order.getOrderStatus(),null,null,null));
        });
        sm.start();

        return sm;
    }

    private void awaitStatusChangeTo(KombuchaOrderStatusEnum expectedStatus, UUID kombuchaOrderId) {

        log.debug("Waiting for Order with Id: \""+kombuchaOrderId+"\" to change to Status: "+expectedStatus.name());

        AtomicInteger loopCount = new AtomicInteger(0);
        AtomicBoolean found = new AtomicBoolean(false);


        while(!found.get()) {
            if (loopCount.incrementAndGet() > 10) {
                found.set(true);
                log.debug("Loop tries exceeded");
            }

            kombOrderRepository.findById(kombuchaOrderId).ifPresentOrElse(order -> {
                if (order.getOrderStatus().equals(expectedStatus)) {
                    found.set(true);
                    log.debug("found Order with correct Status");
                } else {
                    log.debug("Order Status mismatch. Expected: \"" + expectedStatus.name() + "\" but  found: \"" + order.getOrderStatus().name() + "\"");
                }

            }, () -> {
                log.debug("Order Id \"" + kombuchaOrderId.toString() + "\" Not Found");
            });
        }
        if (!found.get()) {
            try {
                log.debug("Sleeping for retry");
                Thread.sleep(100);
            } catch (Exception e) {
                // do nothing
            }
        }

    }


    private void updateOrderLinesAllocatedQty(KombuchaOrderDto kombuchaOrderDto) {
        Optional<KombuchaOrder> kombOrderOptional = kombOrderRepository.findById(kombuchaOrderDto.getId());

        kombOrderOptional.ifPresentOrElse(order->{
            order.getKombuchaOrderLines().forEach(orderLine -> {
                kombuchaOrderDto.getKombuchaOrderLines().forEach(dtoOrderLine ->{
                    if (orderLine.getId().equals(dtoOrderLine.getId()))
                        orderLine.setQuantityAllocated(dtoOrderLine.getQuantityAllocated());
                });
            });
        },() -> log.error("Order Not Found for Id: \"" + kombuchaOrderDto.getId()+"\""));

    }

}
