package ru.ivk.lab2;

import java.util.Random;

// MonteCarloIntegration
public class Main {
    private static final Random random = new Random(123456);

    private static final class PowerDensity {
        private final double a;
        private final double b;
        private final int power;
        private final double normalization;
        private final double lowerCdfTerm;
        private final double cdfRange;

        private PowerDensity(double a, double b, int power) {
            this.a = a;
            this.b = b;
            this.power = power;
            this.normalization = integralOfPower(a, b, power);
            this.lowerCdfTerm = Math.pow(a, power + 1);
            this.cdfRange = Math.pow(b, power + 1) - lowerCdfTerm;
        }

        private double pdf(double x) {
            return Math.pow(x, power) / normalization;
        }

        private double sample() {
            double u = random.nextDouble();

            return Math.pow(lowerCdfTerm + u * cdfRange, 1.0 / (power + 1));
        }
    }

    private static double f(double x) {
        return x * x;
    }

    private static double integralOfPower(double a, double b, int power) {
        return (Math.pow(b, power + 1) - Math.pow(a, power + 1)) / (power + 1);
    }

    private static double analyticalIntegral(double a, double b) {
        return integralOfPower(a, b, 2);
    }

    private static double sampleUniform(double a, double b) {
        return a + (b - a) * random.nextDouble();
    }

    private static double simpleMC(double a, double b, int sampleCount) {
        double sum = 0;

        for (int i = 0; i < sampleCount; i++) {
            double x = sampleUniform(a, b);
            sum += f(x);
        }

        return (b - a) * sum / sampleCount;
    }

    private static double stratifiedMC(double a, double b, int sampleCount, double step) {
        int strata = (int) ((b - a) / step); // промежутков
        double result = 0;

        if (sampleCount < strata) {
            throw new IllegalArgumentException("sampleCount must be at least the number of strata");
        }

        strata = (int) Math.round((b - a) / step);

        int baseSamplesPerStratum = sampleCount / strata;
        int remainder = sampleCount % strata;

        for (int i = 0; i < strata; i++) {
            double left = a + i * step;
            double right = (i == strata - 1) ? b : left + step;
            int currentSamples = baseSamplesPerStratum + (i < remainder ? 1 : 0);

            double sum = 0;

            for (int j = 0; j < currentSamples; j++) {
                double x = sampleUniform(left, right);
                sum += f(x);
            }

            result += (right - left) * sum / currentSamples;
        }

        return result;
    }

    private static double importanceSampling(PowerDensity density, int sampleCount) {
        double sum = 0;

        for (int i = 0; i < sampleCount; i++) {
            double x = density.sample();
            sum += f(x) / density.pdf(x);
        }

        return sum / sampleCount;
    }

    private static double balanceWeight(PowerDensity current, PowerDensity other, double x) {
        double currentPdf = current.pdf(x);
        double otherPdf = other.pdf(x);

        return currentPdf / (currentPdf + otherPdf);
    }

    private static double powerWeight(PowerDensity current, PowerDensity other, double x, int beta) {
        double currentPdf = Math.pow(current.pdf(x), beta);
        double otherPdf = Math.pow(other.pdf(x), beta);

        return currentPdf / (currentPdf + otherPdf);
    }

    private static double multipleImportanceSamplingBalanced(
            int firstSampleCount,
            int secondSampleCount,
            PowerDensity firstDensity,
            PowerDensity secondDensity
    ) {
        double firstSum = 0;

        for (int i = 0; i < firstSampleCount; i++) {
            double x = firstDensity.sample();
            double weight = balanceWeight(firstDensity, secondDensity, x);
            firstSum += weight * f(x) / firstDensity.pdf(x);
        }

        firstSum /= firstSampleCount;

        double secondSum = 0;

        for (int i = 0; i < secondSampleCount; i++) {
            double x = secondDensity.sample();
            double weight = balanceWeight(secondDensity, firstDensity, x);
            secondSum += weight * f(x) / secondDensity.pdf(x);
        }

        secondSum /= secondSampleCount;

        return firstSum + secondSum;
    }

    private static double multipleImportanceSamplingPower(
            int firstSampleCount,
            int secondSampleCount,
            PowerDensity firstDensity,
            PowerDensity secondDensity,
            int beta
    ) {
        double firstSum = 0;

        for (int i = 0; i < firstSampleCount; i++) {
            double x = firstDensity.sample();
            double weight = powerWeight(firstDensity, secondDensity, x, beta);
            firstSum += weight * f(x) / firstDensity.pdf(x);
        }

        firstSum /= firstSampleCount;

        double secondSum = 0;

        for (int i = 0; i < secondSampleCount; i++) {
            double x = secondDensity.sample();
            double weight = powerWeight(secondDensity, firstDensity, x, beta);
            secondSum += weight * f(x) / secondDensity.pdf(x);
        }

        secondSum /= secondSampleCount;

        return firstSum + secondSum;
    }

    private static double russianRoulette(double a, double b, int sampleCount, double survivalProbability) {
        double sum = 0;

        for (int i = 0; i < sampleCount; i++) {
            double x = sampleUniform(a, b);

            if (random.nextDouble() <= survivalProbability) {
                sum += f(x) / survivalProbability;
            }
        }

        return (b - a) * sum / sampleCount;
    }

    private static void printHeader() {
        System.out.println("==================================================================================================================");
        System.out.printf("%-30s %10s %16s %18s %18s %18s%n",
                "Method",
                "N",
                "Exact",
                "Value",
                "Error",
                "Est. Error");
        System.out.println("==================================================================================================================");
    }

    private static void printRow(String method, int sampleCount, double exact, double value) {
        double error = Math.abs(value - exact);
        double estimatedError = exact / Math.sqrt(sampleCount);

        System.out.printf("%-30s %10d %16.10f %18.10f %18.10f %18.10f%n",
                method,
                sampleCount,
                exact,
                value,
                error,
                estimatedError);
    }

    public static void main(String[] args) {
        double a = 2;
        double b = 5;

        int[] samples = {100, 1_000, 10_000, 100_000};

        PowerDensity p1 = new PowerDensity(a, b, 1);
        PowerDensity p2 = new PowerDensity(a, b, 2);
        PowerDensity p3 = new PowerDensity(a, b, 3);

        double exact = analyticalIntegral(a, b);

        System.out.printf("Точный интеграл: %f%n%n", exact);

        printHeader();

        for (int sampleCount : samples) {
            int firstMisSamples = sampleCount / 2;
            int secondMisSamples = sampleCount - firstMisSamples;
            int N = sampleCount;

            double simple = simpleMC(a, b, sampleCount);
            double strat1 = stratifiedMC(a, b, sampleCount, 1.0);
            double strat05 = stratifiedMC(a, b, sampleCount, 0.5);
            double imp1 = importanceSampling(p1, sampleCount);
            double imp2 = importanceSampling(p2, sampleCount);
            double imp3 = importanceSampling(p3, sampleCount);
            double multi1 = multipleImportanceSamplingBalanced(firstMisSamples, secondMisSamples, p1, p3);
            double multi2 = multipleImportanceSamplingPower(firstMisSamples, secondMisSamples, p1, p3, 2);
            double rr05 = russianRoulette(a, b, sampleCount, 0.5);
            double rr075 = russianRoulette(a, b, sampleCount, 0.75);
            double rr095 = russianRoulette(a, b, sampleCount, 0.95);

            System.out.printf("Оценка погрешности: %.6f%n", exact / Math.sqrt(N));

            printRow("Simple MC", sampleCount, exact, simple);
            printRow("Stratified (step=1)", sampleCount, exact, strat1);
            printRow("Stratified (step=0.5)", sampleCount, exact, strat05);
            printRow("Importance (p(x)=x)", sampleCount, exact, imp1);
            printRow("Importance (p(x)=x^2)", sampleCount, exact, imp2);
            printRow("Importance (p(x)=x^3)", sampleCount, exact, imp3);
            printRow("MIS balance", sampleCount, exact, multi1);
            printRow("MIS power(beta=2)", sampleCount, exact, multi2);
            printRow("Russian roulette (R=0.5)", sampleCount, exact, rr05);
            printRow("Russian roulette (R=0.75)", sampleCount, exact, rr075);
            printRow("Russian roulette (R=0.95)", sampleCount, exact, rr095);

            System.out.println("------------------------------------------------------------------------------------------------------------------");
        }
    }
}
