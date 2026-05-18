package ru.ivk.lab4.image;

import ru.ivk.lab4.core.ColorRgb;
import ru.ivk.lab4.core.RenderSettings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Записывает буфер изображения в PNG-файл через стандартный ImageIO.
 */
public final class PngWriter {
    private PngWriter() {
    }

    public static void write(ImageBuffer image, RenderSettings settings) throws IOException {
        write(image, settings, settings.getOutputPath());
    }

    public static void write(ImageBuffer image, RenderSettings settings, String outputPath) throws IOException {
        Path path = Paths.get(outputPath);
        Path parent = path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        ImageColorMapper mapper = new ImageColorMapper(image, settings);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                ColorRgb color = image.getPixel(x, image.getHeight() - 1 - y);
                int rgb = (mapper.red(color) << 16) | (mapper.green(color) << 8) | mapper.blue(color);

                output.setRGB(x, y, rgb);
            }
        }

        ImageIO.write(output, "png", path.toFile());
    }
}
