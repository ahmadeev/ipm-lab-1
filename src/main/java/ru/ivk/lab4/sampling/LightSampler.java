package ru.ivk.lab4.sampling;

import ru.ivk.lab4.geometry.Triangle;

import java.util.List;

public final class LightSampler {
    private final List<Triangle> lights;
    private final double[] weights;
    private final double totalWeight;

    public LightSampler(List<Triangle> lights) {
        this.lights = lights;
        this.weights = new double[lights.size()];

        double sum = 0.0;

        for (int i = 0; i < lights.size(); i++) {
            double power = lights.get(i).getArea() * lights.get(i).getMaterial().getEmission().average();
            weights[i] = power;
            sum += power;
        }

        this.totalWeight = sum;
    }

    public boolean hasLights() {
        return !lights.isEmpty() && totalWeight > 0.0;
    }

    public LightSample sample(Sampler sampler) {
        int index = sampler.chooseByWeights(weights);
        Triangle light = lights.get(index);
        double probability = weights[index] / totalWeight;

        return new LightSample(
                light,
                light.samplePoint(sampler.nextDouble(), sampler.nextDouble()),
                probability
        );
    }
}
