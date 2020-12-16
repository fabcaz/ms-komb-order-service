package com.kombucha.model.order;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeallocateOrderRequest {
    private KombuchaOrderDto kombuchaOrderDto;
}
