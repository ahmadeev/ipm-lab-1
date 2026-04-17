package ru.ivk.lab3;

import ru.ivk.common.math.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static final Random random = new Random(123456L);
    private static final int SAMPLE_COUNT = 100_000;
    private static final int PREVIEW_COUNT = 5;

    private static final Vec3 V1 = new Vec3(0, 0, 0);
    private static final Vec3 V2 = new Vec3(5, 0, 2);
    private static final Vec3 V3 = new Vec3(1, 4, 6);

    public static void main(String[] args) {
        List<Vec3> points = generateUniformPointsInTriangle(V1, V2, V3, SAMPLE_COUNT);

        System.out.println("Lab 3 - uniform points inside triangle");
        System.out.printf("V1 = %s%n", V1);
        System.out.printf("V2 = %s%n", V2);
        System.out.printf("V3 = %s%n", V3);
        System.out.printf("Generated points: %d%n", points.size());
        System.out.println("First points:");

        for (int i = 0; i < Math.min(PREVIEW_COUNT, points.size()); i++) {
            System.out.printf("%d. %s%n", i + 1, points.get(i));
        }
    }

    private static List<Vec3> generateUniformPointsInTriangle(Vec3 v1, Vec3 v2, Vec3 v3, int sampleCount) {
        if (sampleCount <= 0) {
            throw new IllegalArgumentException("sampleCount must be positive");
        }

        List<Vec3> points = new ArrayList<>(sampleCount);

        for (int i = 0; i < sampleCount; i++) {
            points.add(sampleUniformPointInTriangle(v1, v2, v3));
        }

        return points;
    }

    private static Vec3 sampleUniformPointInTriangle(Vec3 v1, Vec3 v2, Vec3 v3) {
        double xiU = random.nextDouble();
        double xiV = random.nextDouble();

        if (xiU + xiV > 1.0) {
            xiU = 1.0 - xiU;
            xiV = 1.0 - xiV;
        }

        Vec3 u = v2.sub(v1);
        Vec3 v = v3.sub(v1);

        return v1.add(u.mul(xiU)).add(v.mul(xiV));
    }
}
