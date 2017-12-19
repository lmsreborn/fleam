package fleam.core.memory;

import java.nio.ByteBuffer;

public final class MemorySegmentFactory {
    public static MemorySegment wrap(byte[] buffer){
        return new HybridMemorySegment(buffer);
    }

    public static MemorySegment allocateUnpooledSegment(int size) {
        return allocateUnpooledSegment(size, null);
    }

    public static MemorySegment allocateUnpooledSegment(int size, Object owner) {
        return new HybridMemorySegment(new byte[size], owner);
    }

    public static MemorySegment wrapPooledHeapMemory(byte[] memory, Object owner) {
        return new HybridMemorySegment(memory, owner);
    }

    public static MemorySegment wrapPooledOffHeapMemory(ByteBuffer memory, Object owner) {
        return new HybridMemorySegment(memory, owner);
    }
}
