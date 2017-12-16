package fleam.core.io;

import fleam.core.memory.DataInputView;
import fleam.core.memory.DataOutputView;

import java.io.IOException;

public interface IOReadableWritable {
    void write(DataOutputView out) throws IOException;

    void read(DataInputView in) throws IOException;

}
