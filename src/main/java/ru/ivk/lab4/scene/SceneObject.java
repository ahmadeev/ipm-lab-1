package ru.ivk.lab4.scene;

import lombok.Getter;

import java.util.Objects;

/**
 * Логический объект сцены для группировки геометрии при постобработке.
 */
@Getter
public final class SceneObject {
    public static final SceneObject UNKNOWN = new SceneObject(-1, "unknown");

    private final int id;
    private final String name;

    public SceneObject(int id, String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
    }
}
