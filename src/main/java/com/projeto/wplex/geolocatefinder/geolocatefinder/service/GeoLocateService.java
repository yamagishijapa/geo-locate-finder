package com.projeto.wplex.geolocatefinder.geolocatefinder.service;

import com.projeto.wplex.geolocatefinder.geolocatefinder.model.RegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

@Slf4j
@Service
public class GeoLocateService {

    public void printLogoWplex(){
        System.out.println(" __          _______  _     ________   __");
        System.out.println(" \\ \\        / /  __ \\| |    |  ____\\ \\ / /");
        System.out.println("  \\ \\  /\\  / /| |__) | |    | |__   \\ V / ");
        System.out.println("   \\ \\/  \\/ / |  ___/| |    |  __|   > <  ");
        System.out.println("    \\  /\\  /  | |    | |____| |____ / . \\ ");
        System.out.println("     \\/  \\/   |_|    |______|______/_/ \\_\\\n");
    }

    public void verificaDistanciaEntrada(Scanner scanner){
        while (true) {

            System.out.println("Bem vindo ao sistema de verificação da localização dos eventos registrados. \n");
            System.out.println("Para verificar quais eventos ocorreram próximos (dentro de um raio de 50m) da localização, utilize o comando ./csv-search --location <latitude>,<longitude>). \n");
            System.out.print("Por exemplo: './csv-search --location -23.70041,-046.53713' \n");
            System.out.print("Para sair do programa, digite 'exit'. \n");
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

            double targetLatitude = Double.parseDouble(coordinates[0]);
            double targetLongitude = Double.parseDouble(coordinates[1]);

            List<RegisteredEvent> events = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/eventlog.csv"))) {
                for(String line : reader.lines().toList()){
                    if(!line.startsWith("device")){
                        String[] parts = line.split(",");

                        int start = line.indexOf("\"");
                        int stop = line.indexOf("\"", start+2);
                        String eventInfo = line.substring(start,stop);
                        Integer deviceCode = Integer.parseInt(parts[0]);
                        String prefix = parts[1];
                        String timestamp = parts[2];
                        String companyName = parts[7];

                        String[] eventInfoSplit = eventInfo.split(",");

                        if (eventInfoSplit.length >= 3) {
                            double latitude = Double.parseDouble(eventInfoSplit[2]);
                            double longitude = Double.parseDouble(eventInfoSplit[3].substring(0, eventInfoSplit[3].indexOf("<")));
                            double distance = 40.0;
                            //double distance = calculateDistance(targetLatitude, targetLongitude, latitude, longitude);

                            if (distance <= 50) {
                                events.add(new RegisteredEvent(deviceCode, prefix, timestamp, eventInfo, distance, companyName));
                            }
                        }
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            events.sort(Comparator.comparing(RegisteredEvent::getDeviceCode).thenComparing(RegisteredEvent::getTimestamp));

            try (FileWriter writer = new FileWriter("results.csv")) {
                writer.write("device,distance,instant,payload \n");
                for (RegisteredEvent event : events) {
                    writer.write(event.toCSV() + "\n");
                }
                System.out.println("Resultados salvos em results.csv");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
