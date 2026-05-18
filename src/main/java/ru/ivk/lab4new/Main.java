package ru.ivk.lab4new;

import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.image.ImageBuffer;
import ru.ivk.lab4new.image.PpmWriter;
import ru.ivk.lab4new.render.Renderer;
import ru.ivk.lab4new.scene.DemoSceneFactory;
import ru.ivk.lab4new.scene.RenderJob;

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

        printUsage();
    }

    private static void runDemo() throws IOException {
        RenderSettings settings = RenderSettings.demo();
        RenderJob job = DemoSceneFactory.create(settings);

        System.out.printf(
                "Render: %dx%d, spp=%d, maxDepth=%d, output=%s%n",
                job.getSettings().getWidth(),
                job.getSettings().getHeight(),
                job.getSettings().getSamplesPerPixel(),
                job.getSettings().getMaxDepth(),
                job.getSettings().getOutputPath()
        );

        ImageBuffer image = new Renderer().render(job.getCamera(), job.getSettings());
        PpmWriter.write(image, job.getSettings().getOutputPath());
        System.out.printf("Saved: %s%n", job.getSettings().getOutputPath());
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java ru.ivk.lab4new.Main demo");
    }
}
