package ru.ivk.lab5.filter;

import lombok.Getter;

/**
 * Параметры билатерального фильтра для вторичной яркости.
 */
@Getter
public final class BilateralFilterSettings {
    private final int radius;
    private final double sigmaSpace;
    private final double sigmaDepth;
    private final double sigmaColor;
    private final double normalPower;
    private final boolean useColorWeight;

    public BilateralFilterSettings(
            int radius,
            double sigmaSpace,
            double sigmaDepth,
            double sigmaColor,
            double normalPower,
            boolean useColorWeight
    ) {
        if (radius < 0) {
            throw new IllegalArgumentException("filter radius must be non-negative");
        }

        if (sigmaSpace <= 0.0) {
            throw new IllegalArgumentException("space sigma must be positive");
        }

        if (sigmaDepth <= 0.0) {
            throw new IllegalArgumentException("depth sigma must be positive");
        }

        if (sigmaColor <= 0.0) {
            throw new IllegalArgumentException("color sigma must be positive");
        }

        if (normalPower < 0.0) {
            throw new IllegalArgumentException("normal power must be non-negative");
        }

        this.radius = radius;
        this.sigmaSpace = sigmaSpace;
        this.sigmaDepth = sigmaDepth;
        this.sigmaColor = sigmaColor;
        this.normalPower = normalPower;
        this.useColorWeight = useColorWeight;
    }

    public static BilateralFilterSettings demo() {
        return new BilateralFilterSettings(
                3,
                2.0,
                0.35,
                0.5,
                16.0,
                true
        );
    }
}
