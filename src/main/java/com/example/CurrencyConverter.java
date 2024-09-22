package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CurrencyConverter {

    private static final String API_KEY = "41a843d8d233b2e80f331b08"; // Reemplaza con tu clave de API
    private static final String API_URL_BASE = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    private static final Map<Integer, String> CURRENCY_OPTIONS = new HashMap<>();
    static {
        CURRENCY_OPTIONS.put(1, "USD"); // Dólar estadounidense
        CURRENCY_OPTIONS.put(2, "EUR"); // Euro
        CURRENCY_OPTIONS.put(3, "GBP"); // Libra esterlina
        CURRENCY_OPTIONS.put(4, "JPY"); // Yen japonés
        CURRENCY_OPTIONS.put(5, "COP"); // Peso colombiano
        CURRENCY_OPTIONS.put(6, "ARG"); // Peso argentino
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("______________________________________________________");
            System.out.println("Seleccione la moneda desde la que desea convertir:");
            printCurrencyOptions();
            int fromOption = scanner.nextInt();
            scanner.nextLine();  // Consume the newline

            System.out.println("______________________________________________________");
            System.out.println("Seleccione la moneda a la que desea convertir:");
            printCurrencyOptions();
            int toOption = scanner.nextInt();
            scanner.nextLine();  // Consume the newline

            System.out.println("______________________________________________________");
            System.out.println("Introduzca la cantidad:");
            double amount = scanner.nextDouble();
            scanner.nextLine();  // Consume the newline

            String fromCurrency = CURRENCY_OPTIONS.get(fromOption);
            String toCurrency = CURRENCY_OPTIONS.get(toOption);
            String fromCurrencyName = getCurrencyName(fromCurrency);
            String toCurrencyName = getCurrencyName(toCurrency);

            try {
                double convertedAmount = convertCurrency(fromCurrency, toCurrency, amount);
                System.out.println("______________________________________________________");
                System.out.printf("La cantidad %.2f %s equivale a %.2f %s.%n", amount, fromCurrencyName, convertedAmount, toCurrencyName);
            } catch (IOException | InterruptedException e) {
                System.err.println("Error al obtener la tasa de conversión.");
                e.printStackTrace();
            }

            System.out.println("______________________________________________________");
            System.out.println("Seleccione una opción:");
            System.out.println("1. Hacer otra conversión");
            System.out.println("2. Salir");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline

            if (choice != 1) {
                running = false;
            }
        }

        System.out.println("Saliendo...");
        scanner.close();
    }

    private static void printCurrencyOptions() {
        for (Map.Entry<Integer, String> entry : CURRENCY_OPTIONS.entrySet()) {
            String currencyCode = entry.getValue();
            String currencyName = getCurrencyName(currencyCode);
            System.out.printf("%d. %s - %s%n", entry.getKey(), currencyCode, currencyName);
        }
    }

    private static String getCurrencyName(String code) {
        switch (code) {
            case "USD": return "Dólar estadounidense";
            case "EUR": return "Euro";
            case "GBP": return "Libra esterlina";
            case "JPY": return "Yen japonés";
            case "COP": return "Peso colombiano";
            case "ARG": return "Peso argentino";
            default: return "Desconocida";
        }
    }

    private static double convertCurrency(String fromCurrency, String toCurrency, double amount)
            throws IOException, InterruptedException {
        String url = API_URL_BASE + fromCurrency;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.body());
        JsonNode ratesNode = rootNode.path("conversion_rates");
        double rate = ratesNode.path(toCurrency).asDouble();
        return amount * rate;
    }
}
