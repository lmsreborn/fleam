package fleam.core.memory;

import java.io.IOException;

public interface DataInputView extends DataView {

    void skipBytesToRead(int numOfBytes) throws IOException;

    int read(byte[] b) throws IOException;
}
