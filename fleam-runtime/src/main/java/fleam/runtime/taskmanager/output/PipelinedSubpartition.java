package fleam.runtime.taskmanager.output;

import fleam.runtime.network.buffer.Buffer;
import fleam.runtime.taskmanager.input.BufferAvailabilityListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayDeque;

public class PipelinedSubpartition extends ResultSubpartition {
    private static final Logger LOG = LoggerFactory.getLogger(PipelinedSubpartition.class);

    private final ArrayDeque<Buffer> buffers = new ArrayDeque<>();

    private PipelinedSubpartitionView readView;

    private volatile boolean isReleased;

    @Override
    public PipelinedSubpartitionView createReadView(BufferAvailabilityListener availabilityListener) throws IOException {
        long numOfBuffers = buffers.size();
        readView = new PipelinedSubpartitionView(this, availabilityListener);

        // notify the consumers once created
        readView.notifyBuffersAvailable(numOfBuffers);

        return readView;
    }

    Buffer pollBuffer() {
        synchronized (buffers) {
            return buffers.pollFirst();
        }
    }

    @Override
    public boolean isReleased(){
        return this.isReleased;
    }
}
