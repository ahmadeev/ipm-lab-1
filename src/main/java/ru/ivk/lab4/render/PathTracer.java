package ru.ivk.lab4.render;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.core.ColorRgb;
import ru.ivk.lab4.core.Ray;
import ru.ivk.lab4.geometry.HitRecord;
import ru.ivk.lab4.material.Material;
import ru.ivk.lab4.sampling.LightSample;
import ru.ivk.lab4.sampling.LightSampler;
import ru.ivk.lab4.sampling.Sampler;
import ru.ivk.lab4.scene.Scene;

import java.util.Optional;

public final class PathTracer {
    private static final double EPSILON = 1e-4;
    private static final int RUSSIAN_ROULETTE_START_DEPTH = 3;

    public ColorRgb trace(Scene scene, Ray ray, int maxDepth, Sampler sampler) {
        return trace(scene, new LightSampler(scene.getLights()), ray, maxDepth, sampler);
    }

    public ColorRgb trace(Scene scene, LightSampler lightSampler, Ray ray, int maxDepth, Sampler sampler) {
        return traceRecursive(scene, lightSampler, ray, maxDepth, sampler, 0);
    }

    private ColorRgb traceRecursive(
            Scene scene,
            LightSampler lightSampler,
            Ray ray,
            int maxDepth,
            Sampler sampler,
            int depth
    ) {
        if (depth >= maxDepth) {
            return ColorRgb.BLACK;
        }

        Optional<HitRecord> hitOptional = scene.intersect(ray, EPSILON, Double.POSITIVE_INFINITY);

        if (!hitOptional.isPresent()) {
            return ColorRgb.BLACK;
        }

        HitRecord hit = hitOptional.get();
        Material material = hit.getMaterial();
        ColorRgb result = material.getEmission();

        if (material.isLight()) {
            return result;
        }

        result = result.add(estimateDirectLighting(scene, lightSampler, hit, sampler));

        double diffuseWeight = material.diffuseWeight();
        double specularWeight = material.specularWeight();
        double totalWeight = diffuseWeight + specularWeight;

        if (totalWeight <= 0.0) {
            return result;
        }

        double survivalProbability = survivalProbability(material, depth);

        if (depth >= RUSSIAN_ROULETTE_START_DEPTH && sampler.nextDouble() > survivalProbability) {
            return result;
        }

        boolean chooseDiffuse = sampler.nextDouble() < diffuseWeight / totalWeight;
        ColorRgb reflectance = chooseDiffuse ? material.getDiffuse() : material.getSpecular();
        double eventProbability = chooseDiffuse ? diffuseWeight / totalWeight : specularWeight / totalWeight;

        Vec3 origin = hit.getPoint().add(hit.getNormal().mul(EPSILON));
        Vec3 nextDirection = chooseDiffuse
                ? sampler.cosineHemisphere(hit.getNormal())
                : Sampler.reflect(ray.getDirection(), hit.getNormal());

        ColorRgb indirect = traceRecursive(
                scene,
                lightSampler,
                new Ray(origin, nextDirection),
                maxDepth,
                sampler,
                depth + 1
        );

        double rouletteFactor = depth >= RUSSIAN_ROULETTE_START_DEPTH ? survivalProbability : 1.0;
        ColorRgb weightedIndirect = indirect.mul(reflectance).div(eventProbability * rouletteFactor);

        return result.add(weightedIndirect);
    }

    private ColorRgb estimateDirectLighting(Scene scene, LightSampler lightSampler, HitRecord hit, Sampler sampler) {
        if (!lightSampler.hasLights()) {
            return ColorRgb.BLACK;
        }

        LightSample sample = lightSampler.sample(sampler);
        Vec3 hitPoint = hit.getPoint();
        Vec3 lightPoint = sample.getPoint();
        Vec3 toLight = lightPoint.sub(hitPoint);
        double distanceSquared = toLight.dot(toLight);
        double distance = Math.sqrt(distanceSquared);

        if (distance <= EPSILON) {
            return ColorRgb.BLACK;
        }

        Vec3 directionToLight = toLight.mul(1.0 / distance);
        double surfaceCos = Math.max(0.0, hit.getNormal().dot(directionToLight));
        double lightCos = Math.max(0.0, sample.getLight().getNormal().dot(directionToLight.mul(-1.0)));

        if (surfaceCos <= 0.0 || lightCos <= 0.0) {
            return ColorRgb.BLACK;
        }

        Ray shadowRay = new Ray(hitPoint.add(hit.getNormal().mul(EPSILON)), directionToLight);

        if (scene.isOccluded(shadowRay, EPSILON, distance - EPSILON)) {
            return ColorRgb.BLACK;
        }

        double pdfArea = sample.getLightPickProbability() / sample.getLight().getArea();
        double geometryTerm = surfaceCos * lightCos / distanceSquared;

        return sample.getLight().getMaterial().getEmission()
                .mul(hit.getMaterial().getDiffuse())
                .mul(geometryTerm / (Math.PI * pdfArea));
    }

    private static double survivalProbability(Material material, int depth) {
        if (depth < RUSSIAN_ROULETTE_START_DEPTH) {
            return 1.0;
        }

        double maxReflectance = material.getDiffuse().add(material.getSpecular()).maxComponent();
        return Math.max(0.1, Math.min(0.95, maxReflectance));
    }
}
