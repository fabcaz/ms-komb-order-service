package com.proj.msorder.web.controllers;


import com.kombucha.model.order.KombuchaOrderDto;
import com.kombucha.model.order.KombuchaOrderPagedList;
import com.proj.msorder.services.order.KombuchaOrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("api/v1/customers/{customerId}/")
@RestController
public class KombuchaOrderController {



    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final KombuchaOrderService kombuchaOrderService;

    @Operation(summary = "Returns a paged list of KombuchaOrders." )
    @GetMapping(produces = {"application/json"}, path = "kombOrders")
    public ResponseEntity<KombuchaOrderPagedList> listOrders(@PathVariable("customerId") UUID customerId,
                                                            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                            @RequestParam(value = "pageSize", required = false) Integer pageSize){
        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        KombuchaOrderPagedList pagedList = kombuchaOrderService.listOrders(customerId, PageRequest.of(pageNumber,pageSize));

        return new ResponseEntity<>(pagedList, HttpStatus.OK);

    }

    @Operation(summary = "Returns the KombuchaOrder for which the ID and CustomerID have been provided as params." )
    @GetMapping("kombOrder/{kombId}")
    public ResponseEntity<KombuchaOrderDto> getKombuchaById(@PathVariable("customerId") UUID customerId,
                                                            @PathVariable("kombOrder") UUID kombOrder)  {

        return new ResponseEntity<>(kombuchaOrderService.getOrderById(customerId,kombOrder), HttpStatus.OK);
    }

    @Operation(summary = "Creates new KombuchaOrder")
    @PostMapping("kombOrder")
    public ResponseEntity<KombuchaOrderDto> createNewOrder(@PathVariable("customerId") UUID customerId,
                                                           @RequestBody KombuchaOrderDto kombOrderDto){

        return new ResponseEntity<>(kombuchaOrderService.createNewOrder(customerId, kombOrderDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Changes Status of the Order whose ID has been provided as a param to PICKED_UP")
    @PutMapping("kombOrder/{kombId}")
    public ResponseEntity<KombuchaOrderDto> pickUpOrder(@PathVariable("customerId") UUID customerId,
                                                        @PathVariable("kombId") UUID kombId){
        kombuchaOrderService.pickupOrder(customerId, kombId);
        return new ResponseEntity<>( HttpStatus.OK);
    }


    @Operation(summary = "Changes Status of the Order whose ID has been provided as a param to CANCELED." )
    @DeleteMapping("kombOrder/{kombId}/delete")
    public ResponseEntity deleteKombuchaById(@PathVariable("customerId") UUID customerId,
                                             @PathVariable("kombId") UUID kombId){
        kombuchaOrderService.cancelOrder(customerId, kombId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
