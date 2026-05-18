package ru.ivk.lab4new.render;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4new.core.ColorRgb;
import ru.ivk.lab4new.core.Ray;
import ru.ivk.lab4new.geometry.HitRecord;
import ru.ivk.lab4new.material.Material;
import ru.ivk.lab4new.scene.Scene;
import ru.ivk.lab4new.sampling.LightSample;
import ru.ivk.lab4new.sampling.LightSampler;
import ru.ivk.lab4new.sampling.Sampler;

import java.util.Optional;

/**
 * Трассировщик одного луча для текущей базовой модели сцены.
 */
public final class PathTracer {
    private static final double EPSILON = 1e-4;
    private final LightSampler lightSampler = new LightSampler();

    public ColorRgb trace(Scene scene, Ray ray, Sampler sampler, int depth) {
        if (depth <= 0) {
            return ColorRgb.BLACK;
        }

        Optional<HitRecord> hit = scene.intersect(ray, EPSILON, Double.POSITIVE_INFINITY);

        // пересечение не найдено
        if (!hit.isPresent()) {
            return directionColor(ray.getDirection());
        }

        Material material = hit.get().getTriangle().getMaterial();

        // пересечение -- свет
        if (material.isLight()) {
            return material.getEmission();
        }

        // расчет света
        return directLighting(scene, hit.get(), material, sampler)
                .add(indirectDiffuse(scene, hit.get(), material, sampler, depth));
    }

    private ColorRgb directLighting(Scene scene, HitRecord hit, Material material, Sampler sampler) {
        if (scene.getLights().isEmpty()) {
            return ColorRgb.BLACK;
        }

        LightSample light = lightSampler.sample(scene, sampler);
        Vec3 hitPoint = hit.getPoint();
        Vec3 hitNormal = hit.getNormal();
        Vec3 toLight = light.getPoint().sub(hitPoint);
        double distanceSquared = toLight.dot(toLight);

        if (distanceSquared <= EPSILON) {
            return ColorRgb.BLACK;
        }

        double distance = Math.sqrt(distanceSquared);
        Vec3 lightDirection = toLight.mul(1.0 / distance);
        // максимум 90 градусов между нормалью и направлением на свет, иначе поверхность отвернута от света
        double surfaceCos = Math.max(0.0, hitNormal.dot(lightDirection));
        // свет направлен на точку?
        double lightCos = Math.max(0.0, light.getNormal().dot(lightDirection.mul(-1.0)));

        if (surfaceCos <= 0.0 || lightCos <= 0.0 || light.getPdf() <= 0.0) {
            return ColorRgb.BLACK;
        }

        Ray shadowRay = new Ray(hitPoint.add(hitNormal.mul(EPSILON)), lightDirection);

        if (scene.isOccluded(shadowRay, EPSILON, distance - EPSILON)) {
            return ColorRgb.BLACK;
        }

        double geometry = surfaceCos * lightCos / distanceSquared;
        return material.getDiffuse().mul(light.getEmission()).mul(geometry / (Math.PI * light.getPdf()));
    }

    private ColorRgb indirectDiffuse(Scene scene, HitRecord hit, Material material, Sampler sampler, int depth) {
        if (depth <= 1 || material.getDiffuse().isBlack()) {
            return ColorRgb.BLACK;
        }

        Vec3 hitPoint = hit.getPoint();
        Vec3 hitNormal = hit.getNormal();
        Vec3 direction = sampler.sampleCosineHemisphere(hitNormal);
        Ray bounceRay = new Ray(hitPoint.add(hitNormal.mul(EPSILON)), direction);

        return material.getDiffuse().mul(trace(scene, bounceRay, sampler, depth - 1));
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
