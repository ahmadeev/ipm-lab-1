package ru.ivk.lab4new.scene;

import lombok.Getter;
import ru.ivk.lab4new.core.Camera;
import ru.ivk.lab4new.core.RenderSettings;
import ru.ivk.lab4new.geometry.Triangle;

import java.util.Objects;

/**
 * Контейнер данных, необходимых для запуска одного задания рендера.
 */
@Getter
public final class RenderJob {
    private final RenderSettings settings;
    private final Camera camera;
    private final Triangle triangle;

    public RenderJob(RenderSettings settings, Camera camera, Triangle triangle) {
        this.settings = Objects.requireNonNull(settings, "settings");
        this.camera = Objects.requireNonNull(camera, "camera");
        this.triangle = Objects.requireNonNull(triangle, "triangle");
    }
}
