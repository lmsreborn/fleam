package fleam.runtime.io;

import java.io.IOException;

/**
 * Basic interface that I/O requests that are sent to the threads of the I/O manager need to implement.
 */
public interface IORequest {
    public void requestDone() throws IOException;

}
