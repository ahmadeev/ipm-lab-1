package ru.ivk.lab4new.sampling;

import ru.ivk.lab4new.geometry.Triangle;
import ru.ivk.lab4new.scene.Scene;

import java.util.List;
import java.util.Objects;

/**
 * Выбирает протяженный источник света по мощности и точку на нем равномерно по площади.
 */
public final class LightSampler {
    public LightSample sample(Scene scene, Sampler sampler) {
        Objects.requireNonNull(scene, "scene");
        Objects.requireNonNull(sampler, "sampler");

        List<Triangle> lights = scene.getLights();

        if (lights.isEmpty()) {
            throw new IllegalArgumentException("scene must contain at least one light");
        }

        double[] weights = buildWeights(lights);
        double totalWeight = sum(weights);
        int lightIndex = sampler.chooseByWeights(weights);
        Triangle light = lights.get(lightIndex);
        double selectionPdf = weights[lightIndex] / totalWeight; // вероятность выбора источника
        double areaPdf = 1.0 / light.getArea(); // вероятность выбора точки

        return new LightSample(
                light,
                light.samplePoint(sampler.nextDouble(), sampler.nextDouble()),
                light.getNormal(),
                light.getMaterial().getEmission(),
                selectionPdf,
                areaPdf
        );
    }

    private double[] buildWeights(List<Triangle> lights) {
        double[] weights = new double[lights.size()];

        for (int index = 0; index < lights.size(); index++) {
            Triangle light = lights.get(index);
            weights[index] = light.getArea() * light.getMaterial().getEmission().average();
        }

        return weights;
    }

    private double sum(double[] weights) {
        double total = 0.0;

        for (double weight : weights) {
            total += weight;
        }

        return total;
    }
}
