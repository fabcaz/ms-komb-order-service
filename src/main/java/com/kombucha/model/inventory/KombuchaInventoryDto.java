package com.kombucha.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KombuchaInventoryDto {

    private UUID kombInventoryId;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastModifiedDate;
    private UUID kombId;
    private String upc;
    private Integer qtyOnHand;
}

