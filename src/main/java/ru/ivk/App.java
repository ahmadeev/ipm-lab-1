package ru.ivk;

import ru.ivk.math.Vec3;
import ru.ivk.utils.Light;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main( String[] args ) {
        // ---- Треугольник ----
        Vec3 A = new Vec3(0, 0, 0); // P_0
        Vec3 B = new Vec3(2, 0, 0); // P_1
        Vec3 C = new Vec3(0, 2, 5); // P_2

        // ---- Источники света ----
        List<Light> lights = new ArrayList<>();

        lights.add(new Light(
                new Vec3(5, -5, 2), // расположение
                new Vec3(-0.333333333333333, 0.666666666666666, -0.666666666666666), // направление
                new Vec3(1500, 1500, 0) // I_0
        ));

        lights.add(new Light(
                new Vec3(0, 2, 15), // расположение
                new Vec3(0, 0, 1), // направление
                new Vec3(1000, 1000, 1000) // I_0
        ));

        // ---- Наблюдатель ----
        Vec3 observer = new Vec3(2, 2, 2);

        // ---- Параметры материала ----
        double kd = 0.5; // коэффициент диффузного отражения
        double ks = 0.5; // коэффициент зеркального отражения
        double ke = 200; // коэффициент, определяющий ширину блика

        Vec3 K = new Vec3(1, 0, 0); // цвет поверхности

        // ---- Локальные точки ----
        Double[] localXs = new Double[]{-100.0, 0.0, 2.0, 5.0, 10.0};
        Double[] localYs = {-100.0, 0.0, 2.0, 15.0, 100.0};

        Double[][] localPoints = generateSeries(localXs, localYs);

        compute(
                localPoints,
                true,
                lights,
                A, B, C,
                observer,
                K, kd, ks, ke
        );

/*        // ---- Глобальные точки ----
        double[][] globalPoints = {
                {-100, -37.1391, -92.8477},
                {0, 0, 0},
                {0, 5.5709, 13.9272},
                {0, 0.7428, 1.8570},
                {100, 37.1391, 92.8477}
        };

        compute(
                globalPoints,
                false,
                lights,
                A, B, C,
                observer,
                K, kd, ks, ke
        );*/
    }

    private static void compute(
            Double[][] points,
            boolean isLocal,
            List<Light> lights,
            Vec3 A,
            Vec3 B,
            Vec3 C,
            Vec3 observer,
            Vec3 K,
            double kd,
            double ks,
            double ke
    ) {
        // ---- Нормаль ----
        Vec3 N = C.subtract(A).cross(B.subtract(A)).normalize();

        for (Double[] lp : points) {
            double u = lp[0];
            double v = lp[1];

            Vec3 P = isLocal ? localToGlobal(A, B, C, u, v) : new Vec3(lp[0], lp[1], lp[2]);

            Vec3 V = P.subtract(observer).normalize();

            Vec3 totalBrightness = new Vec3(0,0,0);
            Vec3 totalE = new Vec3(0, 0, 0);

            for (Light light : lights) {
                Vec3 s = P.subtract(light.position);
                double R = s.length();
                s = s.normalize();

                double cosAlpha = Math.max(0, N.dot(s));
                double cosTheta = Math.max(0, s.dot(light.direction));

                Vec3 I = light.intensity.multiply(cosTheta);

                Vec3 E = I.multiply(cosAlpha / (R * R));
                totalE = totalE.add(E);

                double brdf = computeBRDF(N, s, V, kd, ks, ke);

                Vec3 brightnessFactor = E.multiply(brdf);

                totalBrightness = totalBrightness.add(
                        new Vec3(
                                K.x * brightnessFactor.x,
                                K.y * brightnessFactor.y,
                                K.z * brightnessFactor.z
                        )
                );
            }

            totalBrightness = totalBrightness.multiply(1 / Math.PI);

            if (isLocal) {
                System.out.printf("Точка (u=%.4f, v=%.4f)%n", u, v);
            }

            System.out.printf("Глобальные координаты: %s%n", P);
            System.out.printf("Освещенность: %s%n", totalE);
            System.out.printf("Яркость RGB: %s%n", totalBrightness);
            System.out.println("---------------------------------");
        }
    }

    public static Vec3 localToGlobal(Vec3 A, Vec3 B, Vec3 C, double u, double v) {
        return A.add(
                B.subtract(A).normalize().multiply(u)
        ).add(
                C.subtract(A).normalize().multiply(v)
        );
    }

    public static double computeBRDF(Vec3 N, Vec3 s, Vec3 V, double kd, double ks, double ke) {
        double diffuse = kd * Math.max(0, N.dot(s));

        Vec3 h = s.add(V).normalize();

        double specular = ks * Math.pow(Math.max(0, N.dot(h)), ke);

        return diffuse + specular;
    }

    private static Double[][] generateSeries(Double[] xs, Double[] ys) {
        List<Double[]> result = new ArrayList<>();

        for (double x : xs) {
            for (double y : ys) {
                result.add(new Double[]{x, y});
            }
        }

        return result.toArray(new Double[0][2]);
    }
}
