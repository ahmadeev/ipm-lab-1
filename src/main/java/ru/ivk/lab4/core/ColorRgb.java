package ru.ivk.lab4.core;

import ru.ivk.common.utils.Utils;

public final class ColorRgb {
    public static final ColorRgb BLACK = new ColorRgb(0.0, 0.0, 0.0);
    public static final ColorRgb WHITE = new ColorRgb(1.0, 1.0, 1.0);

    public final double r;
    public final double g;
    public final double b;

    public ColorRgb(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ColorRgb add(ColorRgb color) {
        return new ColorRgb(r + color.r, g + color.g, b + color.b);
    }

    public ColorRgb sub(ColorRgb color) {
        return new ColorRgb(r - color.r, g - color.g, b - color.b);
    }

    public ColorRgb mul(double scalar) {
        return new ColorRgb(r * scalar, g * scalar, b * scalar);
    }

    public ColorRgb mul(ColorRgb color) {
        return new ColorRgb(r * color.r, g * color.g, b * color.b);
    }

    public ColorRgb div(double scalar) {
        if (scalar == 0.0) {
            throw new IllegalArgumentException("scalar must be non-zero");
        }

        return mul(1.0 / scalar);
    }

    public ColorRgb clamp(double min, double max) {
        return new ColorRgb(
                Utils.clamp(r, min, max),
                Utils.clamp(g, min, max),
                Utils.clamp(b, min, max)
        );
    }

    public double maxComponent() {
        return Math.max(r, Math.max(g, b));
    }

    public double average() {
        return (r + g + b) / 3.0;
    }

    public boolean isBlack() {
        return r == 0.0 && g == 0.0 && b == 0.0;
    }
}
