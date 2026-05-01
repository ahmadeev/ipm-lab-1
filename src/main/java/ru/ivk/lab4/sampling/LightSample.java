package ru.ivk.lab4.sampling;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.geometry.Triangle;

public final class LightSample {
    private final Triangle light;
    private final Vec3 point;
    private final double lightPickProbability;

    public LightSample(Triangle light, Vec3 point, double lightPickProbability) {
        this.light = light;
        this.point = Vec3.copyOf(point);
        this.lightPickProbability = lightPickProbability;
    }

    public Triangle getLight() {
        return light;
    }

    public Vec3 getPoint() {
        return Vec3.copyOf(point);
    }

    public double getLightPickProbability() {
        return lightPickProbability;
    }
}
