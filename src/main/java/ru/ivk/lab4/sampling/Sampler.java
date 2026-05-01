package ru.ivk.lab4.sampling;

import ru.ivk.common.math.Vec3;
import ru.ivk.common.utils.Utils;

import java.util.Random;

public final class Sampler {
    private final Random random;

    public Sampler(long seed) {
        this.random = new Random(seed);
    }

    public double nextDouble() {
        return random.nextDouble();
    }

    public Vec3 cosineHemisphere(Vec3 normal) {
        Vec3 n = normal.normalize();
        Vec3 u = buildPerpendicularAxis(n);
        Vec3 v = n.cross(u).normalize();

        double xi1 = random.nextDouble();
        double xi2 = random.nextDouble();
        double r = Math.sqrt(xi1);
        double phi = 2.0 * Math.PI * xi2;
        double x = r * Math.cos(phi);
        double y = r * Math.sin(phi);
        double z = Math.sqrt(Math.max(0.0, 1.0 - xi1));

        return u.mul(x).add(v.mul(y)).add(n.mul(z)).normalize();
    }

    public int chooseByWeights(double[] weights) {
        double sum = 0.0;

        for (double weight : weights) {
            sum += Math.max(0.0, weight);
        }

        if (sum <= 0.0) {
            return 0;
        }

        double target = random.nextDouble() * sum;
        double prefix = 0.0;

        for (int i = 0; i < weights.length; i++) {
            prefix += Math.max(0.0, weights[i]);

            if (target <= prefix) {
                return i;
            }
        }

        return weights.length - 1;
    }

    private static Vec3 buildPerpendicularAxis(Vec3 normal) {
        Vec3 referenceAxis = selectReferenceAxis(normal);
        return referenceAxis.cross(normal).normalize();
    }

    private static Vec3 selectReferenceAxis(Vec3 normal) {
        double absX = Math.abs(normal.x);
        double absY = Math.abs(normal.y);
        double absZ = Math.abs(normal.z);

        if (absX <= absY && absX <= absZ) {
            return new Vec3(1, 0, 0);
        }

        if (absY <= absZ) {
            return new Vec3(0, 1, 0);
        }

        return new Vec3(0, 0, 1);
    }

    public static Vec3 reflect(Vec3 direction, Vec3 normal) {
        Vec3 d = direction.normalize();
        Vec3 n = normal.normalize();
        return d.sub(n.mul(2.0 * d.dot(n))).normalize();
    }

    public static double positiveDot(Vec3 a, Vec3 b) {
        return Utils.clamp(a.normalize().dot(b.normalize()), 0.0, 1.0);
    }
}
