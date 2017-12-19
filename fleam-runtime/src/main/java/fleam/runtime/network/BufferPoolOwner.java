package fleam.runtime.network;

import java.io.IOException;

public interface BufferPoolOwner {

    void releaseMemory(int numOfBuffersToRecycle) throws IOException;
}
