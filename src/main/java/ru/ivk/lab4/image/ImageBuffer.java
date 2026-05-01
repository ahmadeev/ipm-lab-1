package ru.ivk.lab4.image;

import ru.ivk.lab4.core.ColorRgb;

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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setPixel(int x, int y, ColorRgb color) {
        pixels[index(x, y)] = color;
    }

    public ColorRgb getPixel(int x, int y) {
        return pixels[index(x, y)];
    }

    public double maxComponent() {
        double max = 0.0;

        for (ColorRgb pixel : pixels) {
            max = Math.max(max, pixel.maxComponent());
        }

        return max;
    }

    private int index(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("pixel is outside image");
        }

        return y * width + x;
    }
}
