package com.projeto.wplex.geolocatefinder.geolocatefinder.components;

import com.projeto.wplex.geolocatefinder.geolocatefinder.service.GeoLocateServiceConsole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CommandSearchCsv implements CommandLineRunner {

    @Autowired
    GeoLocateServiceConsole service;

    @Override
    public void run(String... args) {

        Scanner scanner = new Scanner(System.in);

        service.iniciaProgramaConsole(scanner);


    }
}
