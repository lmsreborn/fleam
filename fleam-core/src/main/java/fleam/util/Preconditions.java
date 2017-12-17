package fleam.util;

public final class Preconditions {
    public static <T> T checkNotNill(T reference){
        if (reference == null){
            throw new NullPointerException();
        }

        return reference;
    }
}
