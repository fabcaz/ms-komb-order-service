package com.proj.msorder.web.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.kombucha.model.brewery.KombuchaDto;
import com.kombucha.model.order.KombuchaOrderLineDto;
import com.proj.msorder.domain.KombuchaOrderLine;
import com.proj.msorder.services.kombucha.KombuchaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(WireMockExtension.class)
class KombuchaOrderLineMapperImplTest {

    public static final String KOMB_1_UPC = "0101234200036";
    public static final UUID KOMB_1_UUID = UUID.fromString("0a818933-087d-47f2-ad83-2f986ed087eb");


    @Autowired
    KombuchaOrderLineMapperImpl mapper;

    @Autowired
    KombuchaService service;

    @Autowired
    ObjectMapper objectMapper;

    @Managed
    WireMockServer wireMockServer = with(wireMockConfig().port(8083));


    @Test
    void testKombuchaOrderLineToDtoWithBreweryInfo() throws JsonProcessingException {
        KombuchaOrderLine orderLine = KombuchaOrderLine.builder()
                .upc(KOMB_1_UPC)
                .orderQuantity(5)
                .quantityAllocated(7)
                .build();

        KombuchaDto testDto = KombuchaDto.builder()
                .id(KOMB_1_UUID)
                .upc(KOMB_1_UPC)
                .kombName("testName")
                .kombStyle("SomeStyle")
                .price(new BigDecimal(4.99))
                .build();

        wireMockServer.stubFor(get(KombuchaService.KOMBUCHA_UPC_PATH + KOMB_1_UPC)
        .willReturn(okJson(objectMapper.writeValueAsString(testDto))));

        KombuchaOrderLineDto dtoWithBreweryInfo = mapper.kombuchaOrderLineToDtoWithBreweryInfo(orderLine);

        verify(1, getRequestedFor(urlEqualTo(KombuchaService.KOMBUCHA_UPC_PATH + KOMB_1_UPC)));


        assertEquals(testDto.getUpc(), dtoWithBreweryInfo.getUpc());
        assertEquals(testDto.getPrice(), dtoWithBreweryInfo.getPrice());

    }
}