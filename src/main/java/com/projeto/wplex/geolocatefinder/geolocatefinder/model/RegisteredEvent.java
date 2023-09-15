package com.projeto.wplex.geolocatefinder.geolocatefinder.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisteredEvent {

    private Integer deviceCode;
    private String prefix;
    private String timestamp;
    private String payload;
    private Double distance;
    private String companyName;

    public RegisteredEvent(Integer deviceCode, String prefix, String timestamp, String payload, Double distance, String companyName) {
        this.deviceCode = deviceCode;
        this.prefix = prefix;
        this.timestamp = timestamp;
        this.payload = payload;
        this.distance = distance;
        this.companyName = companyName;
    }

    public String toCSV() {
        return deviceCode + "," + distance + "," + timestamp + "," + payload;
    }
}
