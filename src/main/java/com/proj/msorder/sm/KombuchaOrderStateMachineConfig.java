package com.proj.msorder.sm;


import com.proj.msorder.domain.KombuchaOrderEventEnum;
import com.proj.msorder.domain.KombuchaOrderStatusEnum;
import com.proj.msorder.sm.actions.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@RequiredArgsConstructor
@Configuration
@EnableStateMachineFactory
public class KombuchaOrderStateMachineConfig extends StateMachineConfigurerAdapter<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> {

    private final ValidateOrderAction validateOrderAction;
    private final ValidationFailureAction validationFailureAction;
    private final AllocateOrderAction allocateOrderAction;
    private final AllocationFailureAction allocationFailureAction;
    private  final DeallocateOrderAction deallocateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(KombuchaOrderStatusEnum.NEW)
                .states(EnumSet.allOf(KombuchaOrderStatusEnum.class))
                .end(KombuchaOrderStatusEnum.PICKED_UP)
                .end(KombuchaOrderStatusEnum.DELIVERED)
                .end(KombuchaOrderStatusEnum.CANCELLED)
                .end(KombuchaOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(KombuchaOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(KombuchaOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<KombuchaOrderStatusEnum, KombuchaOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                .source(KombuchaOrderStatusEnum.NEW).target(KombuchaOrderStatusEnum.VALIDATION_PENDING)
                .event(KombuchaOrderEventEnum.VALIDATE_ORDER)
                .action(validateOrderAction)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.VALIDATION_PENDING).target(KombuchaOrderStatusEnum.VALIDATED)
                .event(KombuchaOrderEventEnum.VALIDATION_PASSED)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.VALIDATION_PENDING).target(KombuchaOrderStatusEnum.CANCELLED)
                .event(KombuchaOrderEventEnum.CANCEL_ORDER)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.VALIDATION_PENDING).target(KombuchaOrderStatusEnum.VALIDATION_EXCEPTION)
                .event(KombuchaOrderEventEnum.VALIDATION_FAILED)
                .action(validationFailureAction)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.VALIDATED).target(KombuchaOrderStatusEnum.ALLOCATION_PENDING)
                .event(KombuchaOrderEventEnum.ALLOCATE_ORDER)
                .action(allocateOrderAction)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.VALIDATED).target(KombuchaOrderStatusEnum.CANCELLED)
                .event(KombuchaOrderEventEnum.CANCEL_ORDER)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.ALLOCATION_PENDING).target(KombuchaOrderStatusEnum.ALLOCATED)
                .event(KombuchaOrderEventEnum.ALLOCATION_SUCCESS)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.ALLOCATION_PENDING).target(KombuchaOrderStatusEnum.ALLOCATION_EXCEPTION)
                .event(KombuchaOrderEventEnum.ALLOCATION_FAILED)
                .action(allocationFailureAction)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.ALLOCATION_PENDING).target(KombuchaOrderStatusEnum.CANCELLED)
                .event(KombuchaOrderEventEnum.CANCEL_ORDER)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.ALLOCATION_PENDING).target(KombuchaOrderStatusEnum.PENDING_INVENTORY)
                .event(KombuchaOrderEventEnum.ALLOCATION_NO_INVENTORY)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.PENDING_INVENTORY).target(KombuchaOrderStatusEnum.ALLOCATION_PENDING)
                .event(KombuchaOrderEventEnum.ALLOCATE_ORDER)
                .action(allocateOrderAction)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.ALLOCATED).target(KombuchaOrderStatusEnum.PICKED_UP)
                .event(KombuchaOrderEventEnum.ORDER_PICKED_UP)
                .and().withExternal()
                .source(KombuchaOrderStatusEnum.ALLOCATED).target(KombuchaOrderStatusEnum.CANCELLED)
                .event(KombuchaOrderEventEnum.CANCEL_ORDER)
                .action(deallocateOrderAction);
    }





}
