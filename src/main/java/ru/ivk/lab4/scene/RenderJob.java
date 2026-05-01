package ru.ivk.lab4.scene;

import ru.ivk.lab4.core.Camera;

public final class RenderJob {
    private final Scene scene;
    private final Camera camera;

    public RenderJob(Scene scene, Camera camera) {
        this.scene = scene;
        this.camera = camera;
    }

    public Scene getScene() {
        return scene;
    }

    public Camera getCamera() {
        return camera;
    }
}
