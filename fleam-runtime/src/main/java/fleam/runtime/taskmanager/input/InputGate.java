package fleam.runtime.taskmanager.input;

import java.io.IOException;

public interface InputGate {

    BufferOrEvent getNextBufferOrEvent() throws IOException, InterruptedException

    void requestPartitions() throws IOException;

    void notifyChannelNonEmpty(InputChannel channel);
}
