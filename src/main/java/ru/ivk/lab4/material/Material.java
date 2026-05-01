package ru.ivk.lab4.material;

import ru.ivk.lab4.core.ColorRgb;

public final class Material {
    private final ColorRgb diffuse;
    private final ColorRgb specular;
    private final ColorRgb emission;

    public Material(ColorRgb diffuse, ColorRgb specular, ColorRgb emission) {
        requirePhysical(diffuse, specular);

        this.diffuse = diffuse;
        this.specular = specular;
        this.emission = emission;
    }

    public static Material diffuse(ColorRgb diffuse) {
        return new Material(diffuse, ColorRgb.BLACK, ColorRgb.BLACK);
    }

    public static Material mirror(ColorRgb specular) {
        return new Material(ColorRgb.BLACK, specular, ColorRgb.BLACK);
    }

    public static Material mixed(ColorRgb diffuse, ColorRgb specular) {
        return new Material(diffuse, specular, ColorRgb.BLACK);
    }

    public static Material light(ColorRgb emission) {
        return new Material(ColorRgb.BLACK, ColorRgb.BLACK, emission);
    }

    public ColorRgb getDiffuse() {
        return diffuse;
    }

    public ColorRgb getSpecular() {
        return specular;
    }

    public ColorRgb getEmission() {
        return emission;
    }

    public boolean isLight() {
        return !emission.isBlack();
    }

    public double diffuseWeight() {
        return diffuse.average();
    }

    public double specularWeight() {
        return specular.average();
    }

    private static void requirePhysical(ColorRgb diffuse, ColorRgb specular) {
        if (diffuse.r < 0.0 || diffuse.g < 0.0 || diffuse.b < 0.0
                || specular.r < 0.0 || specular.g < 0.0 || specular.b < 0.0) {
            throw new IllegalArgumentException("reflection coefficients must be non-negative");
        }

        if (diffuse.r + specular.r > 1.0
                || diffuse.g + specular.g > 1.0
                || diffuse.b + specular.b > 1.0) {
            throw new IllegalArgumentException("diffuse + specular must not exceed 1 per RGB component");
        }
    }
}
