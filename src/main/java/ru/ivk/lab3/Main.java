package ru.ivk.lab3;

import ru.ivk.common.math.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static final Random random = new Random(123456L);
    private static final int SAMPLE_COUNT = 100_000;
    private static final int PREVIEW_COUNT = 5;

    private static final Vec3 TRIANGLE_V1 = new Vec3(0, 0, 0);
    private static final Vec3 TRIANGLE_V2 = new Vec3(5, 0, 2);
    private static final Vec3 TRIANGLE_V3 = new Vec3(1, 4, 6);

    private static final Vec3 CIRCLE_CENTER = new Vec3(2, -1, 3);
    private static final Vec3 CIRCLE_NORMAL = new Vec3(2, 3, 4).normalize();
    private static final double CIRCLE_RADIUS = 3.5;

    public static void main(String[] args) {
        List<Vec3> trianglePoints = generateUniformPointsInTriangle(TRIANGLE_V1, TRIANGLE_V2, TRIANGLE_V3, SAMPLE_COUNT);
        List<Vec3> circlePoints = generateUniformPointsInCircle(CIRCLE_CENTER, CIRCLE_NORMAL, CIRCLE_RADIUS, SAMPLE_COUNT);

        System.out.println("Lab 3 - uniform points inside triangle");
        System.out.printf("V1 = %s%n", TRIANGLE_V1);
        System.out.printf("V2 = %s%n", TRIANGLE_V2);
        System.out.printf("V3 = %s%n", TRIANGLE_V3);
        System.out.printf("Generated points: %d%n", trianglePoints.size());
        System.out.println("First points:");

        for (int i = 0; i < Math.min(PREVIEW_COUNT, trianglePoints.size()); i++) {
            System.out.printf("%d. %s%n", i + 1, trianglePoints.get(i));
        }

        System.out.println();
        System.out.println("Lab 3 - uniform points inside circle");
        System.out.printf("C = %s%n", CIRCLE_CENTER);
        System.out.printf("N = %s%n", CIRCLE_NORMAL);
        System.out.printf("Rc = %.4f%n", CIRCLE_RADIUS);
        System.out.printf("Generated points: %d%n", circlePoints.size());
        System.out.println("First points:");

        for (int i = 0; i < Math.min(PREVIEW_COUNT, circlePoints.size()); i++) {
            System.out.printf("%d. %s%n", i + 1, circlePoints.get(i));
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

    private static List<Vec3> generateUniformPointsInCircle(Vec3 center, Vec3 normal, double radius, int sampleCount) {
        if (sampleCount <= 0) {
            throw new IllegalArgumentException("sampleCount must be positive");
        }

        if (radius <= 0.0) {
            throw new IllegalArgumentException("radius must be positive");
        }

        Vec3 normalizedNormal = requireUnitNormal(normal);
        Vec3 uAxis = buildPerpendicularAxis(normalizedNormal);
        Vec3 vAxis = normalizedNormal.cross(uAxis).normalize();

        List<Vec3> points = new ArrayList<>(sampleCount);

        for (int i = 0; i < sampleCount; i++) {
            points.add(sampleUniformPointInCircle(center, radius, uAxis, vAxis));
        }

        return points;
    }

    private static Vec3 sampleUniformPointInCircle(Vec3 center, double radius, Vec3 uAxis, Vec3 vAxis) {
        double xiRadius = random.nextDouble();
        double xiPhi = random.nextDouble();

        double r = radius * Math.sqrt(xiRadius);
        double phi = 2.0 * Math.PI * xiPhi;

        return center
                .add(vAxis.mul(r * Math.cos(phi)))
                .add(uAxis.mul(r * Math.sin(phi)));
    }

    private static Vec3 requireUnitNormal(Vec3 normal) {
        if (normal.length() == 0.0) {
            throw new IllegalArgumentException("normal must be non-zero");
        }

        return normal.normalize();
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
}
