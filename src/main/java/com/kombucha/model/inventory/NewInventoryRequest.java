package com.kombucha.model.inventory;

import com.kombucha.model.KombuchaEvent;
import com.kombucha.model.brewery.KombuchaDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewInventoryRequest extends KombuchaEvent {
    public NewInventoryRequest(KombuchaDto kombuchaDto){super(kombuchaDto);}
}
