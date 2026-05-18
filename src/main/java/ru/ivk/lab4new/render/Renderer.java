package ru.ivk.lab4new.render;

import ru.ivk.lab4new.core.ColorRgb;
import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.image.ImageBuffer;

/**
 * Построитель изображения для текущего учебного этапа.
 */
public final class Renderer {
    public ImageBuffer render(RenderSettings settings) {
        ImageBuffer image = new ImageBuffer(settings.getWidth(), settings.getHeight());

        for (int y = 0; y < settings.getHeight(); y++) {
            for (int x = 0; x < settings.getWidth(); x++) {
                double u = settings.getWidth() == 1 ? 0.0 : (double) x / (settings.getWidth() - 1);
                double v = settings.getHeight() == 1 ? 0.0 : (double) y / (settings.getHeight() - 1);

                image.setPixel(x, y, new ColorRgb(u, v, 0.25));
            }
        }

        return image;
    }
}
