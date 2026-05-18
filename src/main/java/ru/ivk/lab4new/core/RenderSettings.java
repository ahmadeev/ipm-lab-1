package ru.ivk.lab4new.core;

import lombok.Getter;

import java.util.Objects;

/**
 * Набор базовых параметров, управляющих построением изображения.
 */
@Getter
public final class RenderSettings {
    private final int width;
    private final int height;
    private final int samplesPerPixel;
    private final int maxDepth;
    private final double gamma;
    private final String outputPath;
    private final String modelPath;

    public RenderSettings(
            int width,
            int height,
            int samplesPerPixel,
            int maxDepth,
            double gamma,
            String outputPath,
            String modelPath
    ) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("image size must be positive");
        }

        if (samplesPerPixel <= 0) {
            throw new IllegalArgumentException("samples per pixel must be positive");
        }

        if (maxDepth <= 0) {
            throw new IllegalArgumentException("max depth must be positive");
        }

        if (gamma <= 0.0) {
            throw new IllegalArgumentException("gamma must be positive");
        }

        this.width = width;
        this.height = height;
        this.samplesPerPixel = samplesPerPixel;
        this.maxDepth = maxDepth;
        this.gamma = gamma;
        this.outputPath = Objects.requireNonNull(outputPath, "outputPath");
        this.modelPath = Objects.requireNonNull(modelPath, "modelPath");
    }

    public static RenderSettings demo() {
        return new RenderSettings(
                500,
                500,
                2,
                2,
                2.2,
                "helpers/output/lab-4new/demo.ppm",
                "cube.obj"
        );
    }
}
