package ru.ivk.lab4.scene;

import lombok.Getter;
import ru.ivk.lab4.core.Ray;
import ru.ivk.lab4.geometry.HitRecord;
import ru.ivk.lab4.geometry.Triangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
public final class Scene {
    private final List<Triangle> triangles;
    private final List<Triangle> lights;

    public Scene(List<Triangle> triangles) {
        this.triangles = Collections.unmodifiableList(new ArrayList<>(triangles));

        List<Triangle> lightTriangles = new ArrayList<>();

        for (Triangle triangle : triangles) {
            if (triangle.getMaterial().isLight()) {
                lightTriangles.add(triangle);
            }
        }

        this.lights = Collections.unmodifiableList(lightTriangles);
    }

    public Optional<HitRecord> intersect(Ray ray, double tMin, double tMax) {
        HitRecord closestHit = null;
        double closestT = tMax;

        for (Triangle triangle : triangles) {
            Optional<HitRecord> hit = triangle.intersect(ray, tMin, closestT);

            if (hit.isPresent()) {
                closestHit = hit.get();
                closestT = closestHit.getT();
            }
        }

        return Optional.ofNullable(closestHit);
    }

    public boolean isOccluded(Ray ray, double tMin, double tMax) {
        return intersect(ray, tMin, tMax).isPresent();
    }
}
