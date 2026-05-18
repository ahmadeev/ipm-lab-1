package ru.ivk.lab4new.scene;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4new.core.Camera;
import ru.ivk.lab4new.core.ColorRgb;
import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.geometry.Triangle;
import ru.ivk.lab4new.io.ObjParser;
import ru.ivk.lab4new.material.Material;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Создает демонстрационную сцену из OBJ-модели и базовых треугольников окружения.
 */
public final class ObjSceneFactory {
    private ObjSceneFactory() {
    }

    public static RenderJob create(RenderSettings settings) throws IOException {
        return create(settings, settings.getSceneSource() == SceneSource.PATH);
    }

    private static RenderJob create(RenderSettings settings, boolean pathMode) throws IOException {
        Material importedMaterial = Material.mixed(
                new ColorRgb(0.58, 0.58, 0.54),
                new ColorRgb(0.18, 0.18, 0.18)
        );
        List<Triangle> triangles = new ArrayList<>(pathMode
                ? loadModelFromPath(settings, importedMaterial)
                : loadModelFromResource(settings, importedMaterial));

        Material floor = Material.diffuse(new ColorRgb(0.65, 0.65, 0.62));
        Material light = Material.light(new ColorRgb(9.0, 8.4, 6.8));

        addQuad(triangles, new Vec3(-4.0, -1.0, -4.0), new Vec3(4.0, -1.0, -4.0), new Vec3(4.0, -1.0, 4.0), new Vec3(-4.0, -1.0, 4.0), floor);
        addQuad(triangles, new Vec3(-1.2, 3.2, -0.8), new Vec3(1.2, 3.2, -0.8), new Vec3(1.2, 3.2, 0.8), new Vec3(-1.2, 3.2, 0.8), light);

        double aspectRatio = (double) settings.getWidth() / settings.getHeight();
        Camera camera = new Camera(
                new Vec3(0.0, 1.0, -6.0),
                new Vec3(0.0, 0.4, 0.0),
                new Vec3(0.0, 1.0, 0.0),
                45.0,
                aspectRatio
        );

        return new RenderJob(settings, camera, new Scene(triangles));
    }

    private static List<Triangle> loadModelFromResource(RenderSettings settings, Material material) throws IOException {
        ObjParser parser = new ObjParser();
        InputStream inputStream = ObjSceneFactory.class.getClassLoader().getResourceAsStream(settings.getModelPath());

        if (inputStream != null) {
            return parser.parse(inputStream, material);
        }

        return parser.parse(Path.of("src/main/resources", settings.getModelPath()), material);
    }

    private static List<Triangle> loadModelFromPath(RenderSettings settings, Material material) throws IOException {
        ObjParser parser = new ObjParser();
        Path directPath = Path.of(settings.getModelPath());

        if (Files.exists(directPath)) {
            return parser.parse(directPath, material);
        }

        return parser.parse(directPath, material);
    }

    private static void addQuad(List<Triangle> triangles, Vec3 v0, Vec3 v1, Vec3 v2, Vec3 v3, Material material) {
        triangles.add(new Triangle(v0, v1, v2, material));
        triangles.add(new Triangle(v0, v2, v3, material));
    }
}
