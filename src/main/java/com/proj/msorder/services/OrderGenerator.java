package com.proj.msorder.services;

import com.kombucha.model.order.KombuchaOrderDto;
import com.kombucha.model.order.KombuchaOrderLineDto;
import com.proj.msorder.bootstrap.KombOrderBootstrap;
import com.proj.msorder.domain.Customer;
import com.proj.msorder.domain.KombuchaOrder;
import com.proj.msorder.domain.KombuchaOrderLine;
import com.proj.msorder.repositories.CustomerRepository;
import com.proj.msorder.repositories.KombuchaOrderRepository;
import com.proj.msorder.services.order.KombuchaOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Profile("functionalTest")
@Service
@Slf4j
public class OrderGenerator {

    private final CustomerRepository customerRepository;
    private final KombuchaOrderService kombuchaOrderService;
    private final KombuchaOrderRepository kombuchaOrderRepository;
    private final List<String> kombUpcs = new ArrayList<>(3);

    public OrderGenerator(CustomerRepository customerRepository, KombuchaOrderService kombuchaOrderService, KombuchaOrderRepository kombuchaOrderRepository) {
        this.customerRepository = customerRepository;
        this.kombuchaOrderService = kombuchaOrderService;
        this.kombuchaOrderRepository = kombuchaOrderRepository;

        kombUpcs.add(KombOrderBootstrap.KOMB_1_UPC);
        kombUpcs.add(KombOrderBootstrap.KOMB_2_UPC);
        kombUpcs.add(KombOrderBootstrap.KOMB_3_UPC);
    }

    @Transactional
    @Scheduled(fixedRate = 2000)
    public void placeTastingRoomOrder() {

        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(KombOrderBootstrap.TEST_CUSTOMER);

        if (customerList.size() == 1) {
            placeRandomOrder(customerList.get(0));
        } else {
            log.error("Number of test-customers is not equal to 1");

            customerList.forEach(customer -> log.debug(customer.toString()));
        }
    }

    private void placeRandomOrder(Customer customer) {
        String randomUpc = kombUpcs.get(new Random().nextInt(kombUpcs.size()));

        KombuchaOrderLineDto orderLineDto = KombuchaOrderLineDto.builder()
                .upc(randomUpc)
                .orderQuantity(new Random().nextInt(200))
                .build();

        List<KombuchaOrderLineDto> orderLineList = new ArrayList<>(1);
        orderLineList.add(orderLineDto);

        KombuchaOrderDto orderDto = KombuchaOrderDto.builder()
                .kombuchaOrderLines(orderLineList)
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .build();
        KombuchaOrderDto savedOrder = kombuchaOrderService.createNewOrder(customer.getId(), orderDto);

        log.debug("Placed Order " +savedOrder.getId().toString());

    }


}
