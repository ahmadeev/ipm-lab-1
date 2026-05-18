package ru.ivk.lab4new.core;

public final class ColorRgb {
    public static final ColorRgb BLACK = new ColorRgb(0.0, 0.0, 0.0);

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

    public ColorRgb mul(double value) {
        return new ColorRgb(r * value, g * value, b * value);
    }

    public ColorRgb div(double value) {
        if (value == 0.0) {
            throw new IllegalArgumentException("division by zero");
        }

        return new ColorRgb(r / value, g / value, b / value);
    }

    public boolean isBlack() {
        return r == 0.0 && g == 0.0 && b == 0.0;
    }
}
