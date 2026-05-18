package ru.ivk.lab4new.image;

import ru.ivk.lab4new.core.ColorRgb;
import ru.ivk.lab4new.core.RenderSettings;

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
        write(image, new RenderSettings(
                image.getWidth(),
                image.getHeight(),
                1,
                1,
                0,
                1,
                1.0,
                NormalizationMode.NONE,
                1.0,
                outputPath,
                "cube.obj"
        ));
    }

    public static void write(ImageBuffer image, RenderSettings settings) throws IOException {
        String outputPath = settings.getOutputPath();
        Path path = Paths.get(outputPath);
        Path parent = path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("P3").append(System.lineSeparator());
        builder.append(image.getWidth()).append(' ').append(image.getHeight()).append(System.lineSeparator());
        builder.append(255).append(System.lineSeparator());

        ImageColorMapper mapper = new ImageColorMapper(image, settings);

        for (int y = image.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < image.getWidth(); x++) {
                ColorRgb color = image.getPixel(x, y);

                builder.append(mapper.red(color)).append(' ')
                        .append(mapper.green(color)).append(' ')
                        .append(mapper.blue(color)).append(' ');
            }

            builder.append(System.lineSeparator());
        }

        Files.write(path, builder.toString().getBytes(StandardCharsets.UTF_8));
    }
}
