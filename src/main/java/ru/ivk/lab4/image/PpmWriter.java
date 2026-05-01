package ru.ivk.lab4.image;

import ru.ivk.lab4.core.ColorRgb;

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

        double exposure = resolveExposure(image, normalizationMode, fixedExposure);
        StringBuilder builder = new StringBuilder();

        builder.append("P3\n");
        builder.append(image.getWidth()).append(' ').append(image.getHeight()).append('\n');
        builder.append("255\n");

        for (int y = image.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < image.getWidth(); x++) {
                ColorRgb color = image.getPixel(x, y).mul(exposure).clamp(0.0, 1.0);

                int r = toByte(color.r, gamma);
                int g = toByte(color.g, gamma);
                int b = toByte(color.b, gamma);

                builder.append(r).append(' ').append(g).append(' ').append(b).append('\n');
            }
        }

        Files.write(outputPath, builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static double resolveExposure(ImageBuffer image, NormalizationMode normalizationMode, double fixedExposure) {
        if (normalizationMode == NormalizationMode.MAX) {
            double max = image.maxComponent();
            return max > 0.0 ? 1.0 / max : 1.0;
        }

        if (normalizationMode == NormalizationMode.FIXED) {
            return fixedExposure;
        }

        return 1.0;
    }

    private static int toByte(double value, double gamma) {
        double corrected = Math.pow(value, 1.0 / gamma);
        return (int) Math.round(corrected * 255.0);
    }
}
