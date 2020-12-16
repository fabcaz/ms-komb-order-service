package com.proj.msorder.services.customer;

import com.kombucha.model.order.CustomerDto;
import com.kombucha.model.order.CustomerPagedList;
import com.proj.msorder.domain.Customer;
import com.proj.msorder.exceptions.NotFoundException;
import com.proj.msorder.repositories.CustomerRepository;
import com.proj.msorder.web.mappers.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerPagedList listCustomers(Pageable pageable) {

        Page<Customer> customerPage = customerRepository.findAll(pageable);

        return new CustomerPagedList(customerPage
                .stream()
                .map(customerMapper::customerToCustomerDto)
                .collect(Collectors.toList()),
                PageRequest.of(customerPage.getPageable().getPageNumber(),
                        customerPage.getPageable().getPageSize()),
                customerPage.getTotalElements());
    }

    @Override
    public CustomerDto createCustomer(CustomerDto dto) {
        return customerMapper.customerToCustomerDto(
                customerRepository.save(
                        customerMapper.customerDtoToCustomer(dto)
                )
        );
    }

    @Override
    public CustomerDto updateCustomer(UUID customerId, CustomerDto dto) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(NotFoundException::new);

        customer.setCustomerName(dto.getCustomerName());

        return customerMapper.customerToCustomerDto(customerRepository.save(customer));
    }


}
