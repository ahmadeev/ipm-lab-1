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
}
