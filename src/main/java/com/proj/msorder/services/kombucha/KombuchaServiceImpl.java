package com.proj.msorder.services.kombucha;

import com.kombucha.model.brewery.KombuchaDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@ConfigurationProperties(prefix = "ms.komb-brewery", ignoreUnknownFields = false)
@Service
public class KombuchaServiceImpl implements KombuchaService {

    public final static String KOMBUCHA_ID_PATH = "/api/v1/kombucha/";
    public final static String KOMBUCHA_UPC_PATH = "/api/v1/kombuchaUpc/";

    private final RestTemplate restTemplate;

    public KombuchaServiceImpl(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
    }

    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost;
    }

    //@Value("${ms.komb-brewery.service-host}")
    public String serviceHost;


    @Override
    public Optional<KombuchaDto> getKombuchaById(UUID id) {
        return Optional.of(restTemplate.getForObject(serviceHost+KOMBUCHA_ID_PATH+id.toString(), KombuchaDto.class));
    }

    @Override
    public Optional<KombuchaDto> getKombuchaByUpc(String upc) {
        return Optional.of(restTemplate.getForObject(serviceHost+KOMBUCHA_UPC_PATH+upc , KombuchaDto.class));
    }
}
