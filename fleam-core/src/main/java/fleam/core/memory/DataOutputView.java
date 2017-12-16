package fleam.core.memory;

import java.io.IOException;

public interface DataOutputView extends DataView {
    void skipBytesToWrite(int numOfBytes) throws IOException;

    int write(DataInputView source, int numOfBytes) throws IOException;

}
