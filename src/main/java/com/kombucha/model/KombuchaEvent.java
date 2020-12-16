package com.kombucha.model;


import com.kombucha.model.brewery.KombuchaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KombuchaEvent {

    static final long serialVersionUID = -5943515597148163111L;

    private KombuchaDto dto;
}
