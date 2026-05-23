package ru.ivk.lab5;

import ru.ivk.lab4.core.RenderSettings;
import ru.ivk.lab4.image.ImageBuffer;
import ru.ivk.lab4.image.ImageWriter;
import ru.ivk.lab4.image.RenderDataBuffer;
import ru.ivk.lab4.render.Renderer;
import ru.ivk.lab4.scene.DemoSceneFactory;
import ru.ivk.lab4.scene.RenderJob;
import ru.ivk.lab5.filter.BilateralFilter;
import ru.ivk.lab5.filter.BilateralFilterSettings;
import ru.ivk.lab5.image.RenderDataImages;

import java.io.IOException;
import java.util.Locale;

/**
 * Точка входа для лабораторной 5.
 */
public class Main {
    private static final String OUTPUT_DIR = "output/lab-5/";

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        RenderSettings settings = settingsWithOutputPath(RenderSettings.demo(), OUTPUT_DIR + "noisy");
        RenderJob job = DemoSceneFactory.create(settings);
        Renderer renderer = new Renderer();

        System.out.printf(
                "Lab 5 render: %dx%d, spp=%d, maxDepth=%d, rrStart=%d, threads=%d%n",
                settings.getWidth(),
                settings.getHeight(),
                settings.getSamplesPerPixel(),
                settings.getMaxDepth(),
                settings.getRussianRouletteStartDepth(),
                settings.getThreadCount()
        );

        long renderStart = System.nanoTime();
        RenderDataBuffer noisy = renderer.renderData(job.getScene(), job.getCamera(), job.getSettings());
        long renderElapsedNanos = System.nanoTime() - renderStart;

        BilateralFilterSettings filterSettings = BilateralFilterSettings.demo();
        BilateralFilter filter = new BilateralFilter(filterSettings);

        long filterStart = System.nanoTime();
        RenderDataBuffer filtered = filter.apply(noisy);
        long filterElapsedNanos = System.nanoTime() - filterStart;

        write(RenderDataImages.total(noisy), settings, "noisy");
        write(RenderDataImages.total(filtered), settings, "filtered");
        write(RenderDataImages.direct(noisy), settings, "direct");
        write(RenderDataImages.indirect(noisy), settings, "indirect");
        write(RenderDataImages.indirect(filtered), settings, "filtered-indirect");

        System.out.printf("Render time: %.3f s%n", renderElapsedNanos / 1_000_000_000.0);
        System.out.printf("Filter time: %.3f s%n", filterElapsedNanos / 1_000_000_000.0);
        System.out.printf("Saved images to: %s%n", OUTPUT_DIR);
    }

    private static void write(ImageBuffer image, RenderSettings baseSettings, String name) throws IOException {
        ImageWriter.write(image, settingsWithOutputPath(baseSettings, OUTPUT_DIR + name));
    }

    private static RenderSettings settingsWithOutputPath(RenderSettings source, String outputPath) {
        return new RenderSettings(
                source.getWidth(),
                source.getHeight(),
                source.getSamplesPerPixel(),
                source.getMaxDepth(),
                source.getRussianRouletteStartDepth(),
                source.getThreadCount(),
                source.getGamma(),
                source.getNormalizationMode(),
                source.getFixedExposure(),
                source.getImageFormat(),
                source.getSceneSource(),
                outputPath,
                source.getModelPath()
        );
    }
}
