package fleam.core.memory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class HeapMemorySegment extends MemorySegment{
    private byte[] memory;

    HeapMemorySegment(byte[] memory){
        this(memory, null);
    }

    HeapMemorySegment(byte[] memory, Object owner){
        super(Objects.requireNonNull(memory), owner);
        this.memory = memory;
    }

    @Override
    public void free(){
        super.free();
        this.memory = null;
    }

    @Override
    public ByteBuffer wrap(int offset, int length){
        return ByteBuffer.wrap(this.memory, offset, length);
    }


    @Override
    public final byte get(int index){
        return this.memory[index];
    }

    @Override
    public final void put(int index, byte b){
        this.memory[index] = b;
    }

    @Override
    public final void put(int index, byte[] src){
        put(index, src, 0, src.length);
    }

    @Override
    public final void get(int index, byte[] dst){
        get(index, dst, 0, dst.length);
    }

    @Override
    public final void get(int index, byte[] dst, int offset, int length){
        System.arraycopy(this.memory, index, dst, offset, length);
    }


    @Override
    public final void put(int index, byte[] src, int offset, int length){
        System.arraycopy(src, offset, this.memory, index, length);
    }

    @Override
    public final boolean getBoolean(int index) {
        return this.memory[index] != 0;
    }

    @Override
    public final void putBoolean(int index, boolean value) {
        this.memory[index] = (byte) (value ? 1 : 0);
    }


    // -------------------------------------------------------------------------
    //                     Bulk Read and Write Methods
    // -------------------------------------------------------------------------

    @Override
    public final void get(DataOutput out, int offset, int length) throws IOException {
        out.write(this.memory, offset, length);
    }

    @Override
    public final void put(DataInput in, int offset, int length) throws IOException {
        in.readFully(this.memory, offset, length);
    }

    @Override
    public final void get(int offset, ByteBuffer target, int numBytes) {
        // ByteBuffer performs the boundary checks
        target.put(this.memory, offset, numBytes);
    }

    @Override
    public final void put(int offset, ByteBuffer source, int numBytes) {
        // ByteBuffer performs the boundary checks
        source.get(this.memory, offset, numBytes);
    }


    public static final class HeapMemorySegmentFactory  {

        /**
         * Creates a new memory segment that targets the given heap memory region.
         *
         * @param memory The heap memory region.
         * @return A new memory segment that targets the given heap memory region.
         */
        public HeapMemorySegment wrap(byte[] memory) {
            return new HeapMemorySegment(memory);
        }

        /**
         * Allocates some unpooled memory and creates a new memory segment that represents
         * that memory.
         *
         * @param size The size of the memory segment to allocate.
         * @param owner The owner to associate with the memory segment.
         * @return A new memory segment, backed by unpooled heap memory.
         */
        public HeapMemorySegment allocateUnpooledSegment(int size, Object owner) {
            return new HeapMemorySegment(new byte[size], owner);
        }

        /**
         * Creates a memory segment that wraps the given byte array.
         *
         * <p>This method is intended to be used for components which pool memory and create
         * memory segments around long-lived memory regions.
         *
         * @param memory The heap memory to be represented by the memory segment.
         * @param owner The owner to associate with the memory segment.
         * @return A new memory segment representing the given heap memory.
         */
        public HeapMemorySegment wrapPooledHeapMemory(byte[] memory, Object owner) {
            return new HeapMemorySegment(memory, owner);
        }

        /**
         * Prevent external instantiation.
         */
        HeapMemorySegmentFactory() {}
    }

    public static final HeapMemorySegmentFactory FACTORY = new HeapMemorySegmentFactory();
}
