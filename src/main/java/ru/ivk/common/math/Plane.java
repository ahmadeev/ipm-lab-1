package ru.ivk.common.math;

public class Plane {
    private static final double PRECISION = 1e-6;

    private final Vec3 normal;
    private final double d;

    public Plane(Vec3 normal, Vec3 planePoint) {
        if (normal.length() == 0.0) {
            throw new IllegalArgumentException("normal must be non-zero");
        }

        this.normal = new Vec3(normal.x, normal.y, normal.z);
        this.d = -(this.normal.x * planePoint.x + this.normal.y * planePoint.y + this.normal.z * planePoint.z);
    }

    public double calc(Vec3 point) {
        return normal.x * point.x + normal.y * point.y + normal.z * point.z + d;
    }

    public double calcDistanceTo(Vec3 point) {
        return Math.abs(calc(point)) / Math.sqrt(Math.pow(normal.x, 2) + Math.pow(normal.y, 2) + Math.pow(normal.z, 2));
    }

    public boolean isInPlane(Vec3 point) {
        return calcDistanceTo(point) <= PRECISION;
    }
}
