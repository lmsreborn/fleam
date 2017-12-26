package fleam.runtime.network.buffer;

import java.nio.Buffer;

public interface BufferListener {
    boolean notifyBufferAvailable(Buffer buffer);

    boolean notifyBUfferDestroyed();
}
