package ru.ivk.lab5.filter;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.core.ColorRgb;
import ru.ivk.lab4.image.PixelRenderData;
import ru.ivk.lab4.image.RenderDataBuffer;

import java.util.Objects;

/**
 * Билатеральный фильтр вторичной яркости синтезированного изображения.
 */
public final class BilateralFilter {
    /*
        p -- сама точка изображения
        g(p) -- новая яркость
        S -- некоторая квадратная область
        q -- точка изображения из области S
        f(q) -- старая яркость
        G_s(p, q) -- вес по пространству
        G_r(p, q) -- вес по яркости
        W_p -- сумма произведений весов по всем соседям
     */

    private static final double MIN_WEIGHT_SUM = 1e-12;

    private final BilateralFilterSettings settings;

    public BilateralFilter(BilateralFilterSettings settings) {
        this.settings = Objects.requireNonNull(settings, "settings");
    }

    public RenderDataBuffer apply(RenderDataBuffer source) {
        Objects.requireNonNull(source, "source");

        RenderDataBuffer filtered = new RenderDataBuffer(source.getWidth(), source.getHeight());

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                PixelRenderData center = source.getPixel(x, y);
                ColorRgb filteredIndirect = filterIndirectAt(source, x, y);

                filtered.setPixel(x, y, new PixelRenderData(
                        center.getDirect(),
                        filteredIndirect,
                        center.getDirect().add(filteredIndirect),
                        center.getDepth(),
                        center.getNormal(),
                        center.getObjectId(),
                        center.isHit()
                ));
            }
        }

        return filtered;
    }

    ColorRgb filterIndirectAt(RenderDataBuffer source, int x, int y) {
        PixelRenderData center = source.getPixel(x, y);

        if (!center.isHit()) {
            return center.getIndirect();
        }

        ColorRgb weighted = ColorRgb.BLACK;
        double weightSum = 0.0;
        int radius = settings.getRadius();

        for (int dy = -radius; dy <= radius; dy++) {
            int neighborY = y + dy;

            if (neighborY < 0 || neighborY >= source.getHeight()) {
                continue;
            }

            for (int dx = -radius; dx <= radius; dx++) {
                int neighborX = x + dx;

                if (neighborX < 0 || neighborX >= source.getWidth()) {
                    continue;
                }

                PixelRenderData neighbor = source.getPixel(neighborX, neighborY);
                double weight = weight(center, neighbor, dx, dy);

                if (weight <= 0.0) {
                    continue;
                }

                weighted = weighted.add(neighbor.getIndirect().mul(weight));
                weightSum += weight;
            }
        }

        if (weightSum <= MIN_WEIGHT_SUM) {
            return center.getIndirect();
        }

        return weighted.div(weightSum);
    }

    private double weight(PixelRenderData center, PixelRenderData neighbor, int dx, int dy) {
        if (!neighbor.isHit() || center.getObjectId() != neighbor.getObjectId()) {
            return 0.0;
        }

        return spaceWeight(dx, dy)
                * depthWeight(center, neighbor)
                * normalWeight(center, neighbor)
                * colorWeight(center, neighbor);
    }

    private double spaceWeight(int dx, int dy) {
        double distanceSquared = dx * dx + dy * dy;

        return gaussian(distanceSquared, settings.getSigmaSpace());
    }

    private double depthWeight(PixelRenderData center, PixelRenderData neighbor) {
        double difference = center.getDepth() - neighbor.getDepth();

        return gaussian(difference * difference, settings.getSigmaDepth());
    }

    private double normalWeight(PixelRenderData center, PixelRenderData neighbor) {
        Vec3 centerNormal = center.getNormal();
        Vec3 neighborNormal = neighbor.getNormal();
        double similarity = Math.max(0.0, centerNormal.dot(neighborNormal));

        return Math.pow(similarity, settings.getNormalPower());
    }

    private double colorWeight(PixelRenderData center, PixelRenderData neighbor) {
        if (!settings.isUseColorWeight()) {
            return 1.0;
        }

        ColorRgb centerColor = center.getIndirect();
        ColorRgb neighborColor = neighbor.getIndirect();
        double dr = centerColor.r - neighborColor.r;
        double dg = centerColor.g - neighborColor.g;
        double db = centerColor.b - neighborColor.b;

        return gaussian(dr * dr + dg * dg + db * db, settings.getSigmaColor());
    }

    // sigma растет, вклад веса растет
    private double gaussian(double squaredDistance, double sigma) {
        return Math.exp(-squaredDistance / (2.0 * sigma * sigma));
    }
}
