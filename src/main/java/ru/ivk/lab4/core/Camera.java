package ru.ivk.lab4.core;

import ru.ivk.common.math.Vec3;

public final class Camera {
    private final Vec3 origin;
    private final Vec3 lowerLeftCorner;
    private final Vec3 horizontal;
    private final Vec3 vertical;

    public Camera(Vec3 origin, Vec3 lookAt, Vec3 up, double verticalFovDegrees, double aspectRatio) {
        if (verticalFovDegrees <= 0.0 || verticalFovDegrees >= 180.0) {
            throw new IllegalArgumentException("verticalFovDegrees must be in (0, 180)");
        }
        if (aspectRatio <= 0.0) {
            throw new IllegalArgumentException("aspectRatio must be positive");
        }

        this.origin = Vec3.copyOf(origin);

        double theta = Math.toRadians(verticalFovDegrees);
        double viewportHeight = 2.0 * Math.tan(theta / 2.0);
        double viewportWidth = aspectRatio * viewportHeight;

        Vec3 w = origin.sub(lookAt).normalize();
        Vec3 u = up.cross(w).normalize();
        Vec3 v = w.cross(u);

        this.horizontal = u.mul(viewportWidth);
        this.vertical = v.mul(viewportHeight);
        this.lowerLeftCorner = origin
                .sub(horizontal.mul(0.5))
                .sub(vertical.mul(0.5))
                .sub(w);
    }

    public Ray ray(double s, double t) {
        Vec3 direction = lowerLeftCorner
                .add(horizontal.mul(s))
                .add(vertical.mul(t))
                .sub(origin);

        return new Ray(origin, direction);
    }
}
