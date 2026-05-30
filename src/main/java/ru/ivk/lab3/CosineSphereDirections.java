package ru.ivk.lab3;

import ru.ivk.common.math.Vec3;
import ru.ivk.common.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CosineSphereDirections {
    private static final double EPSILON = 1e-6;
    private static final int MU_BIN_COUNT = 10;
    private static final int THETA_RING_STEP_DEGREES = 15;
    private static final int THETA_RING_COUNT = 90 / THETA_RING_STEP_DEGREES;

    private final Vec3 sphereCenter;
    private final Vec3 sphereNormal;
    private final Vec3 uAxis;
    private final Vec3 vAxis;
    private final UnitSphereDirections unitSphereDirections = new UnitSphereDirections();

    public CosineSphereDirections(Vec3 sphereCenter, Vec3 sphereNormal) {
        this.sphereCenter = Vec3.copyOf(sphereCenter);
        this.sphereNormal = requireUnitNormal(sphereNormal);
        this.uAxis = buildPerpendicularAxis(this.sphereNormal);
        this.vAxis = this.sphereNormal.cross(uAxis).normalize();
    }

    public List<Vec3> generateCosineDirections(int sampleCount) {
        if (sampleCount <= 0) {
            throw new IllegalArgumentException("sampleCount must be positive");
        }

        List<Vec3> uniformDirections = unitSphereDirections.generateUniformDirections(sampleCount);
        List<Vec3> cosineDirections = new ArrayList<>(sampleCount);

        for (Vec3 uniformDirection : uniformDirections) {
            cosineDirections.add(toCosineDirection(uniformDirection));
        }

        return cosineDirections;
    }

    private Vec3 toCosineDirection(Vec3 uniformDirection) {
        Vec3 combined = sphereNormal.add(uniformDirection);
        double combinedLength = combined.length();

        if (combinedLength <= EPSILON) {
            return Vec3.copyOf(sphereNormal);
        }

        return combined.mul(1.0 / combinedLength);
    }

    public ValidationResult validateDirections(List<Vec3> directions) {
        if (directions.isEmpty()) {
            throw new IllegalArgumentException("directions must not be empty");
        }

        int directionsOffUnitSphere = 0;
        int directionsBelowSphere = 0;
        double maxLengthDeviation = 0.0;
        double minDotWithNormal = Double.POSITIVE_INFINITY;
        double sumX = 0.0;
        double sumY = 0.0;
        double sumZ = 0.0;
        int[] rhoBinCounts = new int[MU_BIN_COUNT];
        int[] thetaRingCounts = new int[THETA_RING_COUNT];
//        int[] muBinCounts = new int[MU_BIN_COUNT];

        for (Vec3 direction : directions) {
            double length = direction.length();
            double lengthDeviation = Math.abs(length - 1.0);

            maxLengthDeviation = Math.max(maxLengthDeviation, lengthDeviation);

            if (lengthDeviation > EPSILON) {
                directionsOffUnitSphere++;
            }

            sumX += direction.x;
            sumY += direction.y;
            sumZ += direction.z;

            double mu = Utils.clamp(direction.dot(sphereNormal), -1.0, 1.0);
            minDotWithNormal = Math.min(minDotWithNormal, mu);

            if (mu < -EPSILON) {
                directionsBelowSphere++;
            }

            double rho = Utils.clamp(mu * mu, 0.0, 1.0);
            int rhoBinIndex = mapUnitIntervalToBinIndex(rho);
            rhoBinCounts[rhoBinIndex]++;

            double thetaDegrees = Math.toDegrees(Math.acos(Utils.clamp(mu, 0.0, 1.0)));
            int thetaRingIndex = (int) (thetaDegrees / THETA_RING_STEP_DEGREES);
            if (thetaRingIndex == THETA_RING_COUNT) {
                thetaRingIndex = THETA_RING_COUNT - 1;
            }
            thetaRingCounts[thetaRingIndex]++;

//            int muBinIndex = mapUnitIntervalToBinIndex(Utils.clamp(mu, 0.0, 1.0));
//            muBinCounts[muBinIndex]++;
        }

        double expectedRhoBinCount = directions.size() / (double) MU_BIN_COUNT;
        double maxRhoBinAbsoluteDeviation = 0.0;
        double maxRhoBinRelativeDeviation = 0.0;

        for (int rhoBinCount : rhoBinCounts) {
            double absoluteDeviation = Math.abs(rhoBinCount - expectedRhoBinCount);
            double relativeDeviation = absoluteDeviation / expectedRhoBinCount;

            maxRhoBinAbsoluteDeviation = Math.max(maxRhoBinAbsoluteDeviation, absoluteDeviation);
            maxRhoBinRelativeDeviation = Math.max(maxRhoBinRelativeDeviation, relativeDeviation);
        }

        double[] thetaRingAreas = new double[THETA_RING_COUNT];
        double[] thetaRingDensities = new double[THETA_RING_COUNT];
        double[] expectedThetaRingRelativeDensities = new double[THETA_RING_COUNT];
        double baseExpectedThetaRingDensity = 0.0;

        for (int i = 0; i < THETA_RING_COUNT; i++) {
            double thetaStart = Math.toRadians(i * THETA_RING_STEP_DEGREES);
            double thetaEnd = Math.toRadians((i + 1) * THETA_RING_STEP_DEGREES);
            double cosStart = Math.cos(thetaStart);
            double cosEnd = Math.cos(thetaEnd);

            thetaRingAreas[i] = 2.0 * Math.PI * (cosStart - cosEnd);
            thetaRingDensities[i] = thetaRingCounts[i] / thetaRingAreas[i];

            double expectedDensity = (cosStart + cosEnd) / 2.0;
            if (i == 0) {
                baseExpectedThetaRingDensity = expectedDensity;
            }

            expectedThetaRingRelativeDensities[i] = expectedDensity / baseExpectedThetaRingDensity;
        }

//        double[] expectedMuBinCounts = new double[MU_BIN_COUNT];
//        double maxMuBinAbsoluteDeviation = 0.0;
//        double maxMuBinRelativeDeviation = 0.0;
//
//        for (int i = 0; i < MU_BIN_COUNT; i++) {
//            double binStart = i / (double) MU_BIN_COUNT;
//            double binEnd = (i + 1) / (double) MU_BIN_COUNT;
//            double expectedCount = directions.size() * (binEnd * binEnd - binStart * binStart);
//
//            expectedMuBinCounts[i] = expectedCount;
//
//            double absoluteDeviation = Math.abs(muBinCounts[i] - expectedCount);
//            double relativeDeviation = absoluteDeviation / expectedCount;
//
//            maxMuBinAbsoluteDeviation = Math.max(maxMuBinAbsoluteDeviation, absoluteDeviation);
//            maxMuBinRelativeDeviation = Math.max(maxMuBinRelativeDeviation, relativeDeviation);
//        }

        double meanResultantLength = Math.sqrt(sumX * sumX + sumY * sumY + sumZ * sumZ) / directions.size();

        return new ValidationResult(
                directionsOffUnitSphere,
                directionsBelowSphere,
                maxLengthDeviation,
                minDotWithNormal,
                meanResultantLength,
                rhoBinCounts,
                expectedRhoBinCount,
                maxRhoBinAbsoluteDeviation,
                maxRhoBinRelativeDeviation,
                thetaRingCounts,
                thetaRingAreas,
                thetaRingDensities,
                expectedThetaRingRelativeDensities
        );
    }

    public void printReport(List<Vec3> directions, int previewCount) {
        System.out.println("Косинусное распределение плотности вероятности");
        System.out.printf("C = %s%n", sphereCenter);
        System.out.printf("N = %s%n", sphereNormal);
        System.out.printf("U = %s%n", uAxis);
        System.out.printf("V = %s%n", vAxis);
        System.out.printf("Сгенерировано направлений: %d%n", directions.size());

        System.out.println();
        System.out.println("Первые направления:");

        for (int i = 0; i < Math.min(previewCount, directions.size()); i++) {
            System.out.printf("%d. %s%n", i + 1, directions.get(i));
        }

        System.out.println();
    }

    public void printValidationReport(ValidationResult validation) {
        System.out.println("Проверка:");
        System.out.printf("Направлений вне единичной сферы: %d%n", validation.directionsOffUnitSphere);
        System.out.printf("Направлений ниже касательной плоскости: %d%n", validation.directionsBelowHemisphere);
        System.out.printf("Макс. отклонение длины: %.12f%n", validation.maxLengthDeviation);
        System.out.printf("Мин. dot(dir, N): %.12f%n", validation.minDotWithNormal);
        System.out.printf("Средняя длина результирующего вектора: %.12f%n", validation.meanResultantLength);
        //System.out.printf("Ожидаемое число в каждом rho-бине: %.2f%n", validation.expectedRhoBinCount);
        //System.out.printf("Фактические числа по rho-бинам: %s%n", Arrays.toString(validation.rhoBinCounts));
        //System.out.printf("Макс. абсолютное отклонение по rho-бинам: %.2f%n", validation.maxRhoBinAbsoluteDeviation);
        //System.out.printf("Макс. относительное отклонение по rho-бинам: %.6f%n", validation.maxRhoBinRelativeDeviation);
        System.out.println("Плотность по сферическим кольцам:");
        for (int i = 0; i < THETA_RING_COUNT; i++) {
            double thetaStart = i * THETA_RING_STEP_DEGREES;
            double thetaEnd = (i + 1) * THETA_RING_STEP_DEGREES;
            double relativeDensity = validation.thetaRingDensities[i] / validation.thetaRingDensities[0];

            System.out.printf(
                    "%.0f-%.0f°: count=%d, area=%.6f, density=%.6f, relative=%.6f, expectedRelative=%.6f%n",
                    thetaStart,
                    thetaEnd,
                    validation.thetaRingCounts[i],
                    validation.thetaRingAreas[i],
                    validation.thetaRingDensities[i],
                    relativeDensity,
                    validation.expectedThetaRingRelativeDensities[i]
            );
        }
//        System.out.printf("Ожидаемые числа по mu-бинам: %s%n", formatDoubleArray(validation.expectedMuBinCounts));
//        System.out.printf("Фактические числа по mu-бинам: %s%n", Arrays.toString(validation.muBinCounts));
//        System.out.printf("Макс. абсолютное отклонение по mu-бинам: %.2f%n", validation.maxMuBinAbsoluteDeviation);
//        System.out.printf("Макс. относительное отклонение по mu-бинам: %.6f%n", validation.maxMuBinRelativeDeviation);
        System.out.println();
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

    private static int mapUnitIntervalToBinIndex(double value) {
        int binIndex = (int) (value * MU_BIN_COUNT);

        if (binIndex == MU_BIN_COUNT) {
            return MU_BIN_COUNT - 1;
        }

        return binIndex;
    }

    private static String formatDoubleArray(double[] values) {
        StringBuilder builder = new StringBuilder("[");

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }

            builder.append(String.format("%.2f", values[i]));
        }

        builder.append("]");
        return builder.toString();
    }

    public static final class ValidationResult {
        private final int directionsOffUnitSphere;
        private final int directionsBelowHemisphere;
        private final double maxLengthDeviation;
        private final double minDotWithNormal;
        private final double meanResultantLength;
        private final int[] rhoBinCounts;
        private final double expectedRhoBinCount;
        private final double maxRhoBinAbsoluteDeviation;
        private final double maxRhoBinRelativeDeviation;
        private final int[] thetaRingCounts;
        private final double[] thetaRingAreas;
        private final double[] thetaRingDensities;
        private final double[] expectedThetaRingRelativeDensities;
//        private final int[] muBinCounts;
//        private final double[] expectedMuBinCounts;
//        private final double maxMuBinAbsoluteDeviation;
//        private final double maxMuBinRelativeDeviation;

        private ValidationResult(
                int directionsOffUnitSphere,
                int directionsBelowHemisphere,
                double maxLengthDeviation,
                double minDotWithNormal,
                double meanResultantLength,
                int[] rhoBinCounts,
                double expectedRhoBinCount,
                double maxRhoBinAbsoluteDeviation,
                double maxRhoBinRelativeDeviation,
                int[] thetaRingCounts,
                double[] thetaRingAreas,
                double[] thetaRingDensities,
                double[] expectedThetaRingRelativeDensities
        ) {
            this.directionsOffUnitSphere = directionsOffUnitSphere;
            this.directionsBelowHemisphere = directionsBelowHemisphere;
            this.maxLengthDeviation = maxLengthDeviation;
            this.minDotWithNormal = minDotWithNormal;
            this.meanResultantLength = meanResultantLength;
            this.rhoBinCounts = Arrays.copyOf(rhoBinCounts, rhoBinCounts.length);
            this.expectedRhoBinCount = expectedRhoBinCount;
            this.maxRhoBinAbsoluteDeviation = maxRhoBinAbsoluteDeviation;
            this.maxRhoBinRelativeDeviation = maxRhoBinRelativeDeviation;
            this.thetaRingCounts = Arrays.copyOf(thetaRingCounts, thetaRingCounts.length);
            this.thetaRingAreas = Arrays.copyOf(thetaRingAreas, thetaRingAreas.length);
            this.thetaRingDensities = Arrays.copyOf(thetaRingDensities, thetaRingDensities.length);
            this.expectedThetaRingRelativeDensities =
                    Arrays.copyOf(expectedThetaRingRelativeDensities, expectedThetaRingRelativeDensities.length);
//            this.muBinCounts = Arrays.copyOf(muBinCounts, muBinCounts.length);
//            this.expectedMuBinCounts = Arrays.copyOf(expectedMuBinCounts, expectedMuBinCounts.length);
//            this.maxMuBinAbsoluteDeviation = maxMuBinAbsoluteDeviation;
//            this.maxMuBinRelativeDeviation = maxMuBinRelativeDeviation;
        }
    }
}
