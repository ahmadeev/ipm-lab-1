package ru.ivk.lab4new.core;

import ru.ivk.common.math.Vec3;

/**
 * Точечная камера, строящая первичные лучи через плоскость изображения.
 */
public final class Camera {
    private final Vec3 origin;
    private final Vec3 lowerLeftCorner;
    private final Vec3 horizontal;
    private final Vec3 vertical;

    public Camera(Vec3 origin, Vec3 lookAt, Vec3 up, double verticalFovDegrees, double aspectRatio) {
        if (verticalFovDegrees <= 0.0 || verticalFovDegrees >= 180.0) {
            throw new IllegalArgumentException("vertical FOV must be in range (0, 180)");
        }

        if (aspectRatio <= 0.0) {
            throw new IllegalArgumentException("aspect ratio must be positive");
        }

        Vec3 cameraOrigin = Vec3.copyOf(origin);
        double theta = Math.toRadians(verticalFovDegrees);
        double viewportHeight = 2.0 * Math.tan(theta / 2.0);
        double viewportWidth = aspectRatio * viewportHeight;
        Vec3 cameraBackward = cameraOrigin.sub(lookAt).normalize(); // w
        Vec3 cameraRight = up.cross(cameraBackward).normalize(); // u
        Vec3 cameraUp = cameraBackward.cross(cameraRight); // v

        this.origin = cameraOrigin;
        this.horizontal = cameraRight.mul(viewportWidth);
        this.vertical = cameraUp.mul(viewportHeight);
        this.lowerLeftCorner = cameraOrigin
                .sub(horizontal.mul(0.5))
                .sub(vertical.mul(0.5))
                .sub(cameraBackward);
    }

    public Ray ray(double u, double v) {
        Vec3 direction = lowerLeftCorner
                .add(horizontal.mul(u))
                .add(vertical.mul(v))
                .sub(origin);

        return new Ray(origin, direction);
    }
}
