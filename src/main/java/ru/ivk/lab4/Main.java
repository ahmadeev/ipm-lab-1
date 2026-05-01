package ru.ivk.lab4;

import ru.ivk.lab4.cli.ConsoleInput;
import ru.ivk.lab4.core.RenderSettings;
import ru.ivk.lab4.image.NormalizationMode;
import ru.ivk.lab4.image.PpmWriter;
import ru.ivk.lab4.render.Renderer;
import ru.ivk.lab4.scene.DemoSceneFactory;
import ru.ivk.lab4.scene.ObjSceneFactory;
import ru.ivk.lab4.scene.RenderJob;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

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
        RenderSettings settings = RenderSettings.demo();
        RenderJob job = DemoSceneFactory.create(settings);
        renderAndWrite(job, settings);
    }

    private static void runManual() throws IOException {
        ConsoleInput input = new ConsoleInput(new Scanner(System.in));

        String sceneMode = input.readString("Сцена: demo или obj", "demo").toLowerCase(Locale.ROOT);
        int width = input.readInt("Ширина изображения", 500, 1);
        int height = input.readInt("Высота изображения", 500, 1);
        int samplesPerPixel = input.readInt("Samples per pixel", 32, 1);
        int maxDepth = input.readInt("Максимальная глубина трассировки", 6, 1);
        double gamma = input.readDouble("Gamma", 2.2, 0.1);
        String outputPath = input.readString("Путь выходного .ppm файла", "helpers/output/lab-4/manual.ppm");
        NormalizationMode normalizationMode = input.readNormalizationMode("Режим нормировки яркости", NormalizationMode.MAX);
        double fixedExposure = normalizationMode == NormalizationMode.FIXED
                ? input.readDouble("Фиксированный множитель яркости", 1.0, 0.0001)
                : 1.0;
        int defaultThreadCount = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        int threadCount = input.readInt("Количество потоков", defaultThreadCount, 1);

        RenderSettings settings = new RenderSettings(
                width,
                height,
                samplesPerPixel,
                maxDepth,
                gamma,
                outputPath,
                normalizationMode,
                fixedExposure,
                threadCount
        );

        RenderJob job;

        if ("obj".equals(sceneMode)) {
            String objPath = input.readString("Путь к OBJ-файлу", "scene.obj");
            job = ObjSceneFactory.create(Paths.get(objPath), settings);
        } else {
            job = DemoSceneFactory.create(settings);
        }

        renderAndWrite(job, settings);
    }

    private static void renderAndWrite(RenderJob job, RenderSettings settings) throws IOException {
        System.out.printf(
                "Render: %dx%d, spp=%d, maxDepth=%d, threads=%d%n",
                settings.getWidth(),
                settings.getHeight(),
                settings.getSamplesPerPixel(),
                settings.getMaxDepth(),
                settings.getThreadCount()
        );

        PpmWriter.write(
                new Renderer().render(job.getScene(), job.getCamera(), settings),
                Paths.get(settings.getOutputPath()),
                settings.getNormalizationMode(),
                settings.getFixedExposure(),
                settings.getGamma()
        );

        System.out.printf("Saved: %s%n", settings.getOutputPath());
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java ru.ivk.lab4.Main demo");
        System.out.println("  java ru.ivk.lab4.Main manual");
    }
}
