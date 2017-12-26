package fleam.runtime.taskmanager.output;

import fleam.runtime.network.buffer.Buffer;
import fleam.runtime.taskmanager.input.BufferAvailabilityListener;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static fleam.util.Preconditions.checkNotNull;

public class PipelinedSubpartitionView implements ResultSubpartitionView{
    private final PipelinedSubpartition subpartition;

    private final BufferAvailabilityListener availabilityListener;

    private final AtomicBoolean isReleased;

    PipelinedSubpartitionView(PipelinedSubpartition parent, BufferAvailabilityListener listener) {
        this.subpartition = checkNotNull(parent);
        this.availabilityListener = checkNotNull(listener);
        this.isReleased = new AtomicBoolean();
    }

    public void notifyBuffersAvailable(long numBuffers ){
        availabilityListener.notifyBuffersAvailable(numBuffers);
    }

    @Override
    public Buffer getNextBuffer() throws IOException{
        return this.subpartition.pollBuffer();
    }

    @Override
    public boolean isReleased(){
        return isReleased.get() || subpartition.isReleased();
    }


}
