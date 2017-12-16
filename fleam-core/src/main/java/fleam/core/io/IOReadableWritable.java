package fleam.core.io;

import java.io.IOException;

public interface IOReadableWritable {
    void write() throws IOException;

    void read() throws IOException;

}
