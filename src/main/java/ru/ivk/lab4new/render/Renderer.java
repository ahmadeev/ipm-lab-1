package ru.ivk.lab4new.render;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4new.core.Camera;
import ru.ivk.lab4new.core.ColorRgb;
import ru.ivk.lab4new.core.Ray;
import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.image.ImageBuffer;

/**
 * Построитель изображения для текущего учебного этапа.
 */
public final class Renderer {
    public ImageBuffer render(Camera camera, RenderSettings settings) {
        ImageBuffer image = new ImageBuffer(settings.getWidth(), settings.getHeight());

        for (int y = 0; y < settings.getHeight(); y++) {
            for (int x = 0; x < settings.getWidth(); x++) {
                double u = settings.getWidth() == 1 ? 0.0 : (double) x / (settings.getWidth() - 1);
                double v = settings.getHeight() == 1 ? 0.0 : (double) y / (settings.getHeight() - 1);
                Ray ray = camera.ray(u, v);

                image.setPixel(x, y, directionColor(ray.getDirection()));
            }
        }

        return image;
    }

    private ColorRgb directionColor(Vec3 direction) {
        Vec3 unit = direction.normalize();

        return new ColorRgb(
                0.5 * (unit.x + 1.0),
                0.5 * (unit.y + 1.0),
                0.5 * (unit.z + 1.0)
        );
    }
}
