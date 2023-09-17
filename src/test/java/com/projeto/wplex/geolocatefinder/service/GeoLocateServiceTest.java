package com.projeto.wplex.geolocatefinder.service;

import com.projeto.wplex.geolocatefinder.model.RegisteredEvent;
import com.projeto.wplex.geolocatefinder.utils.FileProcessingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class GeoLocateServiceTest {

    @InjectMocks
    GeoLocateService service;

    @BeforeEach
    void SetUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(FileProcessingUtil.class, "entryFile", "src/test/resources/eventlogTest.csv");
    }

    @Test
    void testVerificaDistanciaEntrada_WithValidData() {
        Double latitude = -23.70041;
        Double longitude = -046.53713;

        List<RegisteredEvent> simulatedEvents = new ArrayList<>();
        simulatedEvents.add(new RegisteredEvent(3182,
                "RUS00",
                "2018-12-10 10:55:40.000",
                "\">RUS00,101218095501,-23.70041,-046.53713<\"",
                "acme",
                latitude,
                longitude,
                0.0));

        List<RegisteredEvent> listEvents = service.verificaDistanciaEntrada(latitude, longitude);

        assertEquals(listEvents, simulatedEvents);
    }

    @Test
    void testVerificaDistanciaEntrada_WithEmptyData() {
        Double latitude = 52.5200;
        Double longitude = 13.4050;

        List<RegisteredEvent> events = service.verificaDistanciaEntrada(latitude, longitude);

        // Verificar se o método retorna uma lista vazia
        assertTrue(events.isEmpty());
    }
}
