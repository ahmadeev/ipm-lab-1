package ru.ivk.lab4new.scene;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4new.core.Camera;
import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.geometry.Triangle;

import java.util.Arrays;

/**
 * Создает демонстрационное задание для текущего этапа реализации.
 */
public final class DemoSceneFactory {
    private DemoSceneFactory() {
    }

    public static RenderJob create(RenderSettings settings) {
        double aspectRatio = (double) settings.getWidth() / settings.getHeight();
        Camera camera = new Camera(
                new Vec3(0.0, 0.0, 0.0),
                new Vec3(0.0, 0.0, -1.0),
                new Vec3(0.0, 1.0, 0.0),
                60.0,
                aspectRatio
        );
        Scene scene = new Scene(Arrays.asList(
                new Triangle(
                        new Vec3(-0.9, -0.6, -1.8),
                        new Vec3(0.9, -0.6, -1.8),
                        new Vec3(0.9, 0.6, -1.8)
                ),
                new Triangle(
                        new Vec3(-0.9, -0.6, -1.8),
                        new Vec3(0.9, 0.6, -1.8),
                        new Vec3(-0.9, 0.6, -1.8)
                )
        ));

        return new RenderJob(settings, camera, scene);
    }
}
