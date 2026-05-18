package ru.ivk.lab4new.image;

import ru.ivk.common.utils.Utils;
import ru.ivk.lab4new.core.ColorRgb;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Записывает буфер изображения в простой текстовый PPM-файл.
 */
public final class PpmWriter {
    private PpmWriter() {
    }

    public static void write(ImageBuffer image, String outputPath) throws IOException {
        Path path = Paths.get(outputPath);
        Path parent = path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("P3").append(System.lineSeparator());
        builder.append(image.getWidth()).append(' ').append(image.getHeight()).append(System.lineSeparator());
        builder.append(255).append(System.lineSeparator());

        for (int y = image.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < image.getWidth(); x++) {
                ColorRgb color = image.getPixel(x, y);

                builder.append(toByte(color.r)).append(' ')
                        .append(toByte(color.g)).append(' ')
                        .append(toByte(color.b)).append(' ');
            }

            builder.append(System.lineSeparator());
        }

        Files.write(path, builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static int toByte(double value) {
        return (int) Math.round(Utils.clamp(value, 0.0, 1.0) * 255.0);
    }
}
