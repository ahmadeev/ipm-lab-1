package ru.ivk.lab3;

import ru.ivk.common.math.Plane;
import ru.ivk.common.math.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlaneTriangle {
    private static final Random random = new Random(123456L);
    private static final double EPSILON = 1e-6;
    private static final int TRIANGLE_GRID_SUBDIVISION_COUNT = 2;

    private final Vec3 vertex1;
    private final Vec3 vertex2;
    private final Vec3 vertex3;

    public PlaneTriangle(Vec3 vertex1, Vec3 vertex2, Vec3 vertex3) {
        this.vertex1 = Vec3.copyOf(vertex1);
        this.vertex2 = Vec3.copyOf(vertex2);
        this.vertex3 = Vec3.copyOf(vertex3);
    }

    public List<Vec3> generateUniformPoints(int sampleCount) {
        if (sampleCount <= 0) {
            throw new IllegalArgumentException("sampleCount must be positive");
        }

        List<Vec3> points = new ArrayList<>(sampleCount);

        for (int i = 0; i < sampleCount; i++) {
            points.add(sampleUniformPoint());
        }

        return points;
    }

    private Vec3 sampleUniformPoint() {
        double xiU = random.nextDouble();
        double xiV = random.nextDouble();

        if (xiU + xiV > 1.0) {
            xiU = 1.0 - xiU;
            xiV = 1.0 - xiV;
        }

        Vec3 u = vertex2.sub(vertex1);
        Vec3 v = vertex3.sub(vertex1);

        return vertex1.add(u.mul(xiU)).add(v.mul(xiV));
    }

    public ValidationResult validatePoints(List<Vec3> points) {
        Vec3 edge12 = vertex2.sub(vertex1);
        Vec3 edge23 = vertex3.sub(vertex2);
        Vec3 edge31 = vertex1.sub(vertex3);
        Vec3 triangleNormal = edge12.cross(vertex3.sub(vertex1));
        Plane plane = new Plane(triangleNormal, vertex1);
        double edgeTolerance = EPSILON * Math.max(1.0, triangleNormal.dot(triangleNormal));

        int pointsOffPlane = 0;
        int pointsOutsideTriangle = 0;
        double maxPlaneDistance = 0.0;
        double maxEdgeViolation = 0.0;
        int[] gridCellCounts = new int[TRIANGLE_GRID_SUBDIVISION_COUNT * TRIANGLE_GRID_SUBDIVISION_COUNT];

        for (Vec3 point : points) {
            double planeDistance = plane.calcDistanceTo(point);
            maxPlaneDistance = Math.max(maxPlaneDistance, planeDistance);

            if (!plane.isInPlane(point)) {
                pointsOffPlane++;
            }

            double s1 = triangleNormal.dot(edge12.cross(point.sub(vertex1)));
            double s2 = triangleNormal.dot(edge23.cross(point.sub(vertex2)));
            double s3 = triangleNormal.dot(edge31.cross(point.sub(vertex3)));

            double minSign = Math.min(s1, Math.min(s2, s3));
            double maxSign = Math.max(s1, Math.max(s2, s3));
            boolean isInside = minSign >= -edgeTolerance || maxSign <= edgeTolerance;
            double edgeViolation = Math.min(
                    Math.max(0.0, -minSign - edgeTolerance), // как сильно в '-'
                    Math.max(0.0, maxSign - edgeTolerance) // как сильно в '+'
            );

            maxEdgeViolation = Math.max(maxEdgeViolation, edgeViolation);

            if (!isInside) {
                pointsOutsideTriangle++;
            }

            ReferenceCoordinates referenceCoordinates = toReferenceCoordinates(point, edge12, vertex3.sub(vertex1));
            int gridCellIndex = mapToGridCellIndex(
                    referenceCoordinates.u,
                    referenceCoordinates.v,
                    TRIANGLE_GRID_SUBDIVISION_COUNT
            );
            gridCellCounts[gridCellIndex]++;
        }

        double expectedGridCellCount = points.size() / (double) gridCellCounts.length;
        double maxGridCellAbsoluteDeviation = 0.0;
        double maxGridCellRelativeDeviation = 0.0;

        for (int gridCellCount : gridCellCounts) {
            double absoluteDeviation = Math.abs(gridCellCount - expectedGridCellCount);
            double relativeDeviation = absoluteDeviation / expectedGridCellCount;

            maxGridCellAbsoluteDeviation = Math.max(maxGridCellAbsoluteDeviation, absoluteDeviation);
            maxGridCellRelativeDeviation = Math.max(maxGridCellRelativeDeviation, relativeDeviation);
        }

        return new ValidationResult(
                pointsOffPlane,
                pointsOutsideTriangle,
                maxPlaneDistance,
                maxEdgeViolation,
                TRIANGLE_GRID_SUBDIVISION_COUNT,
                gridCellCounts,
                expectedGridCellCount,
                maxGridCellAbsoluteDeviation,
                maxGridCellRelativeDeviation
        );
    }

    public void printReport(List<Vec3> points, int previewCount) {
        System.out.println("Равномерное распределение точек внутри треугольника");
        System.out.printf("V1 = %s%n", vertex1);
        System.out.printf("V2 = %s%n", vertex2);
        System.out.printf("V3 = %s%n", vertex3);
        System.out.printf("Сгенерировано точек: %d%n", points.size());

        System.out.println();
        System.out.println("Первые точки:");

        for (int i = 0; i < Math.min(previewCount, points.size()); i++) {
            System.out.printf("%d. %s%n", i + 1, points.get(i));
        }

        System.out.println();
    }

    public void printValidationReport(ValidationResult validation) {
        System.out.println("Проверка:");
        System.out.printf("Точек вне плоскости треугольника: %d%n", validation.pointsOffPlane);
        System.out.printf("Точек вне треугольника: %d%n", validation.pointsOutsideTriangle);
        System.out.printf("Макс. расстояние до плоскости треугольника: %.12f%n", validation.maxPlaneDistance);
        System.out.printf("Макс. нарушение по рёбрам треугольника: %.12f%n", validation.maxEdgeViolation);
        System.out.printf(
                "Ожидаемое число точек в каждой ячейке сетки %dx%d: %.2f%n",
                validation.gridSubdivisionCount,
                validation.gridSubdivisionCount,
                validation.expectedGridCellCount
        );
        System.out.printf("Число точек по ячейкам сетки: %s%n", Arrays.toString(validation.gridCellCounts));
        System.out.printf("Макс. абсолютное отклонение по сетке: %.2f%n", validation.maxGridCellAbsoluteDeviation);
        System.out.printf("Макс. относительное отклонение по сетке: %.6f%n", validation.maxGridCellRelativeDeviation);
        System.out.println();
    }

    private ReferenceCoordinates toReferenceCoordinates(Vec3 point, Vec3 uAxis, Vec3 vAxis) {
        // p = v1 + u * (v2 - v1) + v * (v3 - v1)
        Vec3 offset = point.sub(vertex1);
        double uu = uAxis.dot(uAxis);
        double uv = uAxis.dot(vAxis);
        double vv = vAxis.dot(vAxis);
        double pu = offset.dot(uAxis);
        double pv = offset.dot(vAxis);
        double denominator = uu * vv - uv * uv;

        // pu = uu * u + uv * v
        // pv = uv * u + vv * v

        if (Math.abs(denominator) <= EPSILON) {
            throw new IllegalStateException("triangle must not be degenerate");
        }

        double u = (vv * pu - uv * pv) / denominator;
        double v = (uu * pv - uv * pu) / denominator;

        return new ReferenceCoordinates(u, v);
    }

    private static int mapToGridCellIndex(double u, double v, int subdivisionCount) {
        double scaledU = clamp(u * subdivisionCount, 0.0, subdivisionCount);
        double scaledV = clamp(v * subdivisionCount, 0.0, subdivisionCount);
        int i = Math.min((int) scaledU, subdivisionCount - 1);
        int j = Math.min((int) scaledV, subdivisionCount - 1);

        if (i + j >= subdivisionCount) {
            if (i > 0) {
                i--;
            } else {
                j--;
            }
        }

        double localU = scaledU - i;
        double localV = scaledV - j;
        int rowStartIndex = j * (2 * subdivisionCount - j);
        int offsetInRow = 2 * i;

        if (i + j < subdivisionCount - 1 && localU + localV > 1.0) {
            offsetInRow++;
        }

        return rowStartIndex + offsetInRow;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class ReferenceCoordinates {
        private final double u;
        private final double v;

        private ReferenceCoordinates(double u, double v) {
            this.u = u;
            this.v = v;
        }
    }

    public static final class ValidationResult {
        private final int pointsOffPlane;
        private final int pointsOutsideTriangle;
        private final double maxPlaneDistance;
        private final double maxEdgeViolation;
        private final int gridSubdivisionCount;
        private final int[] gridCellCounts;
        private final double expectedGridCellCount;
        private final double maxGridCellAbsoluteDeviation;
        private final double maxGridCellRelativeDeviation;

        private ValidationResult(
                int pointsOffPlane,
                int pointsOutsideTriangle,
                double maxPlaneDistance,
                double maxEdgeViolation,
                int gridSubdivisionCount,
                int[] gridCellCounts,
                double expectedGridCellCount,
                double maxGridCellAbsoluteDeviation,
                double maxGridCellRelativeDeviation
        ) {
            this.pointsOffPlane = pointsOffPlane;
            this.pointsOutsideTriangle = pointsOutsideTriangle;
            this.maxPlaneDistance = maxPlaneDistance;
            this.maxEdgeViolation = maxEdgeViolation;
            this.gridSubdivisionCount = gridSubdivisionCount;
            this.gridCellCounts = Arrays.copyOf(gridCellCounts, gridCellCounts.length);
            this.expectedGridCellCount = expectedGridCellCount;
            this.maxGridCellAbsoluteDeviation = maxGridCellAbsoluteDeviation;
            this.maxGridCellRelativeDeviation = maxGridCellRelativeDeviation;
        }
    }
}
