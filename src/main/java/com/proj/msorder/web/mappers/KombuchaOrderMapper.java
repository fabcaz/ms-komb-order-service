package com.proj.msorder.web.mappers;

import com.kombucha.model.order.KombuchaOrderDto;
import com.proj.msorder.domain.KombuchaOrder;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface KombuchaOrderMapper {

    KombuchaOrder kombuchaOrderDtoToKombuchaOrder(KombuchaOrderDto dto);

    KombuchaOrderDto kombuchaOrderToKombuchaOrderDto(KombuchaOrder kombuchaOrder);
}
