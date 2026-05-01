package ru.ivk.lab4.cli;

import ru.ivk.lab4.image.NormalizationMode;

import java.util.Locale;
import java.util.Scanner;

public final class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readString(String prompt, String defaultValue) {
        System.out.printf("%s [%s]: ", prompt, defaultValue);
        String value = scanner.nextLine().trim();
        return value.isEmpty() ? defaultValue : value;
    }

    public int readInt(String prompt, int defaultValue, int minValue) {
        while (true) {
            String value = readString(prompt, Integer.toString(defaultValue));

            try {
                int parsed = Integer.parseInt(value);

                if (parsed >= minValue) {
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.printf("Введите целое число не меньше %d.%n", minValue);
        }
    }

    public double readDouble(String prompt, double defaultValue, double minValue) {
        while (true) {
            String value = readString(prompt, Double.toString(defaultValue)).replace(',', '.');

            try {
                double parsed = Double.parseDouble(value);

                if (parsed >= minValue) {
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.printf(Locale.US, "Введите число не меньше %.4f.%n", minValue);
        }
    }

    public NormalizationMode readNormalizationMode(String prompt, NormalizationMode defaultValue) {
        while (true) {
            String value = readString(prompt + " (max/fixed/none)", defaultValue.name().toLowerCase(Locale.ROOT))
                    .toUpperCase(Locale.ROOT);

            try {
                return NormalizationMode.valueOf(value);
            } catch (IllegalArgumentException ignored) {
                System.out.println("Введите max, fixed или none.");
            }
        }
    }
}
