package ru.ivk.lab3;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.ivk.common.math.Plane;
import ru.ivk.common.math.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class PlainTriangle {
    private static final Random random = new Random(123456L);

    private static final double EPSILON = 1e-6;

    private final Vec3 vertex1;
    private final Vec3 vertex2;
    private final Vec3 vertex3;

    protected List<Vec3> generateUniformPoints(Vec3 v1, Vec3 v2, Vec3 v3, int sampleCount) {
        if (sampleCount <= 0) {
            throw new IllegalArgumentException("sampleCount must be positive");
        }

        List<Vec3> points = new ArrayList<>(sampleCount);

        for (int i = 0; i < sampleCount; i++) {
            points.add(sampleUniformPoint(v1, v2, v3));
        }

        return points;
    }

    private Vec3 sampleUniformPoint(Vec3 v1, Vec3 v2, Vec3 v3) {
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

    protected TriangleValidationResult validatePoints(List<Vec3> points, Vec3 v1, Vec3 v2, Vec3 v3) {
        Vec3 edge12 = v2.sub(v1);
        Vec3 edge23 = v3.sub(v2);
        Vec3 edge31 = v1.sub(v3);
        Vec3 triangleNormal = edge12.cross(v3.sub(v1));
        Plane plane = new Plane(triangleNormal, v1);
        double edgeTolerance = EPSILON * Math.max(1.0, triangleNormal.dot(triangleNormal));

        int pointsOffPlane = 0;
        int pointsOutsideTriangle = 0;
        double maxPlaneDistance = 0.0;
        double maxEdgeViolation = 0.0;

        for (Vec3 point : points) {
            double planeDistance = plane.calcDistanceTo(point);
            maxPlaneDistance = Math.max(maxPlaneDistance, planeDistance);

            if (!plane.isInPlane(point)) {
                pointsOffPlane++;
            }

            double s1 = triangleNormal.dot(edge12.cross(point.sub(v1)));
            double s2 = triangleNormal.dot(edge23.cross(point.sub(v2)));
            double s3 = triangleNormal.dot(edge31.cross(point.sub(v3)));

            double minSign = Math.min(s1, Math.min(s2, s3));
            double maxSign = Math.max(s1, Math.max(s2, s3));
            boolean isInside = minSign >= -edgeTolerance || maxSign <= edgeTolerance;
            double edgeViolation = Math.min(
                    Math.max(0.0, -minSign - edgeTolerance),
                    Math.max(0.0, maxSign - edgeTolerance)
            );

            maxEdgeViolation = Math.max(maxEdgeViolation, edgeViolation);

            if (!isInside) {
                pointsOutsideTriangle++;
            }
        }

        return new TriangleValidationResult(
                pointsOffPlane,
                pointsOutsideTriangle,
                maxPlaneDistance,
                maxEdgeViolation
        );
    }

    protected void printReport(List<Vec3> points, int previewCount) {
        System.out.println("Uniform points inside triangle");
        System.out.printf("V1 = %s%n", vertex1);
        System.out.printf("V2 = %s%n", vertex2);
        System.out.printf("V3 = %s%n", vertex3);
        System.out.printf("Generated points: %d%n", points.size());

        System.out.println();
        System.out.println("First points:");

        for (int i = 0; i < Math.min(previewCount, points.size()); i++) {
            System.out.printf("%d. %s%n", i + 1, points.get(i));
        }

        System.out.println();
    }

    protected void printValidationReport(TriangleValidationResult validation) {
        System.out.println("Validation:");
        System.out.printf("Points off triangle plane: %d%n", validation.pointsOffPlane);
        System.out.printf("Points outside triangle by edge test: %d%n", validation.pointsOutsideTriangle);
        System.out.printf("Max distance to triangle plane: %.12f%n", validation.maxPlaneDistance);
        System.out.printf("Max triangle edge violation: %.12f%n", validation.maxEdgeViolation);
        System.out.println();
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    protected static final class TriangleValidationResult {
        private final int pointsOffPlane;
        private final int pointsOutsideTriangle;
        private final double maxPlaneDistance;
        private final double maxEdgeViolation;
    }
}
