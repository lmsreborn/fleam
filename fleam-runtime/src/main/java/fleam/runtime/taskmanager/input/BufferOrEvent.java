package fleam.runtime.taskmanager.input;

import fleam.runtime.network.buffer.Buffer;

import static com.google.common.base.Preconditions.checkArgument;
import static fleam.util.Preconditions.checkNotNull;

public class BufferOrEvent {
    private final Buffer buffer;

    private final AbstractEvent event;

    /**
     * Indicate availability of further instances for the union input gate.
     * This is not needed outside of the input gate unioning logic and cannot
     * be set outside of the consumer package.
     */
    private final boolean moreAvailable;

    private int channelIndex;

    BufferOrEvent(Buffer buffer, int channelIndex, boolean moreAvailable) {
        this.buffer = checkNotNull(buffer);
        this.event = null;
        this.channelIndex = channelIndex;
        this.moreAvailable = moreAvailable;
    }

    BufferOrEvent(AbstractEvent event, int channelIndex, boolean moreAvailable) {
        this.buffer = null;
        this.event = checkNotNull(event);
        this.channelIndex = channelIndex;
        this.moreAvailable = moreAvailable;
    }

    public BufferOrEvent(Buffer buffer, int channelIndex) {
        this(buffer, channelIndex, true);
    }

    public BufferOrEvent(AbstractEvent event, int channelIndex) {
        this(event, channelIndex, true);
    }

    public boolean isBuffer() {
        return buffer != null;
    }

    public boolean isEvent() {
        return event != null;
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public AbstractEvent getEvent() {
        return event;
    }

    public int getChannelIndex() {
        return channelIndex;
    }

    public void setChannelIndex(int channelIndex) {
        checkArgument(channelIndex >= 0);
        this.channelIndex = channelIndex;
    }

    boolean moreAvailable() {
        return moreAvailable;
    }

    @Override
    public String toString() {
        return String.format("BufferOrEvent [%s, channelIndex = %d]",
                isBuffer() ? buffer : event, channelIndex);
    }
}
