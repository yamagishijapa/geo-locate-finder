package com.projeto.wplex.geolocatefinder.controller;

import com.projeto.wplex.geolocatefinder.model.RegisteredEvent;
import com.projeto.wplex.geolocatefinder.service.GeoLocateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Controller
@RestController()
@RequestMapping("/geolocate")
public class GeoLocateController {

    @Autowired
    GeoLocateService service;

    @PostMapping("/generateCsv")
    public ResponseEntity<List<RegisteredEvent>> generateCsvMatches(@RequestParam Double latitude, Double longitude){
        List<RegisteredEvent> returnList = service.verificaDistanciaEntrada(latitude, longitude);
        return ResponseEntity.status(HttpStatus.OK).body(returnList);
    }
}
