package com.projeto.wplex.geolocatefinder.geolocatefinder.components;

import com.projeto.wplex.geolocatefinder.geolocatefinder.service.GeoLocateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CommandSearchCsv implements CommandLineRunner {

    @Autowired
    GeoLocateService service;

    @Override
    public void run(String... args) {

        Scanner scanner = new Scanner(System.in);

        service.printLogoWplex();
        service.verificaDistanciaEntrada(scanner);


    }
}
