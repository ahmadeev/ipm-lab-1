package ru.ivk.lab4new.image;

import ru.ivk.common.utils.Utils;
import ru.ivk.lab4new.core.ColorRgb;
import ru.ivk.lab4new.core.RenderSettings;

/**
 * Преобразует накопленные RGB-яркости изображения в байтовые значения с нормализацией и gamma-коррекцией.
 */
public final class ImageColorMapper {
    private final double normalization;
    private final double gamma;

    public ImageColorMapper(ImageBuffer image, RenderSettings settings) {
        this.normalization = normalization(image, settings);
        this.gamma = settings.getGamma();
    }

    public int red(ColorRgb color) {
        return toByte(color.r);
    }

    public int green(ColorRgb color) {
        return toByte(color.g);
    }

    public int blue(ColorRgb color) {
        return toByte(color.b);
    }

    private int toByte(double value) {
        double relative = value / normalization;
        double corrected = Math.pow(Utils.clamp(relative, 0.0, 1.0), 1.0 / gamma);

        return (int) Math.round(corrected * 255.0);
    }

    private static double normalization(ImageBuffer image, RenderSettings settings) {
        if (settings.getNormalizationMode() == NormalizationMode.NONE) {
            return 1.0;
        }

        if (settings.getNormalizationMode() == NormalizationMode.FIXED) {
            return settings.getFixedExposure();
        }

        double max = 0.0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                max = Math.max(max, image.getPixel(x, y).maxComponent());
            }
        }

        return max > 0.0 ? max : 1.0;
    }
}
