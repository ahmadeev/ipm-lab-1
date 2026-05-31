package ru.ivk.lab5.image;

import lombok.Getter;
import ru.ivk.lab4.image.PixelRenderData;
import ru.ivk.lab4.image.RenderDataBuffer;

import java.util.Objects;

/**
 * Суммарная яркость компонент рендера для проверки физической корректности фильтрации.
 */
@Getter
public final class BrightnessStats {
    private final double direct;
    private final double indirect;
    private final double total;

    private BrightnessStats(double direct, double indirect, double total) {
        this.direct = direct;
        this.indirect = indirect;
        this.total = total;
    }

    public static BrightnessStats from(RenderDataBuffer data) {
        Objects.requireNonNull(data, "data");

        double direct = 0.0;
        double indirect = 0.0;
        double total = 0.0;

        for (int y = 0; y < data.getHeight(); y++) {
            for (int x = 0; x < data.getWidth(); x++) {
                PixelRenderData pixel = data.getPixel(x, y);
                direct += pixel.getDirect().average();
                indirect += pixel.getIndirect().average();
                total += pixel.getTotal().average();
            }
        }

        return new BrightnessStats(direct, indirect, total);
    }
}
