package ru.ivk.lab4new.sampling;

import ru.ivk.common.math.Vec3;

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

    public Vec3 sampleCosineHemisphere(Vec3 normal) {
        Vec3 unitNormal = normal.normalize();
        Vec3 helper = Math.abs(unitNormal.x) > 0.9
                ? new Vec3(0.0, 1.0, 0.0)
                : new Vec3(1.0, 0.0, 0.0);
        Vec3 tangent = helper.cross(unitNormal).normalize();
        Vec3 bitangent = unitNormal.cross(tangent).normalize();
        double radiusSquared = nextDouble();
        double radius = Math.sqrt(radiusSquared);
        double angle = 2.0 * Math.PI * nextDouble(); // [0, 2 * pi)
        double x = radius * Math.cos(angle);
        double y = radius * Math.sin(angle);
        double z = Math.sqrt(1.0 - radiusSquared);

        return tangent.mul(x)
                .add(bitangent.mul(y))
                .add(unitNormal.mul(z))
                .normalize();
    }

    public Vec3 reflect(Vec3 direction, Vec3 normal) {
        Vec3 unitDirection = direction.normalize();
        Vec3 unitNormal = normal.normalize();

        return unitDirection
                .sub(unitNormal.mul(2.0 * unitDirection.dot(unitNormal)))
                .normalize();
    }
}
