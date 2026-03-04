package ru.ivk.utils;

import ru.ivk.math.Vec3;

public class Light {
    public Vec3 position;
    public Vec3 direction;
    public Vec3 intensity; // RGB

    public Light(Vec3 position, Vec3 direction, Vec3 intensity) {
        this.position = position;
        this.direction = direction.normalize();
        this.intensity = intensity;
    }

    @Override
    public String toString() {
        return "Light{" +
                "position=" + position +
                ", direction=" + direction +
                ", intensity=" + intensity +
                '}';
    }
}
