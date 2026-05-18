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
        String outputPath = settings.getOutputPath().toLowerCase();

        if (outputPath.endsWith(".png")) {
            PngWriter.write(image, settings);
            return;
        }

        PpmWriter.write(image, settings);
    }
}
