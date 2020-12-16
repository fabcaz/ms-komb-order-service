package com.proj.msorder.web.mappers;

import com.kombucha.model.order.KombuchaOrderLineDto;
import com.proj.msorder.domain.KombuchaOrderLine;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

//@Mapper(uses = {DateMapper.class})
//@DecoratedWith(KombuchaOrderLineMapperDecorator.class)
public interface KombuchaOrderLineMapper {

    KombuchaOrderLine dtoToKombuchaOrderLine(KombuchaOrderLineDto dto);

    KombuchaOrderLineDto kombuchaOrderLineToDto(KombuchaOrderLine orderLine);

    KombuchaOrderLineDto kombuchaOrderLineToDtoWithBreweryInfo(KombuchaOrderLine orderLine);
}
