package com.proj.msorder.services.kombucha;

import com.kombucha.model.brewery.KombuchaDto;
import com.kombucha.model.order.KombuchaOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Profile("serv-discovery")
@Service
public class KombuchaServiceFein implements KombuchaService {

    private final KombuchaServiceFeinClient kombuchaServiceFeinClient;

    @Override
    public Optional<KombuchaDto> getKombuchaById(UUID kombId) {
        log.debug("Getting Kombucha with Id: \""+ kombId+"\" from kombucha-brewery Service");
        ResponseEntity<KombuchaDto> kombuchaDtoResponseEntity = kombuchaServiceFeinClient.getKombuchaById(kombId);
        KombuchaDto dto = kombuchaDtoResponseEntity.getBody();
        log.debug("Got: "+ dto.toString());
        return Optional.ofNullable(dto);
    }

    @Override
    public Optional<KombuchaDto> getKombuchaByUpc(String kombUpc) {
        log.debug("Getting Kombucha with Upc: \""+ kombUpc+"\" from kombucha-brewery Service");
        ResponseEntity<KombuchaDto> kombuchaDtoResponseEntity = kombuchaServiceFeinClient.getKombuchaByUpc(kombUpc);
        KombuchaDto dto = kombuchaDtoResponseEntity.getBody();
        log.debug("Got: "+ dto.toString());
        return Optional.ofNullable(dto);
    }
}
