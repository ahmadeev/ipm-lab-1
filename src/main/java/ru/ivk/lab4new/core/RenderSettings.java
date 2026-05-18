package ru.ivk.lab4new.core;

import lombok.Getter;
import ru.ivk.lab4new.image.NormalizationMode;

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
    private final int russianRouletteStartDepth;
    private final int threadCount;
    private final double gamma;
    private final NormalizationMode normalizationMode;
    private final double fixedExposure;
    private final String outputPath;
    private final String modelPath;

    public RenderSettings(
            int width,
            int height,
            int samplesPerPixel,
            int maxDepth,
            int russianRouletteStartDepth,
            int threadCount,
            double gamma,
            NormalizationMode normalizationMode,
            double fixedExposure,
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

        if (russianRouletteStartDepth < 0) {
            throw new IllegalArgumentException("russian roulette start depth must be non-negative");
        }

        if (threadCount <= 0) {
            throw new IllegalArgumentException("thread count must be positive");
        }

        if (gamma <= 0.0) {
            throw new IllegalArgumentException("gamma must be positive");
        }

        if (fixedExposure <= 0.0) {
            throw new IllegalArgumentException("fixed exposure must be positive");
        }

        this.width = width;
        this.height = height;
        this.samplesPerPixel = samplesPerPixel;
        this.maxDepth = maxDepth;
        this.russianRouletteStartDepth = russianRouletteStartDepth;
        this.threadCount = threadCount;
        this.gamma = gamma;
        this.normalizationMode = Objects.requireNonNull(normalizationMode, "normalizationMode");
        this.fixedExposure = fixedExposure;
        this.outputPath = Objects.requireNonNull(outputPath, "outputPath");
        this.modelPath = Objects.requireNonNull(modelPath, "modelPath");
    }

    public static RenderSettings demo() {
        return new RenderSettings(
                500,
                500,
                8,
                8,
                4,
                1,
                2.2,
                NormalizationMode.FIXED,
                1.0,
                "helpers/output/lab-4new/demo.ppm",
                "cube.obj"
        );
    }
}
