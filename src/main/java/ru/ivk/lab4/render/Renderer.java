package ru.ivk.lab4.render;

import ru.ivk.lab4.core.Camera;
import ru.ivk.lab4.core.ColorRgb;
import ru.ivk.lab4.core.Ray;
import ru.ivk.lab4.core.RenderSettings;
import ru.ivk.lab4.image.ImageBuffer;
import ru.ivk.lab4.sampling.LightSampler;
import ru.ivk.lab4.sampling.Sampler;
import ru.ivk.lab4.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class Renderer {
    private final PathTracer pathTracer = new PathTracer();

    public ImageBuffer render(Scene scene, Camera camera, RenderSettings settings) {
        ImageBuffer image = new ImageBuffer(settings.getWidth(), settings.getHeight());
        LightSampler lightSampler = new LightSampler(scene.getLights());
        ExecutorService executor = Executors.newFixedThreadPool(settings.getThreadCount());
        List<Future<?>> futures = new ArrayList<>();

        for (int y = 0; y < settings.getHeight(); y++) {
            final int row = y;
            futures.add(executor.submit(() -> renderRow(scene, lightSampler, camera, settings, image, row)));
        }

        executor.shutdown();

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                throw new IllegalStateException("rendering failed", e);
            }
        }

        return image;
    }

    private void renderRow(
            Scene scene,
            LightSampler lightSampler,
            Camera camera,
            RenderSettings settings,
            ImageBuffer image,
            int y
    ) {
        Sampler sampler = new Sampler(1234567L + y * 7919L);

        for (int x = 0; x < settings.getWidth(); x++) {
            ColorRgb color = ColorRgb.BLACK;

            for (int sample = 0; sample < settings.getSamplesPerPixel(); sample++) {
                double u = (x + sampler.nextDouble()) / settings.getWidth();
                double v = (y + sampler.nextDouble()) / settings.getHeight();
                Ray ray = camera.ray(u, v);

                color = color.add(pathTracer.trace(scene, lightSampler, ray, settings.getMaxDepth(), sampler));
            }

            image.setPixel(x, y, color.div(settings.getSamplesPerPixel()));
        }
    }
}
