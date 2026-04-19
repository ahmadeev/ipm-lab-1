package ru.ivk.lab3;

import ru.ivk.common.math.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class UnitSphereDirections {
    private static final Random random = new Random(123456L);
    private static final double EPSILON = 1e-6;
    private static final int MU_BIN_COUNT = 10;

    private final Vec3 uAxis = new Vec3(1, 0, 0);
    private final Vec3 vAxis = new Vec3(0, 1, 0);
    private final Vec3 nAxis = new Vec3(0, 0, 1);

    public List<Vec3> generateUniformDirections(int sampleCount) {
        if (sampleCount <= 0) {
            throw new IllegalArgumentException("sampleCount must be positive");
        }

        List<Vec3> directions = new ArrayList<>(sampleCount);

        for (int i = 0; i < sampleCount; i++) {
            directions.add(sampleUniformDirection());
        }

        return directions;
    }

    private Vec3 sampleUniformDirection() {
        double xiTheta = random.nextDouble();
        double xiPhi = random.nextDouble();

        double phi = 2.0 * Math.PI * xiPhi;
        double cosTheta = clamp(2.0 * xiTheta - 1.0, -1.0, 1.0);
        double theta = Math.acos(cosTheta);
        double sinTheta = Math.sin(theta);

        return uAxis.mul(sinTheta * Math.cos(phi))
                .add(vAxis.mul(sinTheta * Math.sin(phi)))
                .add(nAxis.mul(cosTheta));
    }

    public ValidationResult validateDirections(List<Vec3> directions) {
        if (directions.isEmpty()) {
            throw new IllegalArgumentException("directions must not be empty");
        }

        int directionsOffUnitSphere = 0;
        double maxLengthDeviation = 0.0;
        double sumX = 0.0;
        double sumY = 0.0;
        double sumZ = 0.0;
        int[] muBinCounts = new int[MU_BIN_COUNT];

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

            double mu = clamp(direction.dot(nAxis), -1.0, 1.0);
            int muBinIndex = mapMuToBinIndex(mu);
            muBinCounts[muBinIndex]++;
        }

        double expectedMuBinCount = directions.size() / (double) MU_BIN_COUNT;
        double maxMuBinAbsoluteDeviation = 0.0;
        double maxMuBinRelativeDeviation = 0.0;

        for (int muBinCount : muBinCounts) {
            double absoluteDeviation = Math.abs(muBinCount - expectedMuBinCount);
            double relativeDeviation = absoluteDeviation / expectedMuBinCount;

            maxMuBinAbsoluteDeviation = Math.max(maxMuBinAbsoluteDeviation, absoluteDeviation);
            maxMuBinRelativeDeviation = Math.max(maxMuBinRelativeDeviation, relativeDeviation);
        }

        double meanResultantLength = Math.sqrt(sumX * sumX + sumY * sumY + sumZ * sumZ) / directions.size();

        return new ValidationResult(
                directionsOffUnitSphere,
                maxLengthDeviation,
                meanResultantLength,
                expectedMuBinCount,
                muBinCounts,
                maxMuBinAbsoluteDeviation,
                maxMuBinRelativeDeviation
        );
    }

    public void printReport(List<Vec3> directions, int previewCount) {
        System.out.println("Uniform directions on unit sphere");
        System.out.printf("U = %s%n", uAxis);
        System.out.printf("V = %s%n", vAxis);
        System.out.printf("N = %s%n", nAxis);
        System.out.printf("Generated directions: %d%n", directions.size());

        System.out.println();
        System.out.println("First directions:");

        for (int i = 0; i < Math.min(previewCount, directions.size()); i++) {
            System.out.printf("%d. %s%n", i + 1, directions.get(i));
        }

        System.out.println();
    }

    public void printValidationReport(ValidationResult validation) {
        System.out.println("Validation:");
        System.out.printf("Directions off unit sphere: %d%n", validation.directionsOffUnitSphere);
        System.out.printf("Max length deviation from 1: %.12f%n", validation.maxLengthDeviation);
        System.out.printf("Mean resultant length: %.12f%n", validation.meanResultantLength);
        System.out.printf("Expected count per mu-bin: %.2f%n", validation.expectedMuBinCount);
        System.out.printf("mu-bin counts: %s%n", Arrays.toString(validation.muBinCounts));
        System.out.printf("Max absolute mu-bin deviation: %.2f%n", validation.maxMuBinAbsoluteDeviation);
        System.out.printf("Max relative mu-bin deviation: %.6f%n", validation.maxMuBinRelativeDeviation);
        System.out.println();
    }

    private static int mapMuToBinIndex(double mu) {
        int muBinIndex = (int) (((mu + 1.0) * 0.5) * MU_BIN_COUNT);

        if (muBinIndex == MU_BIN_COUNT) {
            return MU_BIN_COUNT - 1;
        }

        return muBinIndex;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static final class ValidationResult {
        private final int directionsOffUnitSphere;
        private final double maxLengthDeviation;
        private final double meanResultantLength;
        private final double expectedMuBinCount;
        private final int[] muBinCounts;
        private final double maxMuBinAbsoluteDeviation;
        private final double maxMuBinRelativeDeviation;

        private ValidationResult(
                int directionsOffUnitSphere,
                double maxLengthDeviation,
                double meanResultantLength,
                double expectedMuBinCount,
                int[] muBinCounts,
                double maxMuBinAbsoluteDeviation,
                double maxMuBinRelativeDeviation
        ) {
            this.directionsOffUnitSphere = directionsOffUnitSphere;
            this.maxLengthDeviation = maxLengthDeviation;
            this.meanResultantLength = meanResultantLength;
            this.expectedMuBinCount = expectedMuBinCount;
            this.muBinCounts = Arrays.copyOf(muBinCounts, muBinCounts.length);
            this.maxMuBinAbsoluteDeviation = maxMuBinAbsoluteDeviation;
            this.maxMuBinRelativeDeviation = maxMuBinRelativeDeviation;
        }
    }
}
