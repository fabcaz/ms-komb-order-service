package com.proj.msorder.sm.actions;

import com.proj.msorder.domain.KombuchaOrderEventEnum;
import com.proj.msorder.domain.KombuchaOrderStatusEnum;
import com.proj.msorder.services.order.KombuchaOrderManagerImpl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationFailureAction implements Action<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> {

    @Override
    public void execute(StateContext<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> context) {
        UUID kombOrderId = UUID.fromString((String) context.getMessage().getHeaders().get(KombuchaOrderManagerImpl.ORDER_ID_HEADER));

        log.debug("Allocation Failed for order id:  \""+ kombOrderId.toString()+"\"");
    }
}
