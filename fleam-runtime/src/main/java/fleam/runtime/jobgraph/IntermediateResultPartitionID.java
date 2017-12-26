package fleam.runtime.jobgraph;

import fleam.util.AbstractID;
import io.netty.buffer.ByteBuf;

public class IntermediateResultPartitionID extends AbstractID {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an new random intermediate result partition ID.
     */
    public IntermediateResultPartitionID() {
        super();
    }

    public IntermediateResultPartitionID(long lowerPart, long upperPart) {
        super(lowerPart, upperPart);
    }

    public void writeTo(ByteBuf buf) {
        buf.writeLong(this.lowerPart);
        buf.writeLong(this.upperPart);
    }

    public static IntermediateResultPartitionID fromByteBuf(ByteBuf buf) {
        long lower = buf.readLong();
        long upper = buf.readLong();
        return new IntermediateResultPartitionID(lower, upper);
    }
}
}
