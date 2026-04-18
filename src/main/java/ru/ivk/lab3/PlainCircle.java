package ru.ivk.lab3;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.ivk.common.math.Plane;
import ru.ivk.common.math.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class PlainCircle {
    private static final Random random = new Random(123456L);

    private static final double EPSILON = 1e-6;

    private final Vec3 circleCenter;
    private final Vec3 circleNormal;
    private final double circleRadius;

    protected List<Vec3> generateUniformPoints(Vec3 center, Vec3 normal, double radius, int sampleCount) {
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
            points.add(sampleUniformPoint(center, radius, uAxis, vAxis));
        }

        return points;
    }

    private Vec3 sampleUniformPoint(Vec3 center, double radius, Vec3 uAxis, Vec3 vAxis) {
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

    protected CircleValidationResult validatePoints(List<Vec3> points, Vec3 center, Vec3 normal, double radius) {
        Vec3 normalizedNormal = requireUnitNormal(normal);
        Plane plane = new Plane(normalizedNormal, center);
        double radialTolerance = EPSILON * Math.max(1.0, radius);

        int pointsOffPlane = 0;
        int pointsOutsideCircle = 0;
        double maxPlaneDistance = 0.0;
        double maxRadialOvershoot = 0.0;

        for (Vec3 point : points) {
            double planeDistance = plane.calcDistanceTo(point);
            maxPlaneDistance = Math.max(maxPlaneDistance, planeDistance);

            if (!plane.isInPlane(point)) {
                pointsOffPlane++;
            }

            Vec3 offset = point.sub(center);
            double normalProjection = offset.dot(normalizedNormal);
            double radialSquared = offset.dot(offset) - normalProjection * normalProjection;
            double radialDistance = Math.sqrt(Math.max(0.0, radialSquared));
            double radialOvershoot = Math.max(0.0, radialDistance - radius);

            maxRadialOvershoot = Math.max(maxRadialOvershoot, radialOvershoot);

            if (radialOvershoot > radialTolerance) {
                pointsOutsideCircle++;
            }
        }

        return new CircleValidationResult(
                pointsOffPlane,
                pointsOutsideCircle,
                maxPlaneDistance,
                maxRadialOvershoot
        );
    }

    protected void printReport(List<Vec3> points, int previewCount) {
        System.out.println("Uniform points inside circle");
        System.out.printf("C = %s%n", circleCenter);
        System.out.printf("N = %s%n", circleNormal);
        System.out.printf("Rc = %.4f%n", circleRadius);
        System.out.printf("Generated points: %d%n", points.size());

        System.out.println();
        System.out.println("First points:");

        for (int i = 0; i < Math.min(previewCount, points.size()); i++) {
            System.out.printf("%d. %s%n", i + 1, points.get(i));
        }

        System.out.println();
    }

    protected void printValidationReport(CircleValidationResult validation) {
        System.out.println("Validation:");
        System.out.printf("Points off circle plane: %d%n", validation.pointsOffPlane);
        System.out.printf("Points outside circle by radial check: %d%n", validation.pointsOutsideCircle);
        System.out.printf("Max distance to circle plane: %.12f%n", validation.maxPlaneDistance);
        System.out.printf("Max circle radial overshoot: %.12f%n", validation.maxRadialOvershoot);
        System.out.println();
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    protected static final class CircleValidationResult {
        private final int pointsOffPlane;
        private final int pointsOutsideCircle;
        private final double maxPlaneDistance;
        private final double maxRadialOvershoot;
    }
}
