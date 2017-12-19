package fleam.util;

public final class MathUtils {
    public static boolean isPowerOf2(long value) {
        return (value & (value - 1L)) == 0L;
    }
}
