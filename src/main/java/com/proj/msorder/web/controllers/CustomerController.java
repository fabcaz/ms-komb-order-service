package com.proj.msorder.web.controllers;


import com.kombucha.model.order.CustomerDto;
import com.kombucha.model.order.CustomerPagedList;
import com.kombucha.model.order.KombuchaOrderPagedList;
import com.proj.msorder.domain.Customer;
import com.proj.msorder.services.customer.CustomerService;
import com.proj.msorder.services.order.KombuchaOrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("api/v1/customer")
@RestController
public class CustomerController {



    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final CustomerService customerService;

    @Operation(summary = "Returns a paged list of Customers." )
    @GetMapping(produces = {"application/json"}, path = "")
    public ResponseEntity<CustomerPagedList> listOrders(
                                                        @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                        @RequestParam(value = "pageSize", required = false) Integer pageSize){
        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        CustomerPagedList pagedList = customerService.listCustomers( PageRequest.of(pageNumber,pageSize));

        return new ResponseEntity<>(pagedList, HttpStatus.OK);

    }

    @Operation(summary = "Creates new Customer.")
    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto dto){
        return new ResponseEntity<>(customerService.createCustomer(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing Customer.")
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable("customerId")UUID customerId,
                                                      @RequestBody CustomerDto dto){
        return new ResponseEntity<>(customerService.updateCustomer(customerId, dto), HttpStatus.NO_CONTENT);
    }




}
