package fleam.runtime.network.buffer;

import java.io.IOException;

public interface BufferPoolOwner {

    void releaseMemory(int numOfBuffersToRecycle) throws IOException;
}
