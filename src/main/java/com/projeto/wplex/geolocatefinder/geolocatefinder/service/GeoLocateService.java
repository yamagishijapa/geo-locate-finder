package com.projeto.wplex.geolocatefinder.geolocatefinder.service;

import com.projeto.wplex.geolocatefinder.geolocatefinder.model.RegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static com.projeto.wplex.geolocatefinder.geolocatefinder.utils.FileProcessingUtil.readEntryFileCsv;

@Slf4j
@Service
public class GeoLocateService {
    public void verificaDistanciaEntrada(Double latitude, Double longitude){

        log.info("Start reading csv: {}", LocalDateTime.now());
        List<RegisteredEvent> events = readEntryFileCsv(latitude, longitude);
        log.info("Ending reading csv: {}", LocalDateTime.now());

        if(!events.isEmpty()){
            log.info("Start sorting results: {}", LocalDateTime.now());
            events.sort(Comparator.comparing(RegisteredEvent::getDeviceCode)
                    .thenComparing(RegisteredEvent::getTimestamp));
            log.info("Finish sorting results: {}", LocalDateTime.now());
            fillResponseFile(events);
        } else {
            log.info("Nenhum evento do arquivo foi localizado dentro de um raio de 50m da coordenada informada. \n");
        }
    }

    private void fillResponseFile(List<RegisteredEvent> events){
        log.info("Start writing csv: {}", LocalDateTime.now());
        try (FileWriter writer = new FileWriter("results.csv")) {
            writer.write("device,distance,instant,payload \n");
            for (RegisteredEvent event : events) {
                writer.write(event.toCSV() + "\n");
            }
            log.info("End writing csv: {}", LocalDateTime.now());
            log.info("Resultados salvos em results.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
