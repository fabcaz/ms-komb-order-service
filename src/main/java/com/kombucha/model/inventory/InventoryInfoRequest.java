package com.kombucha.model.inventory;

import com.kombucha.model.brewery.KombuchaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryInfoRequest {
    KombuchaDto dto;
}
