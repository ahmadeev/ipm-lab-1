package ru.ivk.lab5.image;

import ru.ivk.lab4.core.ColorRgb;
import ru.ivk.lab4.image.ImageBuffer;
import ru.ivk.lab4.image.PixelRenderData;
import ru.ivk.lab4.image.RenderDataBuffer;

import java.util.Objects;

/**
 * Собирает изображения из отдельных компонент расширенного буфера рендера.
 */
public final class RenderDataImages {
    private RenderDataImages() {
    }

    public static ImageBuffer total(RenderDataBuffer data) {
        return map(data, Component.TOTAL);
    }

    public static ImageBuffer direct(RenderDataBuffer data) {
        return map(data, Component.DIRECT);
    }

    public static ImageBuffer indirect(RenderDataBuffer data) {
        return map(data, Component.INDIRECT);
    }

    private static ImageBuffer map(RenderDataBuffer data, Component component) {
        Objects.requireNonNull(data, "data");
        ImageBuffer image = new ImageBuffer(data.getWidth(), data.getHeight());

        for (int y = 0; y < data.getHeight(); y++) {
            for (int x = 0; x < data.getWidth(); x++) {
                image.setPixel(x, y, colorOf(data.getPixel(x, y), component));
            }
        }

        return image;
    }

    private static ColorRgb colorOf(PixelRenderData pixel, Component component) {
        if (component == Component.DIRECT) {
            return pixel.getDirect();
        }

        if (component == Component.INDIRECT) {
            return pixel.getIndirect();
        }

        return pixel.getTotal();
    }

    private enum Component {
        TOTAL,
        DIRECT,
        INDIRECT
    }
}
