package br.org.institutobushido.providers.utils.date_utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    // Lista de formatadores de data
    private static final DateTimeFormatter[] formatters = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("yyyy-MM-dd"), // Formato padrão ISO
            DateTimeFormatter.ofPattern("dd-MM-yyyy"), // Formato dia-mês-ano com hífens
            DateTimeFormatter.ofPattern("dd/MM/yyyy")  // Formato dia/mês/ano com barras
    };

    // Método para converter String em LocalDate
    public static LocalDate getDataAsLocalDate(String dataString) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dataString, formatter);
            } catch (DateTimeParseException ignored) {
                // Ignora a exceção e tenta o próximo formatador
            }
        }
        throw new IllegalArgumentException("Data inválida: " + dataString);
    }

    // Novo método para converter LocalDate em String
    public static String formatarData(LocalDate data) {
        if (data == null) {
            return null; // ou lance uma exceção, dependendo do seu caso de uso
        }
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); // Formato de saída
    }
}