package ru.ivk.lab4.image;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PpmWriter {
    private PpmWriter() {
    }

    public static void write(
            ImageBuffer image,
            Path outputPath,
            NormalizationMode normalizationMode,
            double fixedExposure,
            double gamma
    ) throws IOException {
        Path parent = outputPath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        double exposure = ImageColorMapper.resolveExposure(image, normalizationMode, fixedExposure);
        StringBuilder builder = new StringBuilder();

        builder.append("P3\n");
        builder.append(image.getWidth()).append(' ').append(image.getHeight()).append('\n');
        builder.append("255\n");

        for (int y = image.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < image.getWidth(); x++) {
                int r = ImageColorMapper.toByte(image.getPixel(x, y), exposure, gamma, 0);
                int g = ImageColorMapper.toByte(image.getPixel(x, y), exposure, gamma, 1);
                int b = ImageColorMapper.toByte(image.getPixel(x, y), exposure, gamma, 2);

                builder.append(r).append(' ').append(g).append(' ').append(b).append('\n');
            }
        }

        Files.write(outputPath, builder.toString().getBytes(StandardCharsets.UTF_8));
    }
}
