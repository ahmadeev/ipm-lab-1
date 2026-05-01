package ru.ivk.lab4.geometry;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.material.Material;

public final class HitRecord {
    private final Vec3 point;
    private final Vec3 normal;
    private final double t;
    private final Triangle triangle;

    public HitRecord(Vec3 point, Vec3 normal, double t, Triangle triangle) {
        this.point = Vec3.copyOf(point);
        this.normal = normal.normalize();
        this.t = t;
        this.triangle = triangle;
    }

    public Vec3 getPoint() {
        return Vec3.copyOf(point);
    }

    public Vec3 getNormal() {
        return Vec3.copyOf(normal);
    }

    public double getT() {
        return t;
    }

    public Triangle getTriangle() {
        return triangle;
    }

    public Material getMaterial() {
        return triangle.getMaterial();
    }
}
