package ru.ivk.lab4new.render;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4new.core.Camera;
import ru.ivk.lab4new.core.ColorRgb;
import ru.ivk.lab4new.core.Ray;
import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.geometry.HitRecord;
import ru.ivk.lab4new.image.ImageBuffer;
import ru.ivk.lab4new.scene.Scene;
import ru.ivk.lab4new.sampling.Sampler;

import java.util.Optional;

/**
 * Построитель изображения для текущего учебного этапа.
 */
public final class Renderer {
    public ImageBuffer render(Scene scene, Camera camera, RenderSettings settings) {
        ImageBuffer image = new ImageBuffer(settings.getWidth(), settings.getHeight());
        Sampler sampler = new Sampler(1234567L);

        for (int y = 0; y < settings.getHeight(); y++) {
            for (int x = 0; x < settings.getWidth(); x++) {
                ColorRgb color = ColorRgb.BLACK;

                for (int sample = 0; sample < settings.getSamplesPerPixel(); sample++) {
                    double u = (x + sampler.nextDouble()) / settings.getWidth();
                    double v = (y + sampler.nextDouble()) / settings.getHeight();
                    Ray ray = camera.ray(u, v);

                    color = color.add(rayColor(scene, ray));
                }

                image.setPixel(x, y, color.div(settings.getSamplesPerPixel()));
            }
        }

        return image;
    }

    private ColorRgb rayColor(Scene scene, Ray ray) {
        Optional<HitRecord> hit = scene.intersect(ray, 1e-4, Double.POSITIVE_INFINITY);

        if (hit.isPresent()) {
            return hit.get().getTriangle().getMaterial().getDiffuse();
        }

        return directionColor(ray.getDirection());
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
