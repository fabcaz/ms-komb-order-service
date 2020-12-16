package com.proj.msorder.services.kombucha;

import com.kombucha.model.brewery.KombuchaDto;

import java.util.Optional;
import java.util.UUID;


public interface KombuchaService {

    Optional<KombuchaDto> getKombuchaById(UUID id);
    Optional<KombuchaDto> getKombuchaByUpc(String upc);
}
