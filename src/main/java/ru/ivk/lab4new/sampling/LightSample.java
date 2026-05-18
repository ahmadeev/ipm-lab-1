package ru.ivk.lab4new.sampling;

import lombok.Getter;
import ru.ivk.common.math.Vec3;
import ru.ivk.lab4new.core.ColorRgb;
import ru.ivk.lab4new.geometry.Triangle;

import java.util.Objects;

/**
 * Результат случайного выбора точки на протяженном треугольном источнике света.
 */
@Getter
public final class LightSample {
    private final Triangle light;
    private final ColorRgb emission;
    private final double selectionPdf;
    private final double areaPdf;
    private final double pdf;
    private final Vec3 point;
    private final Vec3 normal;

    public LightSample(Triangle light, Vec3 point, Vec3 normal, ColorRgb emission, double selectionPdf, double areaPdf) {
        this.light = Objects.requireNonNull(light, "light");
        this.point = Vec3.copyOf(point);
        this.normal = Vec3.copyOf(normal);
        this.emission = Objects.requireNonNull(emission, "emission");
        this.selectionPdf = selectionPdf;
        this.areaPdf = areaPdf;
        this.pdf = selectionPdf * areaPdf;
    }

    public Vec3 getPoint() {
        return Vec3.copyOf(point);
    }

    public Vec3 getNormal() {
        return Vec3.copyOf(normal);
    }
}
