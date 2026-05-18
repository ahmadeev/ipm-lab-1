package ru.ivk.lab4new.render;

import ru.ivk.lab4new.core.Camera;
import ru.ivk.lab4new.core.ColorRgb;
import ru.ivk.lab4new.core.Ray;
import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.image.ImageBuffer;
import ru.ivk.lab4new.scene.Scene;
import ru.ivk.lab4new.sampling.Sampler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Построитель изображения для текущего учебного этапа.
 */
public final class Renderer {
    private final PathTracer pathTracer = new PathTracer();

    public ImageBuffer render(Scene scene, Camera camera, RenderSettings settings) {
        ImageBuffer image = new ImageBuffer(settings.getWidth(), settings.getHeight());
        ExecutorService executor = Executors.newFixedThreadPool(settings.getThreadCount());
        List<Future<?>> futures = new ArrayList<>();

        for (int y = 0; y < settings.getHeight(); y++) {
            int row = y;
            futures.add(executor.submit(() -> renderRow(scene, camera, settings, image, row)));
        }

        try {
            waitForRows(futures);
        } finally {
            executor.shutdown();
        }

        return image;
    }

    private void renderRow(Scene scene, Camera camera, RenderSettings settings, ImageBuffer image, int y) {
        Sampler sampler = new Sampler(1234567L + y * 1000003L);

        for (int x = 0; x < settings.getWidth(); x++) {
            ColorRgb color = ColorRgb.BLACK;

            for (int sample = 0; sample < settings.getSamplesPerPixel(); sample++) {
                double u = (x + sampler.nextDouble()) / settings.getWidth();
                double v = (y + sampler.nextDouble()) / settings.getHeight();
                Ray ray = camera.ray(u, v);

                color = color.add(pathTracer.trace(
                        scene,
                        ray,
                        sampler,
                        settings.getMaxDepth(),
                        settings.getRussianRouletteStartDepth()
                ));
            }

            image.setPixel(x, y, color.div(settings.getSamplesPerPixel()));
        }
    }

    private void waitForRows(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("render was interrupted", exception);
            } catch (ExecutionException exception) {
                throw new IllegalStateException("render row failed", exception);
            }
        }
    }
}
