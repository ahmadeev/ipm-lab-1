package ru.ivk.lab4new.render;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4new.core.ColorRgb;
import ru.ivk.lab4new.core.Ray;
import ru.ivk.lab4new.geometry.HitRecord;
import ru.ivk.lab4new.material.Material;
import ru.ivk.lab4new.scene.Scene;

import java.util.Optional;

/**
 * Трассировщик одного луча для текущей базовой модели сцены.
 */
public final class PathTracer {
    private static final double EPSILON = 1e-4;

    public ColorRgb trace(Scene scene, Ray ray) {
        Optional<HitRecord> hit = scene.intersect(ray, EPSILON, Double.POSITIVE_INFINITY);

        if (!hit.isPresent()) {
            return directionColor(ray.getDirection());
        }

        Material material = hit.get().getTriangle().getMaterial();

        if (material.isLight()) {
            return material.getEmission();
        }

        return material.getDiffuse();
    }

    private ColorRgb directionColor(Vec3 direction) {
        Vec3 unit = direction.normalize();

        return new ColorRgb(
                0.5 * (unit.x + 1.0),
                0.5 * (unit.y + 1.0),
                0.5 * (unit.z + 1.0)
        );
    }
}
