package fleam.runtime.network.buffer;

import fleam.core.memory.MemorySegment;

public interface BufferRecycler {
    public void recycle(MemorySegment memorySegment);
}
