package fleam.runtime.network.buffer;


import fleam.core.memory.MemorySegment;
import fleam.runtime.network.NetworkBufferPool;

import java.io.IOException;
import java.util.ArrayDeque;

public class LocalBufferPool implements BufferPool {

    /** the buffer get from and recycle to. */
    private final NetworkBufferPool networkBufferPool;

    private final int minNumOfMemorySegment;

    private final int maxNumOfMemorySegment;

    private final ArrayDeque<MemorySegment> availableMemorySegments = new ArrayDeque<>();

    private final ArrayDeque<BufferListener> registeredListeners = new ArrayDeque<>();

    private int numOfRequestedMemorySegments;

    private int currentPoolSize;

    private boolean isDestroyed;

    public LocalBufferPool(NetworkBufferPool networkBufferPool,int minNumOfMemorySegment, int maxNumOfMemorySegment){
        this.networkBufferPool = networkBufferPool;
        this.minNumOfMemorySegment = minNumOfMemorySegment;
        this.maxNumOfMemorySegment = maxNumOfMemorySegment;
    }


    @Override
    public boolean isDestroyed() {
        return false;
    }

    @Override
    public void recycle(MemorySegment memorySegment) {


    }

    @Override
    public boolean addBufferListener(BufferListener listener){
        synchronized (registeredListeners){
            if(!availableMemorySegments.isEmpty() || isDestroyed){
                return false;
            }

            registeredListeners.add(listener);
            return true;
        }
    }

    @Override
    public Buffer requestBufferBlocking() throws IOException {
        return requestBuffer(true);
    }

    @Override
    public Buffer requestBuffer() throws IOException{
        return requestBuffer(false);
    }


    private Buffer requestBuffer(boolean isBlocking){
        synchronized (availableMemorySegments){

        }

    }
}
