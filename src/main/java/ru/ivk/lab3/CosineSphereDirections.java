package ru.ivk.lab3;

import ru.ivk.common.math.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CosineSphereDirections {
    private static final double EPSILON = 1e-6;
    private static final int MU_BIN_COUNT = 10;
    private static final double EXPECTED_MEAN_MU = 2.0 / 3.0;

    private final Vec3 sphereCenter;
    private final Vec3 sphereNormal;
    private final Vec3 uAxis;
    private final Vec3 vAxis;
    private final UnitSphereDirections unitSphereDirections = new UnitSphereDirections();

    public CosineSphereDirections(Vec3 sphereCenter, Vec3 sphereNormal) {
        this.sphereCenter = copyOf(sphereCenter);
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

        // The antipodal case P = -N is vanishingly rare; keep the result in the hemisphere if it happens.
        if (combinedLength <= EPSILON) {
            return copyOf(sphereNormal);
        }

        return combined.mul(1.0 / combinedLength);
    }

    public ValidationResult validateDirections(List<Vec3> directions) {
        if (directions.isEmpty()) {
            throw new IllegalArgumentException("directions must not be empty");
        }

        int directionsOffUnitSphere = 0;
        int directionsBelowHemisphere = 0;
        double maxLengthDeviation = 0.0;
        double minDotWithNormal = Double.POSITIVE_INFINITY;
        double sumMu = 0.0;
        int[] muBinCounts = new int[MU_BIN_COUNT];

        for (Vec3 direction : directions) {
            double length = direction.length();
            double lengthDeviation = Math.abs(length - 1.0);

            maxLengthDeviation = Math.max(maxLengthDeviation, lengthDeviation);

            if (lengthDeviation > EPSILON) {
                directionsOffUnitSphere++;
            }

            double mu = clamp(direction.dot(sphereNormal), -1.0, 1.0);
            minDotWithNormal = Math.min(minDotWithNormal, mu);
            sumMu += mu;

            if (mu < -EPSILON) {
                directionsBelowHemisphere++;
            }

            int muBinIndex = mapMuToBinIndex(clamp(mu, 0.0, 1.0));
            muBinCounts[muBinIndex]++;
        }

        double[] expectedMuBinCounts = new double[MU_BIN_COUNT];
        double maxMuBinAbsoluteDeviation = 0.0;
        double maxMuBinRelativeDeviation = 0.0;

        for (int i = 0; i < MU_BIN_COUNT; i++) {
            double binStart = i / (double) MU_BIN_COUNT;
            double binEnd = (i + 1) / (double) MU_BIN_COUNT;
            double expectedCount = directions.size() * (binEnd * binEnd - binStart * binStart);

            expectedMuBinCounts[i] = expectedCount;

            double absoluteDeviation = Math.abs(muBinCounts[i] - expectedCount);
            double relativeDeviation = absoluteDeviation / expectedCount;

            maxMuBinAbsoluteDeviation = Math.max(maxMuBinAbsoluteDeviation, absoluteDeviation);
            maxMuBinRelativeDeviation = Math.max(maxMuBinRelativeDeviation, relativeDeviation);
        }

        double meanMu = sumMu / directions.size();
        double meanMuDeviationFromExpected = Math.abs(meanMu - EXPECTED_MEAN_MU);

        return new ValidationResult(
                directionsOffUnitSphere,
                directionsBelowHemisphere,
                maxLengthDeviation,
                minDotWithNormal,
                meanMu,
                meanMuDeviationFromExpected,
                muBinCounts,
                expectedMuBinCounts,
                maxMuBinAbsoluteDeviation,
                maxMuBinRelativeDeviation
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
        System.out.printf("Среднее mu = dot(dir, N): %.12f%n", validation.meanMu);
        System.out.printf("Отклонение среднего mu от 2/3: %.12f%n", validation.meanMuDeviationFromExpected);
        System.out.printf("Ожидаемые числа по mu-бинам: %s%n", formatDoubleArray(validation.expectedMuBinCounts));
        System.out.printf("Фактические числа по mu-бинам: %s%n", Arrays.toString(validation.muBinCounts));
        System.out.printf("Макс. абсолютное отклонение по mu-бинам: %.2f%n", validation.maxMuBinAbsoluteDeviation);
        System.out.printf("Макс. относительное отклонение по mu-бинам: %.6f%n", validation.maxMuBinRelativeDeviation);
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

    private static int mapMuToBinIndex(double mu) {
        int muBinIndex = (int) (mu * MU_BIN_COUNT);

        if (muBinIndex == MU_BIN_COUNT) {
            return MU_BIN_COUNT - 1;
        }

        return muBinIndex;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static Vec3 copyOf(Vec3 source) {
        return new Vec3(source.x, source.y, source.z);
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
        private final double meanMu;
        private final double meanMuDeviationFromExpected;
        private final int[] muBinCounts;
        private final double[] expectedMuBinCounts;
        private final double maxMuBinAbsoluteDeviation;
        private final double maxMuBinRelativeDeviation;

        private ValidationResult(
                int directionsOffUnitSphere,
                int directionsBelowHemisphere,
                double maxLengthDeviation,
                double minDotWithNormal,
                double meanMu,
                double meanMuDeviationFromExpected,
                int[] muBinCounts,
                double[] expectedMuBinCounts,
                double maxMuBinAbsoluteDeviation,
                double maxMuBinRelativeDeviation
        ) {
            this.directionsOffUnitSphere = directionsOffUnitSphere;
            this.directionsBelowHemisphere = directionsBelowHemisphere;
            this.maxLengthDeviation = maxLengthDeviation;
            this.minDotWithNormal = minDotWithNormal;
            this.meanMu = meanMu;
            this.meanMuDeviationFromExpected = meanMuDeviationFromExpected;
            this.muBinCounts = Arrays.copyOf(muBinCounts, muBinCounts.length);
            this.expectedMuBinCounts = Arrays.copyOf(expectedMuBinCounts, expectedMuBinCounts.length);
            this.maxMuBinAbsoluteDeviation = maxMuBinAbsoluteDeviation;
            this.maxMuBinRelativeDeviation = maxMuBinRelativeDeviation;
        }
    }
}
