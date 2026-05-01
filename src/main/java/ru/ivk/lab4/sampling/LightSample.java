package ru.ivk.lab4.sampling;

import lombok.Getter;
import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.geometry.Triangle;

public final class LightSample {
    @Getter
    private final Triangle light;
    private final Vec3 point;
    @Getter
    private final double lightPickProbability;

    public LightSample(Triangle light, Vec3 point, double lightPickProbability) {
        this.light = light;
        this.point = Vec3.copyOf(point);
        this.lightPickProbability = lightPickProbability;
    }

    public Vec3 getPoint() {
        return Vec3.copyOf(point);
    }
}
