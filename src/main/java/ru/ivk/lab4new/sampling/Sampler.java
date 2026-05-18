package ru.ivk.lab4new.sampling;

import java.util.Random;

/**
 * Генератор случайных чисел для выборок внутри рендера.
 */
public final class Sampler {
    private final Random random;

    public Sampler(long seed) {
        this.random = new Random(seed);
    }

    public double nextDouble() {
        return random.nextDouble();
    }

    public int chooseByWeights(double[] weights) {
        if (weights.length == 0) {
            throw new IllegalArgumentException("weights must not be empty");
        }

        double total = 0.0;

        for (double weight : weights) {
            if (!Double.isFinite(weight) || weight < 0.0) {
                throw new IllegalArgumentException("weights must be finite and non-negative");
            }

            total += weight;
        }

        if (total <= 0.0) {
            throw new IllegalArgumentException("at least one weight must be positive");
        }

        double value = nextDouble() * total;
        double prefix = 0.0;

        for (int index = 0; index < weights.length; index++) {
            prefix += weights[index];

            if (value < prefix) {
                return index;
            }
        }

        return weights.length - 1;
    }
}
