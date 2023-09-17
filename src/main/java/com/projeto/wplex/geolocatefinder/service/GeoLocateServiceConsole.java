package com.projeto.wplex.geolocatefinder.service;

import com.projeto.wplex.geolocatefinder.model.RegisteredEvent;
import com.projeto.wplex.geolocatefinder.utils.FileProcessingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static com.projeto.wplex.geolocatefinder.geolocatefinder.utils.FileProcessingUtil.*;

@Slf4j
@Service
public class GeoLocateServiceConsole {

    @Value("${custom.fileName}")
    private String entryFile;

    public void printLogoWplex(){
        System.out.println(" __          _______  _     ________   __");
        System.out.println(" \\ \\        / /  __ \\| |    |  ____\\ \\ / /");
        System.out.println("  \\ \\  /\\  / /| |__) | |    | |__   \\ V / ");
        System.out.println("   \\ \\/  \\/ / |  ___/| |    |  __|   > <  ");
        System.out.println("    \\  /\\  /  | |    | |____| |____ / . \\ ");
        System.out.println("     \\/  \\/   |_|    |______|______/_/ \\_\\\n");
    }

    public void iniciaProgramaConsole(Scanner scanner){

        printLogoWplex();

        System.out.println("Bem vindo ao sistema de verificação da localização dos eventos registrados. \n");
        System.out.println("Para verificar quais eventos ocorreram próximos (dentro de um raio de 50m) da localização, utilize o comando ./csv-search --location <latitude>,<longitude>). \n");
        System.out.print("Por exemplo: './csv-search --location -23.70041,-046.53713' \n");
        System.out.print("Para sair do programa, digite 'exit'. \n");

        validaEntradasUsuario(scanner);
    }

    public void validaEntradasUsuario(Scanner scanner){

        while (true) {

            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Saindo do programa. \n");
                break;
            }

            if (!userInput.startsWith("./csv-search --location ")) {
                System.out.println("Comando inválido. Use o formato: ./csv-search --location <latitude>,<longitude> \n");
                continue;
            }

            String[] commandParts = userInput.split(" ");
            String[] coordinates = commandParts[2].split(",");

            if (coordinates.length != 2) {
                System.out.println("Coordenadas inválidas. Use o formato: <latitude>,<longitude> \n");
                continue;
            }

            Double targetLatitude = Double.parseDouble(coordinates[0]);
            Double targetLongitude = Double.parseDouble(coordinates[1]);

            //sortEvents(readCsvToList(0 , 7011860, targetLatitude, targetLongitude));
            readCsvInParallel(targetLatitude, targetLongitude);
        }
    }

    private void readCsvInParallel(Double targetLatitude, Double targetLongitude) {
        List<RegisteredEvent> events = new ArrayList<>();
        List<Thread> threads = getThreads(events, targetLatitude, targetLongitude);

        // Aguarde até que todas as threads tenham concluído
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Continue com o processamento (ordenar e escrever no arquivo)
        sortEvents(events);
        fillResponseFileConsole(events);
    }

    private List<Thread> getThreads(List<RegisteredEvent> events, Double targetLatitude, Double targetLongitude) {
        List<Thread> threads = new ArrayList<>();
        int indexLimit = checkFileRegisterSize() / 4;
        int indexInicio = 0;
        int indexFim = 0;
        for (int i = 0; i < 4; i++) {

            indexInicio = indexLimit * i;

            if(indexFim == 0){
                indexFim = indexLimit;
            } else{
                indexFim = indexFim + indexLimit;
            }

            int finalIndexInicio = indexInicio;
            int finalIndexFim = indexFim;
            Thread thread = new Thread(() -> {
                // Cada thread lê e processa uma parte do arquivo
                List<RegisteredEvent> eventsChunk = readCsvToList(finalIndexInicio, finalIndexFim, targetLatitude, targetLongitude);

                // Adicione os resultados à lista compartilhada
                synchronized (events) {
                    events.addAll(eventsChunk);
                }
            });
            threads.add(thread);
            thread.start();
        }
        return threads;
    }

    private Integer checkFileRegisterSize(){
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            return reader.lines().toList().size();
        }catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    private List<RegisteredEvent> readCsvToList(Integer startIndex, Integer endIndex, Double targetLatitude, Double targetLongitude){
        List<RegisteredEvent> events = new ArrayList<>();
        log.info("Start reading csv - startIndex {} to endIndex {} : {}", startIndex, endIndex, LocalDateTime.now());
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            int lineNumber = 0;
            for(String line : reader.lines().toList()){
                if(!line.startsWith("device")){
                    if(lineNumber >= startIndex && lineNumber < endIndex){
                        String[] parts = line.split(",");

                        int start = line.indexOf("\"");
                        int stop = line.indexOf("\"", start+2);
                        String eventInfo = line.substring(start,stop);
                        Integer deviceCode = Integer.parseInt(parts[0]);
                        String timestamp = parts[2];

                        String[] eventInfoSplit = eventInfo.split(",");

                        if (eventInfoSplit.length >= 3) {
                            Double latitude = Double.parseDouble(eventInfoSplit[2]);
                            Double longitude = Double.parseDouble(eventInfoSplit[3].substring(0, eventInfoSplit[3].indexOf("<")));
                            Double distancia = calculateDistance(targetLatitude, targetLongitude, latitude, longitude);

                            if(distancia <= 50){
                                events.add(RegisteredEvent.builder()
                                        .deviceCode(deviceCode)
                                        .timestamp(convertTimeStampToIso(timestamp))
                                        .payload(eventInfo)
                                        .distance(formatDoubleDecimal(distancia))
                                        .build());
                            }
                        }
                    }
                }
            }
            log.info("Ending reading csv: {}", LocalDateTime.now());
            return events;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sortEvents(List<RegisteredEvent> events){
        if(!events.isEmpty()){
            log.info("Start sorting results: {}", LocalDateTime.now());
            events.sort(Comparator.comparing(RegisteredEvent::getDeviceCode)
                    .thenComparing(RegisteredEvent::getTimestamp));
            log.info("Finish sorting results: {}", LocalDateTime.now());
        } else {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("Nenhum evento do arquivo foi localizado dentro de um raio de 50m da coordenada informada. \n");
        }
    }

    private void fillResponseFileConsole(List<RegisteredEvent> events){
        log.info("Start writing csv: {}", LocalDateTime.now());
        try (FileWriter writer = new FileWriter("results.csv")) {
            writer.write("device,distance,instant,payload \n");
            for (RegisteredEvent event : events) {
                writer.write(event.toCSV() + "\n");
            }
            log.info("End writing csv: {}", LocalDateTime.now());
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("Resultados salvos em results.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
