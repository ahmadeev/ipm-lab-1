package ru.ivk.lab1;

import ru.ivk.common.math.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main( String[] args ) {
        // ---- Треугольник ----
        Vec3 A = new Vec3(0, 0, 0); // P_0
        Vec3 B = new Vec3(2, 0, 0); // P_1
        Vec3 C = new Vec3(0, 2, 5); // P_2

        // ---- Источники света ----
        List<Light> lights = new ArrayList<>();

/*        lights.add(new Light(
                new Vec3(5, -5, 2), // расположение
                new Vec3(-0.333333333333333, 0.666666666666666, -0.666666666666666), // направление
                new Vec3(1500, 1500, 0) // I_0
        ));*/

        lights.add(new Light(
                new Vec3(0, 2, 15), // расположение
                new Vec3(0, 0, -1), // направление
                new Vec3(1000, 1000, 1000) // I_0
        ));

        // ---- Наблюдатель ----
        Vec3 observer = new Vec3(2, 2, -8);

        // ---- Параметры материала ----
        double kd = 0.5; // коэффициент диффузного отражения
        double ks = 0.5; // коэффициент зеркального отражения
        double ke = 200; // коэффициент, определяющий ширину блика

        Vec3 K = new Vec3(1, 0, 0); // цвет поверхности

        // ---- Локальные точки ----
        Double[] localXs = new Double[]{-100.0, 0.0, 2.0, 5.0, 10.0};
        Double[] localYs = {-100.0, 0.0, 2.0, 15.0, 100.0};

//        Double[] localXs = new Double[]{0.0};
//        Double[] localYs = {0.0, 15.0};

        Double[][] localPoints = generateSeries(localXs, localYs);

        compute(
                localPoints,
                true,
                lights,
                A, B, C,
                observer,
                K, kd, ks, ke
        );

        // ---- Глобальные точки ----
        Double[][] globalPoints = {
                {-100.0, -37.14, -92.85},
                {0.0, 0.0, 0.0},
                {2.0, 0.74, 1.86},
                {5.0, 5.57, 13.93},
                {10.0, 37.14, 92.85}
        };

/*        compute(
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
        Vec3 N = C.sub(A).cross(B.sub(A)).normalize();

        if (isLocal) {
            System.out.printf("| %-7s | %-7s || %-7s | %-7s | %-7s || %-28s | %-28s |%n", "u", "v", "x", "y", "z", "E (освещенность)", "L (яркость)");
            System.out.printf("|---------|---------||---------|---------|---------||------------------------------|------------------------------|%n");
        } else {
            System.out.printf("| %-7s | %-7s | %-7s || %-28s | %-28s |%n", "x", "y", "z", "E (освещенность)", "L (яркость)");
            System.out.printf("|---------|---------|---------||------------------------------|------------------------------|%n");
        }

        for (Double[] lp : points) {
            double u = lp[0];
            double v = lp[1];

            Vec3 P = isLocal ? localToGlobal(A, B, C, u, v) : new Vec3(lp[0], lp[1], lp[2]);

            Vec3 V = P.sub(observer).normalize();

            Vec3 totalBrightness = new Vec3(0,0,0);
            Vec3 totalE = new Vec3(0, 0, 0);

            for (Light light : lights) {
                Vec3 s = P.sub(light.position);
                double R = s.length();
                s = s.normalize();

                double cosAlpha = Math.max(0, N.dot(s));
                double cosTheta = Math.max(0, s.dot(light.direction));

                Vec3 I = light.intensity.mul(cosTheta);

                Vec3 E = I.mul(cosAlpha / (R * R));
                totalE = totalE.add(E);

                double brdf = computeF(N, s, V, kd, ks, ke);

                Vec3 brightnessFactor = E.mul(brdf);

//                if (N.dot(V) > 0 && N.dot(s) > 0 || N.dot(V) < 0 && N.dot(s) < 0) {
                if (s.dot(V) > 0) {
                    totalBrightness = totalBrightness.add(
                            new Vec3(
                                    K.x * brightnessFactor.x,
                                    K.y * brightnessFactor.y,
                                    K.z * brightnessFactor.z
                            )
                    );
                }
            }

            totalBrightness = totalBrightness.mul(1 / Math.PI);

            if (isLocal) {
                System.out.printf("| %-7.2f | %-7.2f || %-7.2f | %-7.2f | %-7.2f || %-28s | %-28s |%n", u, v, P.x, P.y, P.z, totalE, totalBrightness);
                System.out.printf("|---------|---------||---------|---------|---------||------------------------------|------------------------------|%n");
            } else {
                System.out.printf("| %-7.2f | %-7.2f | %-7.2f || %-28s | %-28s |%n", P.x, P.y, P.z, totalE, totalBrightness);
                System.out.printf("|---------|---------|---------||------------------------------|------------------------------|%n");
            }
        }
    }

    public static Vec3 localToGlobal(Vec3 A, Vec3 B, Vec3 C, double u, double v) {
        return A.add(
                B.sub(A).normalize().mul(u)
        ).add(
                C.sub(A).normalize().mul(v)
        );
    }

    public static double computeF(Vec3 N, Vec3 s, Vec3 V, double kd, double ks, double ke) {
        double diffuse = kd * Math.max(0, N.dot(s));

        Vec3 h = s.mul(-1).add(V.mul(-1)).normalize();

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
