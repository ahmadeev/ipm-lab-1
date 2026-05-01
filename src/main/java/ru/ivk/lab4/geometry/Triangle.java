package ru.ivk.lab4.geometry;

import lombok.Getter;
import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.core.Ray;
import ru.ivk.lab4.material.Material;

import java.util.Optional;

public final class Triangle {
    private static final double EPSILON = 1e-8;

    private final Vec3 v0;
    private final Vec3 v1;
    private final Vec3 v2;
    private final Vec3 normal;
    @Getter
    private final double area;
    @Getter
    private final Material material;

    public Triangle(Vec3 v0, Vec3 v1, Vec3 v2, Material material) {
        this.v0 = Vec3.copyOf(v0);
        this.v1 = Vec3.copyOf(v1);
        this.v2 = Vec3.copyOf(v2);
        this.material = material;

        Vec3 cross = v1.sub(v0).cross(v2.sub(v0));
        double crossLength = cross.length();

        if (crossLength <= EPSILON) {
            throw new IllegalArgumentException("triangle must have non-zero area");
        }

        this.normal = cross.mul(1.0 / crossLength);
        this.area = 0.5 * crossLength;
    }

    public Optional<HitRecord> intersect(Ray ray, double tMin, double tMax) {
        Vec3 origin = ray.getOrigin();
        Vec3 direction = ray.getDirection();
        Vec3 edge1 = v1.sub(v0);
        Vec3 edge2 = v2.sub(v0);
        Vec3 pVector = direction.cross(edge2);
        double determinant = edge1.dot(pVector);

        if (Math.abs(determinant) < EPSILON) {
            return Optional.empty();
        }

        double invDeterminant = 1.0 / determinant;
        Vec3 tVector = origin.sub(v0);
        double u = tVector.dot(pVector) * invDeterminant;

        if (u < 0.0 || u > 1.0) {
            return Optional.empty();
        }

        Vec3 qVector = tVector.cross(edge1);
        double v = direction.dot(qVector) * invDeterminant;

        if (v < 0.0 || u + v > 1.0) {
            return Optional.empty();
        }

        double t = edge2.dot(qVector) * invDeterminant;

        if (t < tMin || t > tMax) {
            return Optional.empty();
        }

        Vec3 hitNormal = determinant < 0.0 ? normal.mul(-1.0) : normal;
        return Optional.of(new HitRecord(ray.at(t), hitNormal, t, this));
    }

    public Vec3 samplePoint(double xi1, double xi2) {
        double u = xi1;
        double v = xi2;

        if (u + v > 1.0) {
            u = 1.0 - u;
            v = 1.0 - v;
        }

        return v0.add(v1.sub(v0).mul(u)).add(v2.sub(v0).mul(v));
    }

    public Vec3 getNormal() {
        return Vec3.copyOf(normal);
    }
}
