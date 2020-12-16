package com.proj.msorder.sm.actions;

import com.kombucha.model.order.DeallocateOrderRequest;
import com.proj.msorder.config.JmsConfig;
import com.proj.msorder.domain.KombuchaOrder;
import com.proj.msorder.domain.KombuchaOrderEventEnum;
import com.proj.msorder.domain.KombuchaOrderStatusEnum;
import com.proj.msorder.repositories.KombuchaOrderRepository;
import com.proj.msorder.services.order.KombuchaOrderManagerImpl;
import com.proj.msorder.web.mappers.KombuchaOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeallocateOrderAction implements Action<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> {

    private final KombuchaOrderRepository kombRepository;
    private final JmsTemplate jmsTemplate;
    private final KombuchaOrderMapper kombMapper;

    @Override
    public void execute(StateContext<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> context) {
        UUID kombOrderId = UUID.fromString((String) context.getMessage().getHeaders().get(KombuchaOrderManagerImpl.ORDER_ID_HEADER));

        Optional<KombuchaOrder> kombOrderOp =  kombRepository.findById(kombOrderId);

        kombOrderOp.ifPresentOrElse(kombuchaOrder -> {
                    jmsTemplate.convertAndSend(JmsConfig.DEALLOCATE_ORDER_QUEUE,
                            DeallocateOrderRequest.builder()
                                    .kombuchaOrderDto(kombMapper.kombuchaOrderToKombuchaOrderDto(kombuchaOrder))
                                    .build());

                }, () -> log.error("Order Not Found. Id: \"" + kombOrderId.toString()+"\"")
        );

        log.debug("Sent Deallocation request to queue: \""+ JmsConfig.DEALLOCATE_ORDER_QUEUE +"\" for order id: \"" + kombOrderId.toString()+"\"");
    }
}