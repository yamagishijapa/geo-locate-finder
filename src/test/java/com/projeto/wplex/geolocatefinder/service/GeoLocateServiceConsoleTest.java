package com.projeto.wplex.geolocatefinder.service;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GeoLocateServiceConsoleTest {

    @Mock
    GeoLocateServiceConsole service;


    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        ReflectionTestUtils.setField(service, "entryFile", "src/test/resources/eventlogTest.csv");
    }

    @Test
    void printLogoWplex_shouldPrintLogo() {
        service.printLogoWplex();
        verify(service, times(1)).printLogoWplex();
    }

    @Test
    void validaEntradasUsuario_exitCommand_shouldExitProgram() {
        String input = "exit\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        doCallRealMethod().when(service).validaEntradasUsuario(scanner);

        service.validaEntradasUsuario(scanner);

        assertEquals("Saindo do programa.", outputStreamCaptor.toString().trim());
    }

    @Test
    void validaEntradasUsuario_wrongInput() {
        String input = "invalid command\nexit\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        doCallRealMethod().when(service).validaEntradasUsuario(scanner);

        service.validaEntradasUsuario(scanner);

        MatcherAssert.assertThat(outputStreamCaptor.toString().trim(), StringContains.containsString("Comando inválido. Use o formato: ./csv-search --location <latitude>,<longitude>"));
    }

    @Test
    void validaEntradasUsuario_wrongCoordinates() {
        String input = "./csv-search --location 12\nexit\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        doCallRealMethod().when(service).validaEntradasUsuario(scanner);

        service.validaEntradasUsuario(scanner);

        MatcherAssert.assertThat(outputStreamCaptor.toString().trim(),
                StringContains.containsString("Coordenadas inválidas. Use o formato: <latitude>,<longitude>"));
    }

    @Test
    void validaEntradasUsuario_entradaValida_noMatches() {
        String input = "./csv-search --location 12,12\nexit\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        doCallRealMethod().when(service).validaEntradasUsuario(scanner);

        service.validaEntradasUsuario(scanner);

        MatcherAssert.assertThat(outputStreamCaptor.toString().trim(),
                StringContains.containsString("Nenhum evento do arquivo foi localizado dentro de um raio de 50m da coordenada informada."));
    }

    @Test
    void validaEntradasUsuario_entradaValida_comMatches() {
        String input = "./csv-search --location -23.70041,-046.53713\nexit\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        doCallRealMethod().when(service).validaEntradasUsuario(scanner);

        service.validaEntradasUsuario(scanner);

        MatcherAssert.assertThat(outputStreamCaptor.toString().trim(),
                StringContains.containsString("Resultados salvos em results.csv"));
    }



    @Test
    void testaExecucaoComSucesso(){

    }
}
