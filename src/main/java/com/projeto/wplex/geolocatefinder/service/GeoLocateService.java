package com.projeto.wplex.geolocatefinder.service;

import com.projeto.wplex.geolocatefinder.model.RegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.projeto.wplex.geolocatefinder.utils.FileProcessingUtil.readEntryFileCsv;

@Slf4j
@Service
public class GeoLocateService {
    public List<RegisteredEvent> verificaDistanciaEntrada(Double latitude, Double longitude){

        log.info("Start reading csv: {}", LocalDateTime.now());
        List<RegisteredEvent> events = readEntryFileCsv(latitude, longitude);
        log.info("Ending reading csv: {}", LocalDateTime.now());

        if(!events.isEmpty()){
            log.info("Start sorting results: {}", LocalDateTime.now());
            events.sort(Comparator.comparing(RegisteredEvent::getDeviceCode)
                    .thenComparing(RegisteredEvent::getTimestamp));
            log.info("Finish sorting results: {}", LocalDateTime.now());
            return events;
        } else {
            log.info("Nenhum evento do arquivo foi localizado dentro de um raio de 50m da coordenada informada. \n");
            return new ArrayList<>();
        }
    }
}
