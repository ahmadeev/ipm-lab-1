package ru.ivk.lab4.image;

import lombok.Getter;
import ru.ivk.lab4.core.ColorRgb;

/**
 * Буфер изображения, хранящий RGB-значения для каждого пикселя.
 */
@Getter
public final class ImageBuffer {
    private final int width;
    private final int height;
    private final ColorRgb[] pixels;

    public ImageBuffer(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("image size must be positive");
        }

        this.width = width;
        this.height = height;
        this.pixels = new ColorRgb[width * height];

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = ColorRgb.BLACK;
        }
    }

    public void setPixel(int x, int y, ColorRgb color) {
        pixels[indexOf(x, y)] = color;
    }

    public ColorRgb getPixel(int x, int y) {
        return pixels[indexOf(x, y)];
    }

    private int indexOf(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("pixel coordinates are outside image");
        }

        return y * width + x;
    }
}
