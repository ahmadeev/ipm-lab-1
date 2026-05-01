package ru.ivk.lab4.core;

import ru.ivk.lab4.image.NormalizationMode;

public final class RenderSettings {
    private final int width;
    private final int height;
    private final int samplesPerPixel;
    private final int maxDepth;
    private final double gamma;
    private final String outputPath;
    private final NormalizationMode normalizationMode;
    private final double fixedExposure;
    private final int threadCount;

    public RenderSettings(
            int width,
            int height,
            int samplesPerPixel,
            int maxDepth,
            double gamma,
            String outputPath,
            NormalizationMode normalizationMode,
            double fixedExposure,
            int threadCount
    ) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("image size must be positive");
        }
        if (samplesPerPixel <= 0) {
            throw new IllegalArgumentException("samplesPerPixel must be positive");
        }
        if (maxDepth <= 0) {
            throw new IllegalArgumentException("maxDepth must be positive");
        }
        if (gamma <= 0.0) {
            throw new IllegalArgumentException("gamma must be positive");
        }
        if (fixedExposure <= 0.0) {
            throw new IllegalArgumentException("fixedExposure must be positive");
        }
        if (threadCount <= 0) {
            throw new IllegalArgumentException("threadCount must be positive");
        }

        this.width = width;
        this.height = height;
        this.samplesPerPixel = samplesPerPixel;
        this.maxDepth = maxDepth;
        this.gamma = gamma;
        this.outputPath = outputPath;
        this.normalizationMode = normalizationMode;
        this.fixedExposure = fixedExposure;
        this.threadCount = threadCount;
    }

    public static RenderSettings demo() {
        return new RenderSettings(
                500,
                500,
                24,
                6,
                2.2,
                "helpers/output/lab-4/demo.ppm",
                NormalizationMode.MAX,
                1.0,
                Math.max(1, Runtime.getRuntime().availableProcessors() - 1)
        );
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSamplesPerPixel() {
        return samplesPerPixel;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public double getGamma() {
        return gamma;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public NormalizationMode getNormalizationMode() {
        return normalizationMode;
    }

    public double getFixedExposure() {
        return fixedExposure;
    }

    public int getThreadCount() {
        return threadCount;
    }
}
