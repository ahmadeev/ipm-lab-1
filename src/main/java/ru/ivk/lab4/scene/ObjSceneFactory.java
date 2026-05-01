package ru.ivk.lab4.scene;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.core.Camera;
import ru.ivk.lab4.core.ColorRgb;
import ru.ivk.lab4.core.RenderSettings;
import ru.ivk.lab4.geometry.Triangle;
import ru.ivk.lab4.io.ObjParser;
import ru.ivk.lab4.material.Material;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ObjSceneFactory {
    private ObjSceneFactory() {
    }

    public static RenderJob create(Path objPath, RenderSettings settings) throws IOException {
        Material importedMaterial = Material.mixed(
                new ColorRgb(0.58, 0.58, 0.54),
                new ColorRgb(0.18, 0.18, 0.18)
        );
        List<Triangle> triangles = new ArrayList<>(new ObjParser().parse(objPath, importedMaterial));

        Material floor = Material.diffuse(new ColorRgb(0.65, 0.65, 0.62));
        Material light = Material.light(new ColorRgb(9.0, 8.4, 6.8));

        addQuad(triangles, new Vec3(-4, -1, -4), new Vec3(4, -1, -4), new Vec3(4, -1, 4), new Vec3(-4, -1, 4), floor);
        addQuad(triangles, new Vec3(-1.2, 3.2, -0.8), new Vec3(1.2, 3.2, -0.8), new Vec3(1.2, 3.2, 0.8), new Vec3(-1.2, 3.2, 0.8), light);

        double aspectRatio = settings.getWidth() / (double) settings.getHeight();
        Camera camera = new Camera(
                new Vec3(0.0, 1.0, -6.0),
                new Vec3(0.0, 0.4, 0.0),
                new Vec3(0.0, 1.0, 0.0),
                45.0,
                aspectRatio
        );

        return new RenderJob(new Scene(triangles), camera);
    }

    private static void addQuad(List<Triangle> triangles, Vec3 v0, Vec3 v1, Vec3 v2, Vec3 v3, Material material) {
        triangles.add(new Triangle(v0, v1, v2, material));
        triangles.add(new Triangle(v0, v2, v3, material));
    }
}
