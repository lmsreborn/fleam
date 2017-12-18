package fleam.runtime.memory;

import fleam.core.memory.MemorySegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryManager {

    private static final Logger LOG = LoggerFactory.getLogger(MemoryManager.class);

    private static final int DEFAULT_PAGE_SIZE = 32 * 1024;

    private static final int MIN_PAGE_SIZE = 4 * 1024;

    // -----------------------------------------------------------------------------

    private final MemoryPool memoryPool;



    abstract static class MemoryPool {
        abstract MemorySegment allocateNewMemorySegemtn();
        abstract MemorySegment requestSegmentFromPool();
        abstract void returnSegmentToPool(MemorySegment segment);
        abstract void clear();
    }


}
