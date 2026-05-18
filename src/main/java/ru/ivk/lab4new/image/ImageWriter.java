package ru.ivk.lab4new.image;

import ru.ivk.lab4new.core.RenderSettings;

import java.io.IOException;

/**
 * Выбирает формат записи изображения по расширению выходного файла.
 */
public final class ImageWriter {
    private ImageWriter() {
    }

    public static void write(ImageBuffer image, RenderSettings settings) throws IOException {
        if (settings.getImageFormat() == ImageFormat.PNG) {
            PngWriter.write(image, settings, pathWithExtension(settings.getOutputPath(), ".png"));
            return;
        }

        if (settings.getImageFormat() == ImageFormat.PPM) {
            PpmWriter.write(image, settings, pathWithExtension(settings.getOutputPath(), ".ppm"));
            return;
        }

        PngWriter.write(image, settings, pathWithExtension(settings.getOutputPath(), ".png"));
        PpmWriter.write(image, settings, pathWithExtension(settings.getOutputPath(), ".ppm"));
    }

    private static String pathWithExtension(String outputPath, String extension) {
        String lowerPath = outputPath.toLowerCase();

        if (lowerPath.endsWith(".png") || lowerPath.endsWith(".ppm")) {
            return outputPath.substring(0, outputPath.length() - 4) + extension;
        }

        return outputPath + extension;
    }
}
