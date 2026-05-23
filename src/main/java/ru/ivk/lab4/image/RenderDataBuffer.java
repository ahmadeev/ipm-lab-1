package ru.ivk.lab4.image;

import lombok.Getter;

import java.util.Objects;

/**
 * Буфер данных рендера, хранящий физические и геометрические значения пикселей.
 */
@Getter
public final class RenderDataBuffer {
    private final int width;
    private final int height;
    private final PixelRenderData[] pixels;

    public RenderDataBuffer(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("render data size must be positive");
        }

        this.width = width;
        this.height = height;
        this.pixels = new PixelRenderData[width * height];

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = PixelRenderData.BACKGROUND;
        }
    }

    public void setPixel(int x, int y, PixelRenderData data) {
        pixels[indexOf(x, y)] = Objects.requireNonNull(data, "data");
    }

    public PixelRenderData getPixel(int x, int y) {
        return pixels[indexOf(x, y)];
    }

    public ImageBuffer toImageBuffer() {
        ImageBuffer image = new ImageBuffer(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setPixel(x, y, getPixel(x, y).getTotal());
            }
        }

        return image;
    }

    private int indexOf(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("pixel coordinates are outside render data");
        }

        return y * width + x;
    }
}
