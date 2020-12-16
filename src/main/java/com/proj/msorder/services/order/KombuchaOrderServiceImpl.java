package com.proj.msorder.services.order;

import com.kombucha.model.order.KombuchaOrderDto;
import com.kombucha.model.order.KombuchaOrderPagedList;
import com.proj.msorder.domain.Customer;
import com.proj.msorder.domain.KombuchaOrder;
import com.proj.msorder.domain.KombuchaOrderStatusEnum;
import com.proj.msorder.repositories.CustomerRepository;
import com.proj.msorder.repositories.KombuchaOrderRepository;
import com.proj.msorder.web.mappers.KombuchaOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class KombuchaOrderServiceImpl implements KombuchaOrderService {

    private final CustomerRepository customerRepository;
    private final KombuchaOrderRepository kombOrderRepository;

    private final KombuchaOrderMapper kombuchaOrderMapper;
    private final KombuchaOrderManager manager;


    @Override
    public KombuchaOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Page<KombuchaOrder> kombuchaOrderPage = kombOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

            return new KombuchaOrderPagedList(
                    kombuchaOrderPage.stream()
                            .map(kombuchaOrderMapper::kombuchaOrderToKombuchaOrderDto)
                            .collect(Collectors.toList()),
                    PageRequest.of(kombuchaOrderPage.getPageable().getPageNumber(),
                            kombuchaOrderPage.getPageable().getPageSize()),
                    kombuchaOrderPage.getTotalElements());
        } else {
            return null;
        }
    }

    @Override
    public KombuchaOrderDto createNewOrder(UUID customerId, KombuchaOrderDto kombOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            KombuchaOrder order = kombuchaOrderMapper.kombuchaOrderDtoToKombuchaOrder(kombOrderDto);
            order.setId(null);
            order.setOrderStatus(KombuchaOrderStatusEnum.NEW);
            order.setCustomer(customerOptional.get());
            order.getKombuchaOrderLines().forEach(line -> line.setKombuchaOrder(order));

            KombuchaOrder savedOrder = manager.newKombuchaOrder(order);

            log.error("Saved new Order with Id: \"" + savedOrder.getId() + "\"");

            return kombuchaOrderMapper.kombuchaOrderToKombuchaOrderDto(savedOrder);
        }
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public KombuchaOrderDto getOrderById(UUID customerId, UUID orderId) {

        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Optional<KombuchaOrder> orderOptional = kombOrderRepository.findById(orderId);

            if (orderOptional.isPresent()) {
                KombuchaOrder foundOrder = orderOptional.get();

                if (foundOrder.getCustomer().getId().equals(customerId)) {
                    return kombuchaOrderMapper.kombuchaOrderToKombuchaOrderDto(foundOrder);
                }
            } else {
                throw new RuntimeException("Order Not Found");
            }

        }
        throw new RuntimeException("Customer Not Found");


    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {

        if (verifyPairOfCustomerAndOrder(customerId, orderId))
            manager.pickUpOrder(orderId);


    }

    @Override
    public void cancelOrder(UUID customerId, UUID orderId) {

        if (verifyPairOfCustomerAndOrder(customerId, orderId))
            manager.cancelOrder(orderId);


    }

    /**
     * Verifies that both the Customer and the Order exist, and that the Order belongs to the Customer.
     * @param customerId
     * @param orderId
     * @return whether the order has been made by the customer or not
     */
    private Boolean verifyPairOfCustomerAndOrder(UUID customerId, UUID orderId){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Optional<KombuchaOrder> orderOptional = kombOrderRepository.findById(orderId);

            if (orderOptional.isPresent()) {
                KombuchaOrder foundOrder = orderOptional.get();

                if (foundOrder.getCustomer().getId().equals(customerId)) {
                    return true;
                }
            } else {
                throw new RuntimeException("Order Not Found");
            }

        }
            throw new RuntimeException("Customer Not Found");

    }
}
