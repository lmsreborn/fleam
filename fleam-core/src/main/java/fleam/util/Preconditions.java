package fleam.util;

import javax.annotation.Nullable;

public final class Preconditions {
    public static <T> T checkNotNull(T reference){
        if (reference == null){
            throw new NullPointerException();
        }

        return reference;
    }

    public static <T> T checkNotNull(T reference, @Nullable String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static void checkState(boolean condition, @Nullable Object errorMessage) {
        if (!condition) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }
}
