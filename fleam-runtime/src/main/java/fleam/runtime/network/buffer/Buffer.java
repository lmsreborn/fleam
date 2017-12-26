package fleam.runtime.network.buffer;

import fleam.core.memory.MemorySegment;
import fleam.util.Preconditions;

/**
 * Wrapper for pooled Memory segment;
 */

public class Buffer {
    /**
     * The backing memorySegment
     */
    private final MemorySegment memorySegment;

    /**
     * recycler for buffer, always be a buffer pool
     */
    private final BufferRecycler bufferRecycler;

    private int referenceCount = 1;

    private volatile boolean isBuffer;

    private volatile int size;


    public Buffer(MemorySegment memorySegment, BufferRecycler bufferRecycler){
        this(memorySegment, bufferRecycler, true);
    }


    public Buffer(MemorySegment memorySegment, BufferRecycler bufferRecycler, boolean isBuffer){
        this.memorySegment = memorySegment;
        this.bufferRecycler = bufferRecycler;
        this.isBuffer = true;
    }
    public int getSizeUnsafe() {
        return size;
    }

    public void setSize(int newSize) {
        ensureNotRecycled();

        if (newSize < 0 || newSize > memorySegment.size()) {
            throw new IllegalArgumentException("Size of buffer must be >= 0 and <= " +
                    memorySegment.size() + ", but was " + newSize + ".");
        }

        this.size = newSize;

    }

    public boolean isBuffer(){
        return isBuffer;
    }

    private void ensureNotRecycled(){
        Preconditions.checkState(referenceCount > 0, "Buffer has already been recycled.");
    }

}
