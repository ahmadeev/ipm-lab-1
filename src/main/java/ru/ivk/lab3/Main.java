package ru.ivk.lab3;

import ru.ivk.common.math.Vec3;

import java.util.List;

public class Main {
    private static final int SAMPLE_COUNT = 100_000;
    private static final int PREVIEW_COUNT = 5;

    private static final Vec3 TRIANGLE_V1 = new Vec3(0, 0, 0);
    private static final Vec3 TRIANGLE_V2 = new Vec3(5, 0, 2);
    private static final Vec3 TRIANGLE_V3 = new Vec3(1, 4, 6);

    private static final Vec3 CIRCLE_CENTER = new Vec3(2, -1, 3);
    private static final Vec3 CIRCLE_NORMAL = new Vec3(2, 3, 4).normalize();
    private static final double CIRCLE_RADIUS = 3.5;

    public static void main(String[] args) {
        /* TRIANGLE */

        PlaneTriangle planeTriangle = new PlaneTriangle(TRIANGLE_V1, TRIANGLE_V2, TRIANGLE_V3);

        List<Vec3> trianglePoints = planeTriangle.generateUniformPoints(SAMPLE_COUNT);
        planeTriangle.printReport(trianglePoints, PREVIEW_COUNT);

        PlaneTriangle.ValidationResult triangleValidation = planeTriangle.validatePoints(trianglePoints);
        planeTriangle.printValidationReport(triangleValidation);

        /* CIRCLE */

        PlaneCircle planeCircle = new PlaneCircle(CIRCLE_CENTER, CIRCLE_NORMAL, CIRCLE_RADIUS);

        List<Vec3> circlePoints = planeCircle.generateUniformPoints(SAMPLE_COUNT);
        planeCircle.printReport(circlePoints, PREVIEW_COUNT);

        PlaneCircle.ValidationResult circleValidation = planeCircle.validatePoints(circlePoints);
        planeCircle.printValidationReport(circleValidation);
    }
}
