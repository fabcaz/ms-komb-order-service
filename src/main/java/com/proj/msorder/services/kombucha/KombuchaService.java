package com.proj.msorder.services.kombucha;

import com.kombucha.model.brewery.KombuchaDto;

import java.util.Optional;
import java.util.UUID;


public interface KombuchaService {

    String KOMBUCHA_ID_PATH = "/api/v1/kombucha/{kombId}";
    String KOMBUCHA_UPC_PATH = "/api/v1/kombuchaUpc/{kombUpc}";

    Optional<KombuchaDto> getKombuchaById(UUID id);
    Optional<KombuchaDto> getKombuchaByUpc(String upc);
}
