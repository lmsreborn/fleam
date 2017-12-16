package fleam.core.memory;

import java.io.DataInput;
import java.io.IOException;

public interface DataInputView extends DataView, DataInput {

    void skipBytesToRead(int numOfBytes) throws IOException;

    int read(byte[] b) throws IOException;
}
