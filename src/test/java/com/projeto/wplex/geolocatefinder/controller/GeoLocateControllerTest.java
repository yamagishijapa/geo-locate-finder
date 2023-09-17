package com.projeto.wplex.geolocatefinder.controller;

import com.projeto.wplex.geolocatefinder.model.RegisteredEvent;
import com.projeto.wplex.geolocatefinder.service.GeoLocateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith({ MockitoExtension.class })
public class GeoLocateControllerTest {

    @InjectMocks
    private GeoLocateController controller;

    @Mock
    private GeoLocateService service;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateCsvMatchesTest(){
        List<RegisteredEvent> list = new ArrayList<>();
        when(service.verificaDistanciaEntrada(12.0, 12.0)).thenReturn(list);

        ResponseEntity<List<RegisteredEvent>> responseEntity = controller.generateCsvMatches(12.0, 12.0);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(list, responseEntity.getBody());
    }

}
