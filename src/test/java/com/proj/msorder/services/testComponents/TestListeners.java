package com.proj.msorder.services.testComponents;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombucha.model.brewery.KombuchaDto;
import com.kombucha.model.inventory.InventoryInfoRequest;
import com.kombucha.model.inventory.InventoryInfoResponse;
import com.kombucha.model.inventory.KombuchaInventoryDto;
import com.kombucha.model.order.*;
import com.proj.msorder.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Slf4j
@RequiredArgsConstructor
@Service
public class TestListeners {




    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void validateOrder(ValidateOrderRequest validateOrderRequest) {
        KombuchaOrderDto koDto = validateOrderRequest.getKombuchaOrderDto();

        Boolean isValid = null;
        //test cases
        switch (koDto.getCustomerRef()) {
            case "e3a9ded3-85d4-4e54-b237-2e6a659ba0e6":
                isValid = true;
                break;
            case "f3a9ded3-85d4-4e54-b237-2e6a659ba0e6":
                isValid = false;
                break;

        }

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                ValidateOrderResult.builder()
                        .orderId(UUID.randomUUID())
                        .isValid(isValid)
                        .orderId(validateOrderRequest.getKombuchaOrderDto().getId())
                        .build());
    }

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void allocateOrder(AllocateOrderRequest allocationRequest) {
        KombuchaOrderDto koDto = allocationRequest.getKombuchaOrderDto();

        log.debug("===========AllocateOrderRequest for: "+ koDto.toString());
        AllocationOrderResult.AllocationOrderResultBuilder builder = AllocationOrderResult.builder();

        builder.kombuchaOrderDto(allocationRequest.getKombuchaOrderDto());

        //test cases
        switch (koDto.getCustomerRef()) {
            case "e3a9ded3-85d4-4e54-b237-2e6a659ba0e6":
                builder.pendingInventory(false);
                builder.allocationError(false);
                break;
            case "f3a9ded3-85d4-4e54-b237-2e6a659ba0e6":
                builder.pendingInventory(true);
                builder.allocationError(false);
                break;
            case "03a9ded3-85d4-4e54-b237-2e6a659ba0e6":
                builder.pendingInventory(false);
                builder.allocationError(true);
                break;
        }
        AllocationOrderResult result = builder.build();

        log.debug("===========AllocateOrderRequest result: "+result.toString());

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, result);
    }


}
