package ru.ivk.lab4.scene;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.ivk.lab4.core.Camera;

@Getter
@RequiredArgsConstructor
public final class RenderJob {
    private final Scene scene;
    private final Camera camera;
}
