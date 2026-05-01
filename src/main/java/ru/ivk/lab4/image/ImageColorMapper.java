package ru.ivk.lab4.image;

import ru.ivk.lab4.core.ColorRgb;

final class ImageColorMapper {
    private ImageColorMapper() {
    }

    static double resolveExposure(ImageBuffer image, NormalizationMode normalizationMode, double fixedExposure) {
        if (normalizationMode == NormalizationMode.MAX) {
            double max = image.maxComponent();
            return max > 0.0 ? 1.0 / max : 1.0;
        }

        if (normalizationMode == NormalizationMode.FIXED) {
            return fixedExposure;
        }

        return 1.0;
    }

    static int toByte(ColorRgb source, double exposure, double gamma, int componentIndex) {
        ColorRgb color = source.mul(exposure).clamp(0.0, 1.0);

        if (componentIndex == 0) {
            return toByte(color.r, gamma);
        }

        if (componentIndex == 1) {
            return toByte(color.g, gamma);
        }

        return toByte(color.b, gamma);
    }

    private static int toByte(double value, double gamma) {
        double corrected = Math.pow(value, 1.0 / gamma);
        return (int) Math.round(corrected * 255.0);
    }
}
