package ru.ivk.lab4new.render;

import ru.ivk.lab4new.core.Camera;
import ru.ivk.lab4new.core.ColorRgb;
import ru.ivk.lab4new.core.Ray;
import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.image.ImageBuffer;
import ru.ivk.lab4new.scene.Scene;
import ru.ivk.lab4new.sampling.Sampler;

/**
 * Построитель изображения для текущего учебного этапа.
 */
public final class Renderer {
    private final PathTracer pathTracer = new PathTracer();

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

                    color = color.add(pathTracer.trace(scene, ray));
                }

                image.setPixel(x, y, color.div(settings.getSamplesPerPixel()));
            }
        }

        return image;
    }
}
