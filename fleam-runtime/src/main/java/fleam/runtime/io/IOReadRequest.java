package fleam.runtime.io;

import java.io.IOException;

/**
 * Interface for I/O requests that are handled by the IOManager's reading thread.
 */
public interface IOReadRequest extends IORequest {
    public void read() throws IOException;
}
