package com.projeto.wplex.geolocatefinder.model;

import lombok.*;


@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@ToString
public class RegisteredEvent {
    Integer deviceCode;
    String prefix;
    String timestamp;
    String payload;
    String companyName;
    Double latitude;
    Double longitude;
    Double distance;

    public String toCSV() {
        return deviceCode + "," + distance + "," + timestamp + "," + payload;
    }
}
