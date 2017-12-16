package fleam.core.memory;

import java.io.DataOutput;
import java.io.IOException;

public interface DataOutputView extends DataOutput, DataView {
    void skipBytesToWrite(int numOfBytes) throws IOException;

    int write(DataInputView source, int numOfBytes) throws IOException;

}
