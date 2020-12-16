package com.proj.msorder.sm.Interceptors;

import com.proj.msorder.domain.KombuchaOrder;
import com.proj.msorder.domain.KombuchaOrderEventEnum;
import com.proj.msorder.domain.KombuchaOrderStatusEnum;
import com.proj.msorder.repositories.KombuchaOrderRepository;
import com.proj.msorder.services.order.KombuchaOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KombuchaOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> {

    private final KombuchaOrderRepository kombuchaOrderRepository;

    @Transactional
    @Override
    public void preStateChange(State<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> state, Message<KombuchaOrderEventEnum> message, Transition<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> transition, StateMachine<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> stateMachine) {
        log.debug("Pre-State Change");

        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(KombuchaOrderManagerImpl.ORDER_ID_HEADER, " ")))
                .ifPresent(orderId -> {
                    log.debug("Saving state for order [id: " + orderId + ", Status: " + state.getId()+ "]");

                    KombuchaOrder kombuchaOrder = kombuchaOrderRepository.getOne(UUID.fromString(orderId));
                    kombuchaOrder.setOrderStatus(state.getId());
                    kombuchaOrderRepository.saveAndFlush(kombuchaOrder);
                });
    }
}
