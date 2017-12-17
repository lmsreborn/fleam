package fleam.core.memory;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class MemoryUtils {
    public static final Unsafe UNSAFE = getUnsafe();

    private static Unsafe getUnsafe(){
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            return (Unsafe) unsafeField.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Access to sun.misc.Unsafe is forbidden by the runtime.", e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("The static handle field in sun.misc.Unsafe was not found.");
        }
    }

    private MemoryUtils(){}
}
