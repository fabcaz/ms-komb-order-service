package com.proj.msorder.web.mappers;

import com.kombucha.model.brewery.KombuchaDto;
import com.kombucha.model.order.KombuchaOrderLineDto;
import com.proj.msorder.domain.KombuchaOrderLine;
import com.proj.msorder.services.kombucha.KombuchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class KombuchaOrderLineMapperImpl implements KombuchaOrderLineMapper {

    @Autowired
    public void setDateMapper(DateMapper dateMapper) {
        this.dateMapper = dateMapper;
    }

    @Autowired
    public void setService(KombuchaService service) {
        this.service = service;
    }

    private DateMapper dateMapper;


    private KombuchaService service;

    @Override
    public KombuchaOrderLine dtoToKombuchaOrderLine(KombuchaOrderLineDto dto) {
        if ( dto == null ) {
            return null;
        }

        KombuchaOrderLine.KombuchaOrderLineBuilder kombuchaOrderLine = KombuchaOrderLine.builder();

        kombuchaOrderLine.id( dto.getId() );
        if ( dto.getVersion() != null ) {
            kombuchaOrderLine.version( dto.getVersion().longValue() );
        }
        kombuchaOrderLine.createdDate( dateMapper.asTimestamp( dto.getCreatedDate() ) );
        kombuchaOrderLine.lastModifiedDate( dateMapper.asTimestamp( dto.getLastModifiedDate() ) );
        kombuchaOrderLine.kombId( dto.getKombId() );
        kombuchaOrderLine.upc( dto.getUpc() );
        kombuchaOrderLine.orderQuantity( dto.getOrderQuantity() );
        kombuchaOrderLine.quantityAllocated( dto.getQuantityAllocated() );

        return kombuchaOrderLine.build();
    }

    @Override
    public KombuchaOrderLineDto kombuchaOrderLineToDto(KombuchaOrderLine orderLine) {
        if ( orderLine == null ) {
            return null;
        }

        KombuchaOrderLineDto.KombuchaOrderLineDtoBuilder kombuchaOrderLineDto = KombuchaOrderLineDto.builder();

        kombuchaOrderLineDto.id( orderLine.getId() );
        if ( orderLine.getVersion() != null ) {
            kombuchaOrderLineDto.version( orderLine.getVersion().intValue() );
        }
        kombuchaOrderLineDto.createdDate( dateMapper.asOffsetDateTime( orderLine.getCreatedDate() ) );
        kombuchaOrderLineDto.lastModifiedDate( dateMapper.asOffsetDateTime( orderLine.getLastModifiedDate() ) );
        kombuchaOrderLineDto.upc( orderLine.getUpc() );
        kombuchaOrderLineDto.kombId( orderLine.getKombId() );
        kombuchaOrderLineDto.orderQuantity( orderLine.getOrderQuantity() );
        kombuchaOrderLineDto.quantityAllocated( orderLine.getQuantityAllocated() );

        return kombuchaOrderLineDto.build();
    }

    @Override
    public KombuchaOrderLineDto kombuchaOrderLineToDtoWithBreweryInfo(KombuchaOrderLine orderLine) {
        KombuchaOrderLineDto kold = kombuchaOrderLineToDto(orderLine);
        Optional<KombuchaDto> dtoOptional = service.getKombuchaByUpc(orderLine.getUpc());

        dtoOptional.ifPresent(dto->{
            kold.setKombName(dto.getKombName());
            kold.setKombStyle(dto.getKombStyle());
            kold.setPrice(dto.getPrice());
            kold.setKombId(dto.getId());


        });
        return kold;
    }
}
