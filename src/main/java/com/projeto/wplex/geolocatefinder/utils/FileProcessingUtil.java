package com.projeto.wplex.geolocatefinder.utils;

import com.projeto.wplex.geolocatefinder.model.RegisteredEvent;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class FileProcessingUtil {

    @Value("${custom.fileName}")
    private static String entryFile;

    public static List<RegisteredEvent> readEntryFileCsv(Double targetLatitude, Double targetLongitude){
        List<RegisteredEvent> events = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(entryFile))) {
            for(String line : reader.lines().toList()){
                if(!line.startsWith("device")){
                    String[] parts = line.split(",");

                    int start = line.indexOf("\"");
                    int stop = line.indexOf("\"", start+2);
                    String eventInfo = line.substring(start,stop+1);
                    Integer deviceCode = Integer.parseInt(parts[0]);
                    String prefix = parts[1];
                    String timestamp = parts[2];
                    String companyName = parts[7];

                    String[] eventInfoSplit = eventInfo.split(",");

                    if (eventInfoSplit.length >= 3) {
                        double latitude = Double.parseDouble(eventInfoSplit[2]);
                        double longitude = Double.parseDouble(eventInfoSplit[3].substring(0, eventInfoSplit[3].indexOf("<")));
                        Double distancia = calculateDistance(targetLatitude, targetLongitude, latitude, longitude);

                        if(distancia <= 50){
                            events.add(RegisteredEvent.builder()
                                    .deviceCode(deviceCode)
                                    .prefix(prefix)
                                    .timestamp(convertTimeStampToIso(timestamp))
                                    .payload(eventInfo)
                                    .companyName(companyName)
                                    .distance(formatDoubleDecimal(distancia))
                                    .latitude(latitude)
                                    .longitude(longitude)
                                    .build());
                        }
                    }
                }
            }
            return events;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Double calculateDistance(Double targetLatitude, Double targetLongitude, Double latitude, Double longitude){
        // Raio da Terra em metros
        double earthRadius = 6371000;

        // Converte diferenças de latitude e longitude de graus para radianos
        double lat1Rad = Math.toRadians(targetLatitude);
        double lon1Rad = Math.toRadians(targetLongitude);

        double lat2Rad = Math.toRadians(latitude);
        double lon2Rad = Math.toRadians(longitude);

        // Diferença entre as latitudes e longitudes
        double latDiff = lat2Rad - lat1Rad;
        double lonDiff = lon2Rad - lon1Rad;

        // Fórmula de Haversine
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distância em metros

        return earthRadius * c;
    }

    public static Double formatDoubleDecimal(Double valorIni){
        DecimalFormat format = new DecimalFormat("0.00");
        String string = format.format(valorIni);
        String[] part = string.split("[,]");
        String string2 = part[0]+"."+part[1];
        return Double.parseDouble(string2);
    }

    public static String convertTimeStampToIso(String timeStamp){

        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        OffsetDateTime offsetDateTime = OffsetDateTime.parse(timeStamp, inputFormatter);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        return offsetDateTime.format(outputFormatter);
    }
}
