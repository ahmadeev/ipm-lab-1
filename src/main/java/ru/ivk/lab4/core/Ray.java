package ru.ivk.lab4.core;

import ru.ivk.common.math.Vec3;

/**
 * Геометрический луч с началом и нормализованным направлением.
 */
public final class Ray {
    private final Vec3 origin;
    private final Vec3 direction;

    public Ray(Vec3 origin, Vec3 direction) {
        this.origin = Vec3.copyOf(origin);
        this.direction = direction.normalize();
    }

    public Vec3 at(double t) {
        return origin.add(direction.mul(t));
    }

    public Vec3 getOrigin() {
        return Vec3.copyOf(origin);
    }

    public Vec3 getDirection() {
        return Vec3.copyOf(direction);
    }
}
