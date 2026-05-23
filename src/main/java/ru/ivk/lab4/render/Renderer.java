package ru.ivk.lab4.render;

import ru.ivk.lab4.core.Camera;
import ru.ivk.lab4.core.ColorRgb;
import ru.ivk.lab4.core.Ray;
import ru.ivk.lab4.core.RenderSettings;
import ru.ivk.lab4.image.ImageBuffer;
import ru.ivk.lab4.image.PixelRenderData;
import ru.ivk.lab4.image.RenderDataBuffer;
import ru.ivk.lab4.scene.Scene;
import ru.ivk.lab4.sampling.Sampler;

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
        return renderData(scene, camera, settings).toImageBuffer();
    }

    public RenderDataBuffer renderData(Scene scene, Camera camera, RenderSettings settings) {
        RenderDataBuffer data = new RenderDataBuffer(settings.getWidth(), settings.getHeight());
        ExecutorService executor = Executors.newFixedThreadPool(settings.getThreadCount());
        List<Future<?>> futures = new ArrayList<>();

        for (int y = 0; y < settings.getHeight(); y++) {
            int row = y;
            futures.add(executor.submit(() -> renderRow(scene, camera, settings, data, row)));
        }

        try {
            waitForRows(futures);
        } finally {
            executor.shutdown();
        }

        return data;
    }

    private void renderRow(Scene scene, Camera camera, RenderSettings settings, RenderDataBuffer data, int y) {
        Sampler sampler = new Sampler(1234567L + y * 1000003L);

        for (int x = 0; x < settings.getWidth(); x++) {
            List<PathTraceResult> samples = new ArrayList<>();
            ColorRgb direct = ColorRgb.BLACK;
            ColorRgb indirect = ColorRgb.BLACK;

            for (int sample = 0; sample < settings.getSamplesPerPixel(); sample++) {
                double u = (x + sampler.nextDouble()) / settings.getWidth();
                double v = (y + sampler.nextDouble()) / settings.getHeight();
                Ray ray = camera.ray(u, v);
                PathTraceResult result = pathTracer.tracePrimary(
                        scene,
                        ray,
                        sampler,
                        settings.getMaxDepth(),
                        settings.getRussianRouletteStartDepth()
                );

                samples.add(result);
                direct = direct.add(result.getDirect());
                indirect = indirect.add(result.getIndirect());
            }

            PathTraceResult dominant = dominantSample(samples);
            data.setPixel(x, y, new PixelRenderData(
                    direct.div(settings.getSamplesPerPixel()),
                    indirect.div(settings.getSamplesPerPixel()),
                    direct.add(indirect).div(settings.getSamplesPerPixel()),
                    dominant.getDepth(),
                    dominant.getNormal(),
                    dominant.getObjectId(),
                    dominant.isHit()
            ));
        }
    }

    private PathTraceResult dominantSample(List<PathTraceResult> samples) {
        PathTraceResult dominant = samples.get(0);
        int dominantCount = 0;

        for (PathTraceResult candidate : samples) {
            // todo: за O(n) алгоритмом Бойера-Мура
            int count = countObjectSamples(samples, candidate.getObjectId());

            if (count > dominantCount) {
                dominant = candidate;
                dominantCount = count;
            }
        }

        return dominant;
    }

    private int countObjectSamples(List<PathTraceResult> samples, int objectId) {
        int count = 0;

        for (PathTraceResult sample : samples) {
            if (sample.getObjectId() == objectId) {
                count++;
            }
        }

        return count;
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
