package com.projeto.wplex.geolocatefinder.utils;

import com.projeto.wplex.geolocatefinder.model.RegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FileProcessingUtilTest {

    @BeforeEach
    void SetUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateDistance() {
        double lat1 = 52.5200;
        double lon1 = 13.4050;
        double lat2 = 48.8566;
        double lon2 = 2.3522;

        double distance = FileProcessingUtil.calculateDistance(lat1, lon1, lat2, lon2);

        // Assert que a distância entre Berlim (lat1, lon1) e Paris (lat2, lon2) é aproximadamente 878,55 km
        assertEquals(877463.0, distance, 100.0); // Aceitando uma margem de erro de 100 metros
    }

    @Test
    void testFormatDoubleDecimal() {
        double value = 123.456789;
        double formattedValue = FileProcessingUtil.formatDoubleDecimal(value);

        // Assert que o valor formatado deve ser igual a 123.46 com uma margem de erro de 0.01
        assertEquals(123.46, formattedValue, 0.01);
    }

    @Test
    void testConvertTimeStampToIso() {
        String timeStamp = "2023-09-17T10:30:00+02:00";
        String expectedIsoFormat = "2023-09-17 10:30:00.000";

        String isoFormat = FileProcessingUtil.convertTimeStampToIso(timeStamp);

        // Assert que o formato ISO convertido é igual ao esperado
        assertEquals(expectedIsoFormat, isoFormat);
    }

    @Test
    void testReadEntryFileCsv() {
        // Chamar o método a ser testado
        List<RegisteredEvent> events = FileProcessingUtil.readEntryFileCsv(-23.70041,-046.53713);

        // Verificar se o método retorna uma lista com dois eventos
        assertEquals(11, events.size());
    }
}
