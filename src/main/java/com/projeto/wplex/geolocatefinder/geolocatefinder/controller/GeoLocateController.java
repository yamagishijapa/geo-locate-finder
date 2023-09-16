package com.projeto.wplex.geolocatefinder.geolocatefinder.controller;

import com.projeto.wplex.geolocatefinder.geolocatefinder.service.GeoLocateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Controller
@RestController()
@RequestMapping("/geolocate")
public class GeoLocateController {

    @Autowired
    GeoLocateService service;

    @PostMapping("/generateCsv")
    public void generateCsvMatches(@RequestParam Double latitude, Double longitude){
        service.verificaDistanciaEntrada(latitude, longitude);
    }
}
