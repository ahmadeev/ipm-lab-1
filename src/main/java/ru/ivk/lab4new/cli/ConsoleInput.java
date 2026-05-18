package ru.ivk.lab4new.cli;

import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.image.ImageFormat;
import ru.ivk.lab4new.image.NormalizationMode;
import ru.ivk.lab4new.scene.SceneSource;

import java.util.Locale;
import java.util.Scanner;

/**
 * Считывает параметры рендера из консоли для ручного демонстрационного режима.
 */
public final class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput() {
        this.scanner = new Scanner(System.in);
        this.scanner.useLocale(Locale.US);
    }

    public RenderSettings readSettings(RenderSettings defaults) {
        System.out.println("Manual render settings. Press Enter to keep default value.");
        SceneSource sceneSource = readSceneSource(defaults.getSceneSource());
        String modelPath = readModelPath(sceneSource, defaults.getModelPath());

        int width = readInt("Width", defaults.getWidth());
        int height = readInt("Height", defaults.getHeight());
        int samplesPerPixel = readInt("Samples per pixel", defaults.getSamplesPerPixel());
        int maxDepth = readInt("Max depth", defaults.getMaxDepth());
        int russianRouletteStartDepth = readInt("Russian roulette start depth", defaults.getRussianRouletteStartDepth());
        int threadCount = readInt("Thread count", defaults.getThreadCount());
        double gamma = readDouble("Gamma", defaults.getGamma());
        NormalizationMode normalizationMode = readNormalizationMode(defaults.getNormalizationMode());
        double fixedExposure = readDouble("Fixed exposure", defaults.getFixedExposure());
        ImageFormat imageFormat = readImageFormat(defaults.getImageFormat());
        String outputPath = readString("Output path", defaults.getOutputPath());

        return new RenderSettings(
                width,
                height,
                samplesPerPixel,
                maxDepth,
                russianRouletteStartDepth,
                threadCount,
                gamma,
                normalizationMode,
                fixedExposure,
                imageFormat,
                sceneSource,
                outputPath,
                modelPath
        );
    }

    private String readString(String label, String defaultValue) {
        System.out.printf("%s [%s]: ", label, defaultValue);
        String value = scanner.nextLine().trim();

        return value.isEmpty() ? defaultValue : value;
    }

    private int readInt(String label, int defaultValue) {
        while (true) {
            String value = readString(label, Integer.toString(defaultValue));

            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Enter an integer value.");
            }
        }
    }

    private double readDouble(String label, double defaultValue) {
        while (true) {
            String value = readString(label, Double.toString(defaultValue));

            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException exception) {
                System.out.println("Enter a numeric value.");
            }
        }
    }

    private NormalizationMode readNormalizationMode(NormalizationMode defaultValue) {
        while (true) {
            String value = readString("Normalization: NONE, FIXED, MAX", defaultValue.name());

            try {
                return NormalizationMode.valueOf(value.toUpperCase(Locale.US));
            } catch (IllegalArgumentException exception) {
                System.out.println("Enter NONE, FIXED, or MAX.");
            }
        }
    }

    private ImageFormat readImageFormat(ImageFormat defaultValue) {
        while (true) {
            String value = readString("Image format: PNG, PPM, BOTH", defaultValue.name());

            try {
                return ImageFormat.valueOf(value.toUpperCase(Locale.US));
            } catch (IllegalArgumentException exception) {
                System.out.println("Enter PNG, PPM, or BOTH.");
            }
        }
    }

    private SceneSource readSceneSource(SceneSource defaultValue) {
        while (true) {
            String value = readString("Scene source: CODE, RESOURCE, PATH", defaultValue.name());

            try {
                return SceneSource.valueOf(value.toUpperCase(Locale.US));
            } catch (IllegalArgumentException exception) {
                System.out.println("Enter CODE, RESOURCE, or PATH.");
            }
        }
    }

    private String readModelPath(SceneSource sceneSource, String defaultValue) {
        if (sceneSource == SceneSource.RESOURCE) {
            return readString("OBJ resource name", defaultValue);
        }

        if (sceneSource == SceneSource.PATH) {
            return readString("OBJ file path", defaultValue);
        }

        return defaultValue;
    }
}
