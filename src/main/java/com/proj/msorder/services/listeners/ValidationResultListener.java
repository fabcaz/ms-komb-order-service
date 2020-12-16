package com.proj.msorder.services.listeners;

import com.kombucha.model.order.ValidateOrderResult;
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
public class ValidationResultListener {

    private final KombuchaOrderManager kombOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult result){

        final UUID kombOrderId = result.getOrderId();

        log.debug("Validation Result for Order with Id: \"" + kombOrderId+"\"");

        kombOrderManager.processValidationResult(kombOrderId, result.getIsValid());
    }
}
