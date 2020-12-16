package com.proj.msorder.web.mappers;

import com.kombucha.model.brewery.KombuchaDto;
import com.kombucha.model.order.KombuchaOrderLineDto;
import com.proj.msorder.domain.KombuchaOrderLine;
import com.proj.msorder.services.kombucha.KombuchaService;

import java.util.Optional;

public abstract class KombuchaOrderLineMapperDecorator implements KombuchaOrderLineMapper {



    private  KombuchaOrderLineMapper mapper;
    private KombuchaService service;

    @Override
    public KombuchaOrderLine dtoToKombuchaOrderLine(KombuchaOrderLineDto dto) {
        return mapper.dtoToKombuchaOrderLine(dto);
    }

    @Override
    public KombuchaOrderLineDto kombuchaOrderLineToDto(KombuchaOrderLine orderLine) {
        return mapper.kombuchaOrderLineToDto(orderLine);
    }

    @Override
    public KombuchaOrderLineDto kombuchaOrderLineToDtoWithBreweryInfo(KombuchaOrderLine orderLine) {
        KombuchaOrderLineDto kold = mapper.kombuchaOrderLineToDto(orderLine);
        Optional<KombuchaDto> dtoOptional = service.getKombuchaByUpc(orderLine.getUpc());

        dtoOptional.ifPresent(dto->{
            kold.setKombName(dto.getKombName());
            kold.setKombStyle(dto.getKombStyle());
            kold.setPrice(dto.getPrice());
            kold.setKombId(dto.getId());


        });
        return kold;
    }


    public void setOrderMapper(KombuchaOrderLineMapper mapper) {
        this.mapper = mapper;
    }
    public void setService(KombuchaService service) {
        this.service = service;
    }
}

