package ru.ivk.math;

import java.util.Objects;

public class Vec3 {
    public double x, y, z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 add(Vec3 v) {
        return new Vec3(x + v.x, y + v.y, z + v.z);
    }

    public Vec3 subtract(Vec3 v) {
        return new Vec3(x - v.x, y - v.y, z - v.z);
    }

    public Vec3 multiply(double scalar) {
        return new Vec3(x * scalar, y * scalar, z * scalar);
    }

    public double dot(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec3 cross(Vec3 v) {
        return new Vec3(
                y * v.z - z * v.y, // x = a_y * b_z - a_z * b_y
                z * v.x - x * v.z, // y = a_z * b_x - a_x * b_z
                x * v.y - y * v.x // z = a_x * b_y - a_y * b_x
        );
    }

    public double length() {
        return Math.sqrt(dot(this)); // a * a = |a|^2
    }

    public Vec3 normalize() {
        double len = length();

        if (len == 0) {
            return new Vec3(0,0,0);
        }

        return multiply(1.0 / len); // v_norm = v / |v|
    }

    @Override
    public String toString() {
        return String.format("(%.4f, %.4f, %.4f)", x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec3 vec3 = (Vec3) o;
        return Double.compare(x, vec3.x) == 0 && Double.compare(y, vec3.y) == 0 && Double.compare(z, vec3.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
