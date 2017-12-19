package fleam.runtime.network;

import fleam.core.memory.MemorySegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;

public class LocalBufferPool {
    private static final Logger LOG = LoggerFactory.getLogger(LocalBufferPool.class);

    private final NetworkBufferPool networkBufferPool;

    private final int minNumOfPoolMemorySegment;

    private final int maxNumOfPoolMemorySegment;

    private int currentPoolSize;

    private int numberOfRequestedMemorySegments;

    private final ArrayDeque<MemorySegment> availableMemorySegments = new ArrayDeque<MemorySegment>();

    private final ArrayDeque<BufferListener> registeredListeners = new ArrayDeque<>();

    private BufferPoolOwner owner;

    LocalBufferPool(NetworkBufferPool networkBufferPool, int minNumOfPoolMemorySegment, int maxNumOfPoolMemorySegment){
        LOG.info("Using a local buffer pool with {}-{} buffers", minNumOfPoolMemorySegment, maxNumOfPoolMemorySegment);

        this.networkBufferPool = networkBufferPool;
        this.minNumOfPoolMemorySegment = minNumOfPoolMemorySegment;
        this.maxNumOfPoolMemorySegment = maxNumOfPoolMemorySegment;
        this.currentPoolSize = minNumOfPoolMemorySegment;
    }




}
