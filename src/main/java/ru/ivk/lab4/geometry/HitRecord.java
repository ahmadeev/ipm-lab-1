package ru.ivk.lab4.geometry;

import lombok.Getter;
import ru.ivk.common.math.Vec3;

/**
 * Результат ближайшего попадания луча в геометрию сцены.
 */
public final class HitRecord {
    private final Vec3 point;
    private final Vec3 normal;
    @Getter
    private final double t;
    @Getter
    private final Triangle triangle;

    public HitRecord(Vec3 point, Vec3 normal, double t, Triangle triangle) {
        this.point = Vec3.copyOf(point);
        this.normal = Vec3.copyOf(normal);
        this.t = t;
        this.triangle = triangle;
    }

    public Vec3 getPoint() {
        return Vec3.copyOf(point);
    }

    public Vec3 getNormal() {
        return Vec3.copyOf(normal);
    }
}
