package ru.ivk;

import org.junit.jupiter.api.Test;
import ru.ivk.math.Vec3;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Vec3Test {
    @Test
    void testSubtract() {
        var expected = new Vec3(3, 4, 0);
        var actual = new Vec3(5, 7, 0).subtract(new Vec3(2, 3, 0));

        assertEquals(expected, actual);
    }
}
