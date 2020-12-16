package com.proj.msorder.web.mappers;


import com.kombucha.model.order.CustomerDto;
import com.proj.msorder.domain.Customer;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDto dto);

    CustomerDto customerToCustomerDto(Customer customer);

}
