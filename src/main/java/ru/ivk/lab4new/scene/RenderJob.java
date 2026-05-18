package ru.ivk.lab4new.scene;

import lombok.Getter;
import ru.ivk.lab4new.core.Camera;
import ru.ivk.lab4new.core.RenderSettings;

import java.util.Objects;

/**
 * Контейнер данных, необходимых для запуска одного задания рендера.
 */
@Getter
public final class RenderJob {
    private final RenderSettings settings;
    private final Camera camera;
    private final Scene scene;

    public RenderJob(RenderSettings settings, Camera camera, Scene scene) {
        this.settings = Objects.requireNonNull(settings, "settings");
        this.camera = Objects.requireNonNull(camera, "camera");
        this.scene = Objects.requireNonNull(scene, "scene");
    }
}
