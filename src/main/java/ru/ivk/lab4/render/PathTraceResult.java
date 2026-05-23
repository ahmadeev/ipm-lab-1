package ru.ivk.lab4.render;

import lombok.Getter;
import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.core.ColorRgb;

import java.util.Objects;

/**
 * Расширенный результат трассировки первичного луча.
 */
@Getter
public final class PathTraceResult {
    public static final int BACKGROUND_OBJECT_ID = -1;

    private final ColorRgb direct;
    private final ColorRgb indirect;
    private final ColorRgb total;
    private final double depth;
    private final Vec3 normal;
    private final int objectId;
    private final boolean hit;

    public PathTraceResult(
            ColorRgb direct,
            ColorRgb indirect,
            ColorRgb total,
            double depth,
            Vec3 normal,
            int objectId,
            boolean hit
    ) {
        this.direct = Objects.requireNonNull(direct, "direct");
        this.indirect = Objects.requireNonNull(indirect, "indirect");
        this.total = Objects.requireNonNull(total, "total");
        this.depth = depth;
        this.normal = Vec3.copyOf(Objects.requireNonNull(normal, "normal"));
        this.objectId = objectId;
        this.hit = hit;
    }

    public static PathTraceResult background(ColorRgb color) {
        return new PathTraceResult(
                Objects.requireNonNull(color, "color"),
                ColorRgb.BLACK,
                color,
                Double.POSITIVE_INFINITY,
                new Vec3(0.0, 0.0, 0.0),
                BACKGROUND_OBJECT_ID,
                false
        );
    }

    public static PathTraceResult surface(
            ColorRgb direct,
            ColorRgb indirect,
            double depth,
            Vec3 normal,
            int objectId
    ) {
        ColorRgb total = Objects.requireNonNull(direct, "direct")
                .add(Objects.requireNonNull(indirect, "indirect"));

        return new PathTraceResult(direct, indirect, total, depth, normal, objectId, true);
    }

    public Vec3 getNormal() {
        return Vec3.copyOf(normal);
    }
}
