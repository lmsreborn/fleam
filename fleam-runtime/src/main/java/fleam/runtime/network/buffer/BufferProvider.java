package fleam.runtime.network.buffer;

import java.io.IOException;

public interface BufferProvider {

    boolean isDestroyed();

    boolean addBufferListener(BufferListener listener);

    Buffer requestBuffer() throws IOException;

    Buffer requestBufferBlocking() throws IOException;
}
