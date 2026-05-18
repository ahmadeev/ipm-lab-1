package ru.ivk.lab4.scene;

import lombok.Getter;
import ru.ivk.lab4.core.Ray;
import ru.ivk.lab4.geometry.HitRecord;
import ru.ivk.lab4.geometry.Triangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сцена, представленная списком треугольников для поиска ближайшего попадания.
 */
@Getter
public final class Scene {
    private final List<Triangle> triangles;
    private final List<Triangle> lights;

    public Scene(List<Triangle> triangles) {
        Objects.requireNonNull(triangles, "triangles");

        if (triangles.isEmpty()) {
            throw new IllegalArgumentException("scene must contain at least one triangle");
        }

        this.triangles = Collections.unmodifiableList(new ArrayList<>(triangles));
        this.lights = Collections.unmodifiableList(collectLights(this.triangles));
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

    private List<Triangle> collectLights(List<Triangle> triangles) {
        List<Triangle> lightTriangles = new ArrayList<>();

        for (Triangle triangle : triangles) {
            if (triangle.getMaterial().isLight()) {
                lightTriangles.add(triangle);
            }
        }

        return lightTriangles;
    }
}
