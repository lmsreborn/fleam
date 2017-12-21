package fleam.runtime.io;

import java.io.IOException;

/**
 * Interface for I/O requests that are handled by the IOManager's writing thread.
 */
public interface IOWriteRequest extends IORequest {
    public void write() throws IOException;
}
