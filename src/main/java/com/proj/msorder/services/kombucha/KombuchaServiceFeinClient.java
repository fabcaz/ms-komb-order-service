package com.proj.msorder.services.kombucha;

import com.kombucha.model.brewery.KombuchaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient(name = "kombucha-brewery")
public interface KombuchaServiceFeinClient {

    @RequestMapping(method = RequestMethod.GET, value = KombuchaService.KOMBUCHA_ID_PATH)
    ResponseEntity<KombuchaDto> getKombuchaById(@PathVariable UUID kombId);

    @RequestMapping(method = RequestMethod.GET, value = KombuchaService.KOMBUCHA_UPC_PATH)
    ResponseEntity<KombuchaDto> getKombuchaByUpc(@PathVariable String kombUpc);

}
