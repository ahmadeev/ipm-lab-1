package ru.ivk.lab4new;

import ru.ivk.lab4new.cli.ConsoleInput;
import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.image.ImageBuffer;
import ru.ivk.lab4new.image.ImageWriter;
import ru.ivk.lab4new.render.Renderer;
import ru.ivk.lab4new.scene.DemoSceneFactory;
import ru.ivk.lab4new.scene.ObjSceneFactory;
import ru.ivk.lab4new.scene.RenderJob;
import ru.ivk.lab4new.scene.SceneSource;

import java.io.IOException;
import java.util.Locale;

/**
 * Точка входа для пошаговой реализации новой версии лабораторной 4.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        if (args.length == 0 || "demo".equalsIgnoreCase(args[0])) {
            runDemo();
            return;
        }

        if ("manual".equalsIgnoreCase(args[0])) {
            runManual();
            return;
        }

        printUsage();
    }

    private static void runDemo() throws IOException {
        render(RenderSettings.demo());
    }

    private static void runManual() throws IOException {
        RenderSettings settings = new ConsoleInput().readSettings(RenderSettings.demo());

        render(settings);
    }

    private static void render(RenderSettings settings) throws IOException {
        RenderJob job = settings.getSceneSource() == SceneSource.CODE
                ? DemoSceneFactory.create(settings)
                : ObjSceneFactory.create(settings);

        System.out.printf(
                "Render: %dx%d, spp=%d, maxDepth=%d, rrStart=%d, threads=%d, gamma=%.3f, normalization=%s, exposure=%.3f, format=%s, scene=%s, model=%s, output=%s%n",
                job.getSettings().getWidth(),
                job.getSettings().getHeight(),
                job.getSettings().getSamplesPerPixel(),
                job.getSettings().getMaxDepth(),
                job.getSettings().getRussianRouletteStartDepth(),
                job.getSettings().getThreadCount(),
                job.getSettings().getGamma(),
                job.getSettings().getNormalizationMode(),
                job.getSettings().getFixedExposure(),
                job.getSettings().getImageFormat(),
                job.getSettings().getSceneSource(),
                job.getSettings().getModelPath(),
                job.getSettings().getOutputPath()
        );

        ImageBuffer image = new Renderer().render(job.getScene(), job.getCamera(), job.getSettings());
        ImageWriter.write(image, job.getSettings());
        System.out.printf("Saved: %s%n", job.getSettings().getOutputPath());
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java ru.ivk.lab4new.Main demo");
        System.out.println("  java ru.ivk.lab4new.Main manual");
    }
}
