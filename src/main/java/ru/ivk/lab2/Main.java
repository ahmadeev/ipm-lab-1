package ru.ivk.lab2;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

// MonteCarloIntegration
public class Main {
    private static final Random random = new Random();

    private static double f(double x) {
        return x * x;
    }

    private static double analyticalIntegral(double a, double b) {
        // ∫x^2 dx = x^3 / 3
        return (Math.pow(b, 3) - Math.pow(a, 3)) / 3.0;
    }

    private static double simpleMC(double a, double b, int N) {
        double sum = 0;

        for (int i = 0; i < N; i++) {
            double x = a + (b - a) * random.nextDouble();
            sum += f(x);
        }

        return (b - a) * sum / N;
    }

    private static double stratifiedMC(double a, double b, int N, double step) {
        int strata = (int)((b - a) / step);
        double result = 0;

        for (int i = 0; i < strata; i++) {
            double left = a + i * step;
            double right = left + step;

            double sum = 0;
            for (int j = 0; j < N / strata; j++) {
                double x = left + (right - left) * random.nextDouble();
                sum += f(x);
            }

            result += (right - left) * sum / (N / strata);
        }

        return result;
    }

    private static double importanceSampling(Function<Double, Double> p, double a, double b, int N) {
        double sum = 0;

        for (int i = 0; i < N; i++) {
            double x = a + (b - a) * random.nextDouble();
            sum += f(x) / p.apply(x);
        }

        System.out.println(sum);

        return (b - a) * sum / N;
    }

    private static double multipleImportanceSampling(
            int N1,
            int N2,
            double a,
            double b,
            Function<Double, Double> p1,
            Function<Double, Double> p2,
            Function<Double, Double> w1,
            Function<Double, Double> w2
    ) {
        double sum1 = 0;

        for (int i = 0; i < N1; i++) {
            double x = a + (b - a) * random.nextDouble();

            sum1 += w1.apply(x) * f(x) / p1.apply(x);
        }

        sum1 /= N1;

        double sum2 = 0;

        for (int i = 0; i < N2; i++) {
            double x = a + (b - a) * random.nextDouble();

            sum2 += w2.apply(x) * f(x) / p2.apply(x);
        }

        sum2 /= N2;

        return sum1 + sum2;
    }

    private static double russianRoulette(double a, double b, int N, double threshold) {
        double sum = 0;

        for (int i = 0; i < N; i++) {
            double x = a + (b - a) * random.nextDouble();

            if (random.nextDouble() < threshold) {
                sum += f(x) / threshold;
            }
        }

        return (b - a) * sum / N;
    }

    public static void main(String[] args) {
        double a = 100;
        double b = 101;

        int[] samples = {100, 1_000, 10_000, 100_000};

        Function<Integer, Double> intPowFunc = (pow) -> (Math.pow(b, pow) - Math.pow(a, pow)) / pow ;
        BiFunction<Double, Integer, Double> powFunc = (x, pow) -> Math.pow(x, pow) / intPowFunc.apply(pow + 1);

        Function<Double, Double> p1 = (x) -> powFunc.apply(x, 1);
        Function<Double, Double> p2 = (x) -> powFunc.apply(x, 2);
        Function<Double, Double> p3 = (x) -> powFunc.apply(x, 3);

        Function<Double, Double> w11 = (x) -> p1.apply(x) / (p1.apply(x) + p2.apply(x));
        Function<Double, Double> w12 = (x) -> p1.apply(x) / (p1.apply(x) + p2.apply(x));

        Function<Double, Double> w21 = (x) -> Math.pow(p1.apply(x), 2) / (Math.pow(p1.apply(x), 2) + Math.pow(p2.apply(x), 2));
        Function<Double, Double> w22 = (x) -> Math.pow(p2.apply(x), 2) / (Math.pow(p1.apply(x), 2) + Math.pow(p2.apply(x), 2));

        double exact = analyticalIntegral(a, b);

        System.out.printf("Точный интеграл: %f%n%n", exact);

        System.out.println("====================================================================================");
        System.out.printf("%-32s %-10s %-20s %-20s%n", "Method", "N", "Value", "Error");
        System.out.println("====================================================================================");

        for (int N : samples) {
            double simple = simpleMC(a, b, N);
            double strat1 = stratifiedMC(a, b, N, 1.0);
            double strat05 = stratifiedMC(a, b, N, 0.5);
            double imp1 = importanceSampling(p1, a, b, N);
            double imp2 = importanceSampling(p2, a, b, N);
            double imp3 = importanceSampling(p3, a, b, N);
            double multi1 = multipleImportanceSampling(N / 2, N / 2, a, b, p1, p3, w11, w12);
            double multi2 = multipleImportanceSampling(N / 2, N / 2, a, b, p1, p3, w21, w22);
            double rr05 = russianRoulette(a, b, N, 0.5);
            double rr075 = russianRoulette(a, b, N, 0.75);
            double rr095 = russianRoulette(a, b, N, 0.95);

            System.out.printf("%-32s %-10d %-20.10f %-20.10f%n", "Simple MC", N, simple, Math.abs(simple - exact));
            System.out.printf("%-32s %-10d %-20.10f %-20.10f%n", "Strat (step=1)", N, strat1, Math.abs(strat1 - exact));
            System.out.printf("%-32s %-10d %-20.10f %-20.10f%n", "Strat (step=0.5)", N, strat05, Math.abs(strat05 - exact));
            System.out.printf("%-32s %-10d %-20.10f %-20.10f%n", "Importance (p(x)=x)", N, imp1, Math.abs(imp1 - exact));
            System.out.printf("%-32s %-10d %-20.10f %-20.10f%n", "Importance (p(x)=x^2)", N, imp2, Math.abs(imp2 - exact));
            System.out.printf("%-32s %-10d %-20.10f %-20.10f%n", "Importance (p(x)=x^3)", N, imp3, Math.abs(imp3 - exact));
            System.out.printf("%-32s %-10d %-20.10f %-20.10f%n", "Multiple IS (1)", N, multi1, Math.abs(multi1 - exact));
            System.out.printf("%-32s %-10d %-20.10f %-20.10f%n", "Multiple IS (2)", N, multi2, Math.abs(multi2 - exact));
            System.out.printf("%-32s %-10d %-20.10f %-20.10f%n", "Russian Roulette (R=0.5)", N, rr05, Math.abs(rr05 - exact));
            System.out.printf("%-32s %-10d %-20.10f %-20.10f%n", "Russian Roulette (R=0.75)", N, rr075, Math.abs(rr075 - exact));
            System.out.printf("%-32s %-10d %-20.10f %-20.10f%n", "Russian Roulette (R=0.95)", N, rr095, Math.abs(rr095 - exact));

            System.out.println("------------------------------------------------------------------------------------");
        }
    }
}
