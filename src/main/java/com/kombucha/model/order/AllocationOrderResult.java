package com.kombucha.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationOrderResult {
    private KombuchaOrderDto kombuchaOrderDto;
    private Boolean pendingInventory = false;
    private Boolean allocationError = false;
}
