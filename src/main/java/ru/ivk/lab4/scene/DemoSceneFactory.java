package ru.ivk.lab4.scene;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.core.Camera;
import ru.ivk.lab4.core.ColorRgb;
import ru.ivk.lab4.core.RenderSettings;
import ru.ivk.lab4.geometry.Triangle;
import ru.ivk.lab4.material.Material;

import java.util.ArrayList;
import java.util.List;

public final class DemoSceneFactory {
    private DemoSceneFactory() {
    }

    public static RenderJob create(RenderSettings settings) {
        List<Triangle> triangles = new ArrayList<>();

        Material floor = Material.diffuse(new ColorRgb(0.72, 0.72, 0.68));
        Material leftWall = Material.diffuse(new ColorRgb(0.75, 0.22, 0.18));
        Material backWall = Material.diffuse(new ColorRgb(0.18, 0.34, 0.72));
        Material cube = Material.mixed(new ColorRgb(0.28, 0.42, 0.28), new ColorRgb(0.42, 0.42, 0.42));
        Material light = Material.light(new ColorRgb(8.0, 7.2, 5.6));

        addQuad(triangles, new Vec3(-3, 0, -3), new Vec3(3, 0, -3), new Vec3(3, 0, 3), new Vec3(-3, 0, 3), floor);
        addQuad(triangles, new Vec3(-3, 0, -3), new Vec3(-3, 0, 3), new Vec3(-3, 3, 3), new Vec3(-3, 3, -3), leftWall);
        addQuad(triangles, new Vec3(-3, 0, 3), new Vec3(3, 0, 3), new Vec3(3, 3, 3), new Vec3(-3, 3, 3), backWall);
        addCube(triangles, new Vec3(-0.7, 0.0, -0.2), new Vec3(0.7, 1.4, 1.2), cube);
        addQuad(triangles, new Vec3(-0.8, 2.85, 0.1), new Vec3(0.8, 2.85, 0.1), new Vec3(0.8, 2.85, 1.1), new Vec3(-0.8, 2.85, 1.1), light);

        double aspectRatio = settings.getWidth() / (double) settings.getHeight();
        // x -- вправо, y -- вверх, z -- вглубь
        Camera camera = new Camera(
                new Vec3(3.0, 2.35, -5.0), // 0.0 (left/right), 1.35 (down/up), -5.0 (close/far)
                new Vec3(0.0, 1.05, 0.5),
                new Vec3(0.0, 1.0, 0.0), // если -y, то изображение перевернуто
                42.0,
                aspectRatio
        );

        return new RenderJob(new Scene(triangles), camera);
    }

    private static void addQuad(List<Triangle> triangles, Vec3 v0, Vec3 v1, Vec3 v2, Vec3 v3, Material material) {
        triangles.add(new Triangle(v0, v1, v2, material));
        triangles.add(new Triangle(v0, v2, v3, material));
    }

    private static void addCube(List<Triangle> triangles, Vec3 min, Vec3 max, Material material) {
        Vec3 p000 = new Vec3(min.x, min.y, min.z);
        Vec3 p001 = new Vec3(min.x, min.y, max.z);
        Vec3 p010 = new Vec3(min.x, max.y, min.z);
        Vec3 p011 = new Vec3(min.x, max.y, max.z);
        Vec3 p100 = new Vec3(max.x, min.y, min.z);
        Vec3 p101 = new Vec3(max.x, min.y, max.z);
        Vec3 p110 = new Vec3(max.x, max.y, min.z);
        Vec3 p111 = new Vec3(max.x, max.y, max.z);

        addQuad(triangles, p000, p100, p110, p010, material);
        addQuad(triangles, p101, p001, p011, p111, material);
        addQuad(triangles, p001, p000, p010, p011, material);
        addQuad(triangles, p100, p101, p111, p110, material);
        addQuad(triangles, p010, p110, p111, p011, material);
        addQuad(triangles, p001, p101, p100, p000, material);
    }
}
