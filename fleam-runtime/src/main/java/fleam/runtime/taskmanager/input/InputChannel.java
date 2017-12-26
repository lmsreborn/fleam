package fleam.runtime.taskmanager.input;

import fleam.runtime.network.buffer.Buffer;
import fleam.runtime.taskmanager.output.ResultPartitionID;

import java.io.IOException;

import static fleam.util.Preconditions.checkNotNull;

public abstract class InputChannel {

    protected final ResultPartitionID resultPartitionID;

    protected final int channelIndex;

    protected final InputGate inputGate;

    protected void notifyChannelNonEmpty(){
        inputGate.notifyChannelNonEmpty(this);
    }

    public InputChannel(InputGate inputGate){
        this.inputGate = inputGate;
    }

    abstract void requestSubpartition(int subpartitionIndex) throws IOException;

    abstract BufferAndAvailability getNextBuffer() throws IOException;

    int getChannelIndex() {
        return channelIndex;
    }

    /**
     * A combination of a {@link Buffer} and a flag indicating availability of further buffers.
     */
    public static final class BufferAndAvailability {

        private final Buffer buffer;
        private final boolean moreAvailable;

        public BufferAndAvailability(Buffer buffer, boolean moreAvailable) {
            this.buffer = checkNotNull(buffer);
            this.moreAvailable = moreAvailable;
        }

        public Buffer buffer() {
            return buffer;
        }

        public boolean moreAvailable() {
            return moreAvailable;
        }
    }

}
