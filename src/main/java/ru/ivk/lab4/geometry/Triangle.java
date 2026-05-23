package ru.ivk.lab4.geometry;

import lombok.Getter;
import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.core.Ray;
import ru.ivk.lab4.material.Material;
import ru.ivk.lab4.scene.SceneObject;

import java.util.Objects;
import java.util.Optional;

/**
 * Треугольник сцены с пересечением через плоскость и проверку ребер.
 */
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
    @Getter
    private final SceneObject sceneObject;

    public Triangle(Vec3 v0, Vec3 v1, Vec3 v2, Material material) {
        this(v0, v1, v2, material, SceneObject.UNKNOWN);
    }

    public Triangle(Vec3 v0, Vec3 v1, Vec3 v2, Material material, SceneObject sceneObject) {
        this.v0 = Vec3.copyOf(v0);
        this.v1 = Vec3.copyOf(v1);
        this.v2 = Vec3.copyOf(v2);
        this.material = Objects.requireNonNull(material, "material");
        this.sceneObject = Objects.requireNonNull(sceneObject, "sceneObject");

        Vec3 cross = this.v1.sub(this.v0).cross(this.v2.sub(this.v0));
        double crossLength = cross.length();

        if (crossLength <= EPSILON) {
            throw new IllegalArgumentException("triangle must have non-zero area");
        }

        this.normal = cross.mul(1.0 / crossLength);
        this.area = crossLength * 0.5;
    }

    public Optional<HitRecord> intersect(Ray ray, double tMin, double tMax) {
        Vec3 direction = ray.getDirection();
        double denominator = normal.dot(direction);

        if (Math.abs(denominator) <= EPSILON) {
            return Optional.empty();
        }

        double t = v0.sub(ray.getOrigin()).dot(normal) / denominator;

        if (t < tMin || t > tMax) {
            return Optional.empty();
        }

        Vec3 point = ray.at(t);

        if (!contains(point)) {
            return Optional.empty();
        }

        Vec3 hitNormal = denominator < 0.0 ? normal : normal.mul(-1.0);
        return Optional.of(new HitRecord(point, hitNormal, t, this));
    }

    public Vec3 getNormal() {
        return Vec3.copyOf(normal);
    }

    public int getObjectId() {
        return sceneObject.getId();
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

    private boolean contains(Vec3 point) {
        return isInsideEdge(point, v0, v1)
                && isInsideEdge(point, v1, v2)
                && isInsideEdge(point, v2, v0);
    }

    private boolean isInsideEdge(Vec3 point, Vec3 edgeStart, Vec3 edgeEnd) {
        Vec3 edge = edgeEnd.sub(edgeStart);
        Vec3 toPoint = point.sub(edgeStart);

        return normal.dot(edge.cross(toPoint)) >= -EPSILON;
    }
}
