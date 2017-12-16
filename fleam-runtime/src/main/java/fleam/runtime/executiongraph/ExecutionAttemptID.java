package fleam.runtime.executiongraph;

import fleam.util.AbstractID;
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBuf;

public class ExecutionAttemptID extends AbstractID{

    public ExecutionAttemptID(){
        super();
    }

    public ExecutionAttemptID(long lowerPart, long upperPart){
        super(lowerPart, upperPart);
    }

    public static ExecutionAttemptID fromByteBuf(ByteBuf buf) {
        long lower = buf.readLong();
        long upper = buf.readLong();
        return new ExecutionAttemptID(lower, upper);
    }

}
