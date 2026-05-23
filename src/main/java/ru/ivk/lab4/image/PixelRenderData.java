package ru.ivk.lab4.image;

import lombok.Getter;
import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.core.ColorRgb;
import ru.ivk.lab4.render.PathTraceResult;

import java.util.Objects;

/**
 * Данные рендера для одного пикселя до преобразования в байтовое изображение.
 */
@Getter
public final class PixelRenderData {
    public static final PixelRenderData BACKGROUND = background(ColorRgb.BLACK);

    private final ColorRgb direct;
    private final ColorRgb indirect;
    private final ColorRgb total;
    private final double depth;
    private final Vec3 normal;
    private final int objectId;
    private final boolean hit;

    public PixelRenderData(
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

    public static PixelRenderData from(PathTraceResult result) {
        return new PixelRenderData(
                result.getDirect(),
                result.getIndirect(),
                result.getTotal(),
                result.getDepth(),
                result.getNormal(),
                result.getObjectId(),
                result.isHit()
        );
    }

    public static PixelRenderData background(ColorRgb color) {
        return new PixelRenderData(
                Objects.requireNonNull(color, "color"),
                ColorRgb.BLACK,
                color,
                Double.POSITIVE_INFINITY,
                new Vec3(0.0, 0.0, 0.0),
                PathTraceResult.BACKGROUND_OBJECT_ID,
                false
        );
    }

    public static PixelRenderData surface(
            ColorRgb direct,
            ColorRgb indirect,
            double depth,
            Vec3 normal,
            int objectId
    ) {
        ColorRgb total = Objects.requireNonNull(direct, "direct")
                .add(Objects.requireNonNull(indirect, "indirect"));

        return new PixelRenderData(direct, indirect, total, depth, normal, objectId, true);
    }

    public Vec3 getNormal() {
        return Vec3.copyOf(normal);
    }
}
