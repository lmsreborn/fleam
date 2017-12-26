package fleam.runtime.taskmanager.output;

import fleam.runtime.network.buffer.Buffer;

import java.io.IOException;

public interface ResultSubpartitionView {

    Buffer getNextBuffer() throws IOException;

    boolean isReleased();
}
