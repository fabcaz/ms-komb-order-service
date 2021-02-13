package com.proj.msorder.services.kombucha;

import com.kombucha.model.brewery.KombuchaDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Profile("!serv-discovery")
@ConfigurationProperties(prefix = "ms.komb-brewery", ignoreUnknownFields = false)
@Service
public class KombuchaServiceImpl implements KombuchaService {

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
    public Optional<KombuchaDto> getKombuchaById(UUID kombId) {
        ResponseEntity<KombuchaDto> responseKombDto = restTemplate.exchange(serviceHost + KOMBUCHA_ID_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<KombuchaDto>() {},
                (Object)kombId);
        return Optional.ofNullable(responseKombDto.getBody());
//        return Optional.of(restTemplate.getForObject(serviceHost+KOMBUCHA_ID_PATH+kombId.toString(), KombuchaDto.class));
    }

    @Override
    public Optional<KombuchaDto> getKombuchaByUpc(String kombUpc) {
        ResponseEntity<KombuchaDto> responseKombDto = restTemplate.exchange(serviceHost + KOMBUCHA_UPC_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<KombuchaDto>() {},
                (Object)kombUpc);
        return Optional.ofNullable(responseKombDto.getBody());
        //return Optional.of(restTemplate.getForObject(serviceHost+KOMBUCHA_UPC_PATH+kombUpc , KombuchaDto.class));
    }
}
