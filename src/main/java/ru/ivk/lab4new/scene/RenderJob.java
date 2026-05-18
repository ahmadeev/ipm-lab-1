package ru.ivk.lab4new.scene;

import lombok.Getter;
import ru.ivk.lab4new.core.RenderSettings;

import java.util.Objects;

/**
 * Контейнер данных, необходимых для запуска одного задания рендера.
 */
@Getter
public final class RenderJob {
    private final RenderSettings settings;

    public RenderJob(RenderSettings settings) {
        this.settings = Objects.requireNonNull(settings, "settings");
    }
}
