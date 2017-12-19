package fleam.runtime.memory;

import fleam.core.memory.HybridMemorySegment;
import fleam.core.memory.MemorySegment;
import fleam.core.memory.MemorySegmentFactory;
import fleam.core.memory.MemoryType;
import fleam.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.*;

public class MemoryManager {

    private static final Logger LOG = LoggerFactory.getLogger(MemoryManager.class);

    private static final int DEFAULT_PAGE_SIZE = 32 * 1024;

    private static final int MIN_PAGE_SIZE = 4 * 1024;

    // -----------------------------------------------------------------------------


    /**
     * The lock used on the shared structures.
     */
    private final Object lock = new Object();

    private final MemoryPool memoryPool;

    // map: Owner - Set<MemorySegment>
    private final HashMap<Object, Set<MemorySegment>> allocatedSegments;

    private final MemoryType memoryType;

    private final long managedMemorySize;

    private final int pageSize;

    private final int totalNumOfPages;

    /**
     * The number of memory pages that have not been allocated and are available for lazy allocation.
     */
    private int numNonAllocatedPages;

    private final int numOfSlots;

    /**
     * Flag marking whether the memory manager immediately allocates the memory.
     */
    private final boolean isPreAllocated;

    /**
     * Flag whether the close() has already been invoked.
     */
    private boolean isShutDown;


    // ---------------------------------------------------------------------

    public MemoryManager(long memorySize, int numOfSlots, int pageSize,
                         MemoryType memoryType, boolean isPreAllocated) {

        checkArguments(memorySize, pageSize, memoryType);

        this.managedMemorySize = memorySize;
        this.numOfSlots = numOfSlots;
        this.pageSize = pageSize;
        this.memoryType = memoryType;
        this.isPreAllocated = isPreAllocated;

        this.totalNumOfPages = (int) managedMemorySize / pageSize;
        if (this.totalNumOfPages < 1) {
            throw new IllegalArgumentException("The given memory size amounted to less than one page");
        }

        this.allocatedSegments = new HashMap<Object, Set<MemorySegment>>();
        this.numNonAllocatedPages = isPreAllocated ? 0 : this.totalNumOfPages;
        final int numOfPagesToAlloc = isPreAllocated ? this.totalNumOfPages : 0;

        switch (memoryType){
            case HEAP:
                this.memoryPool = new HybridHeapMemoryPool(numOfPagesToAlloc, pageSize);
                break;
            case OFF_HEAP:
                this.memoryPool = new HybridOffHeapMemoryPool(numOfPagesToAlloc, pageSize);
                break;
        }
    }


    // ------------------------------------------------------------------------
    //  Memory allocation and release
    // ------------------------------------------------------------------------

    public List<MemorySegment> allocatePages(Object owner, int numPages) throws MemoryAllocationException {
        final ArrayList<MemorySegment> segs = new ArrayList<MemorySegment>(numPages);
        allocatePages(owner, segs, numPages);
        return segs;
    }

    private void allocatePages(Object owner, List<MemorySegment> target, int numPages) throws MemoryAllocationException{

    }





    private void checkArguments(long managedMemorySize, int pageSize, MemoryType memoryType) {
        if (memoryType != MemoryType.HEAP && memoryType != MemoryType.OFF_HEAP) {
            throw new IllegalArgumentException("unrecognized memory type: " + memoryType);
        }

        if (managedMemorySize < 0) {
            throw new IllegalArgumentException("Size of total memory must be positive.");
        }

        if (pageSize < MIN_PAGE_SIZE) {
            throw new IllegalArgumentException("The page size must be at least " + MIN_PAGE_SIZE + " bytes.");
        }

        if (!MathUtils.isPowerOf2(pageSize)) {
            throw new IllegalArgumentException("The given page size is not a power of two.");
        }
    }


    // ------------------------------------------------------------------------
    //  Memory Pools
    // ------------------------------------------------------------------------
    abstract static class MemoryPool {
        abstract MemorySegment allocateNewMemorySegment(Object owner);

        abstract MemorySegment requestSegmentFromPool(Object owner);

        abstract void returnSegmentToPool(MemorySegment segment);

        abstract int getNumberOfAvailableMemorySegments();

        abstract void clear();
    }


    static final class HybridHeapMemoryPool extends MemoryPool {

        private final ArrayDeque<byte[]> availableMemory;

        private final int segmentSize;

        HybridHeapMemoryPool(int numOfInitialSegments, int segmentSize) {
            this.availableMemory = new ArrayDeque<>(numOfInitialSegments);
            this.segmentSize = segmentSize;

            for (int i = 0; i < numOfInitialSegments; i++) {
                this.availableMemory.add(new byte[segmentSize]);

            }
        }


        @Override
        MemorySegment allocateNewMemorySegment(Object owner) {
            return MemorySegmentFactory.allocateUnpooledSegment(segmentSize);
        }


        @Override
        MemorySegment requestSegmentFromPool(Object owner){
            byte[] buf = availableMemory.remove();
            return MemorySegmentFactory.wrapPooledHeapMemory(buf, owner);
        }

        @Override
        void returnSegmentToPool(MemorySegment segment) {
            if (segment.getClass() == HybridMemorySegment.class) {
                HybridMemorySegment heapSegment = (HybridMemorySegment) segment;
                availableMemory.add(heapSegment.getHeapBuffer());
                heapSegment.free();
            } else {
                throw new IllegalArgumentException("Memory segment is not a " + HybridMemorySegment.class.getSimpleName());
            }
        }

        @Override
        protected int getNumberOfAvailableMemorySegments() {
            return availableMemory.size();
        }

        @Override
        void clear() {
            this.availableMemory.clear();
        }
    }


    static final class HybridOffHeapMemoryPool extends MemoryPool {

        /** The collection of availagble memory segments. */
        private final ArrayDeque<ByteBuffer> availableMemory;

        private final int segmentSize;

        HybridOffHeapMemoryPool(int numInitialSegments, int segmentSize) {
            this.availableMemory = new ArrayDeque<>(numInitialSegments);
            this.segmentSize = segmentSize;

            for (int i = 0; i < numInitialSegments; i++) {
                this.availableMemory.add(ByteBuffer.allocateDirect(segmentSize));
            }
        }

        @Override
        MemorySegment allocateNewMemorySegment(Object owner) {
            ByteBuffer memory = ByteBuffer.allocateDirect(segmentSize);
            return MemorySegmentFactory.wrapPooledOffHeapMemory(memory, owner);
        }

        @Override
        MemorySegment requestSegmentFromPool(Object owner) {
            ByteBuffer buf = availableMemory.remove();
            return MemorySegmentFactory.wrapPooledOffHeapMemory(buf, owner);
        }

        @Override
        void returnSegmentToPool(MemorySegment segment) {
            if (segment.getClass() == HybridMemorySegment.class) {
                HybridMemorySegment hybridSegment = (HybridMemorySegment) segment;
                ByteBuffer buf = hybridSegment.getOffHeapBuffer();
                availableMemory.add(buf);
                hybridSegment.free();
            }
            else {
                throw new IllegalArgumentException("Memory segment is not a " + HybridMemorySegment.class.getSimpleName());
            }
        }

        @Override
        protected int getNumberOfAvailableMemorySegments() {
            return availableMemory.size();
        }

        @Override
        void clear() {
            availableMemory.clear();
        }
    }



}
