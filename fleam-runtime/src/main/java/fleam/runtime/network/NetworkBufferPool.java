package fleam.runtime.network;

import fleam.core.memory.MemorySegment;
import fleam.core.memory.MemorySegmentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class NetworkBufferPool {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkBufferPool.class);

    private final Object lock = new Object();

    private final int totalNumberOfMemorySegments;

    private final int segmentSize;

    private final ArrayBlockingQueue<MemorySegment> availableMemorySegments;

    private final Set<LocalBufferPool> allBufferPools = new HashSet<>();

    public NetworkBufferPool(int numInitialSegments, int segmentSize) {
        this.totalNumberOfMemorySegments = numInitialSegments;
        this.segmentSize = segmentSize;

        try {
            this.availableMemorySegments = new ArrayBlockingQueue<>(numInitialSegments);
        } catch (OutOfMemoryError err) {
            throw new OutOfMemoryError("Could not allocate buffer queue of length "
                    + numInitialSegments + " - " + err.getMessage());
        }

        try {
            for (int i = 0; i < numInitialSegments; i++) {
                ByteBuffer buf = ByteBuffer.allocateDirect(segmentSize);
                this.availableMemorySegments.add(MemorySegmentFactory.wrapPooledOffHeapMemory(buf, null));
            }
        } catch (OutOfMemoryError e) {
            int allocated = availableMemorySegments.size();

            // free some memory
            availableMemorySegments.clear();

            long requiredMb = (segmentSize * numInitialSegments) >> 20;
            long allocatedMb = (segmentSize * allocated) >> 20;
            long missingMb = requiredMb - allocatedMb;

            throw new OutOfMemoryError("Could not allocate enough memory segments for NetworkBufferPool " +
                    "(required (Mb): " + requiredMb +
                    ", allocated (Mb): " + allocatedMb +
                    ", missing (Mb): " + missingMb + "). Cause: " + e.getMessage());
        }

        LOG.info("Allocated {} MB for network buffer pool (number of memory segments: {}, bytes per segment: {}).",
                segmentSize * numInitialSegments, availableMemorySegments.size(), segmentSize);
    }


    public MemorySegment requestMemorySegment() {
        return availableMemorySegments.poll();
    }





}
