package com.proj.msorder.services.customer;


import com.kombucha.model.order.CustomerDto;
import com.kombucha.model.order.CustomerPagedList;
import com.proj.msorder.domain.Customer;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface CustomerService {

    CustomerPagedList listCustomers(Pageable pageable);

    CustomerDto createCustomer(CustomerDto dto);

    CustomerDto updateCustomer(UUID customerId, CustomerDto dto);
}
