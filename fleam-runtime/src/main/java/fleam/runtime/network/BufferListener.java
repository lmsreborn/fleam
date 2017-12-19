package fleam.runtime.network;

import java.nio.Buffer;

public interface BufferListener {
    boolean notifyBufferAvailable(Buffer buffer);

    boolean notifyBUfferDestroyed();
}
