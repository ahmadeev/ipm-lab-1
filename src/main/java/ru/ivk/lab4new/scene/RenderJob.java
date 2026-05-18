package ru.ivk.lab4new.scene;

import lombok.Getter;
import ru.ivk.lab4new.core.RenderSettings;

import java.util.Objects;

@Getter
public final class RenderJob {
    private final RenderSettings settings;

    public RenderJob(RenderSettings settings) {
        this.settings = Objects.requireNonNull(settings, "settings");
    }
}
