package ru.ivk.lab4new;

import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.scene.RenderJob;

import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        if (args.length == 0 || "demo".equalsIgnoreCase(args[0])) {
            runDemo();
            return;
        }

        printUsage();
    }

    private static void runDemo() {
        RenderSettings settings = RenderSettings.demo();
        RenderJob job = new RenderJob(settings);

        System.out.printf(
                "Render: %dx%d, spp=%d, maxDepth=%d, output=%s%n",
                job.getSettings().getWidth(),
                job.getSettings().getHeight(),
                job.getSettings().getSamplesPerPixel(),
                job.getSettings().getMaxDepth(),
                job.getSettings().getOutputPath()
        );
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java ru.ivk.lab4new.Main demo");
    }
}
