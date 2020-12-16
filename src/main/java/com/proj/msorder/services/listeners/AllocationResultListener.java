package com.proj.msorder.services.listeners;


import com.kombucha.model.order.AllocationOrderResult;
import com.proj.msorder.config.JmsConfig;
import com.proj.msorder.services.order.KombuchaOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;



@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationResultListener {

    private final KombuchaOrderManager kombOrderManager;


    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocationOrderResult result){
        final UUID kombOrderId = result.getKombuchaOrderDto().getId();

        log.debug("Allocation Result for Order with Id: \"" + kombOrderId
                +"\", PendingInventory: " +result.getPendingInventory() + ", AllocationError: " + result.getAllocationError());

        if (!result.getAllocationError() && !result.getPendingInventory()){

            kombOrderManager.kombuchaOrderAllocationPassed(result.getKombuchaOrderDto());

        }else if (!result.getAllocationError() && result.getPendingInventory()){

            kombOrderManager.kombuchaOrderAllocationPendingInventory(result.getKombuchaOrderDto());

        }else if (result.getAllocationError()){

            kombOrderManager.kombuchaOrderAllocationFailed(result.getKombuchaOrderDto());
        }
    }

}
