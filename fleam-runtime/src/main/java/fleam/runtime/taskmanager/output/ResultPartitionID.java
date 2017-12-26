package fleam.runtime.taskmanager.output;

import fleam.runtime.executiongraph.ExecutionAttemptID;
import fleam.runtime.jobgraph.IntermediateResultPartitionID;

import java.io.Serializable;

public class ResultPartitionID implements Serializable {

    private static final long serialVersionUID = 8520699852176570127L;

    private final IntermediateResultPartitionID partitionId;

    private final ExecutionAttemptID producerId;

    public ResultPartitionID() {
        this(new IntermediateResultPartitionID(), new ExecutionAttemptID());
    }

    public ResultPartitionID(IntermediateResultPartitionID partitionId, ExecutionAttemptID producerId) {
        this.partitionId = partitionId;
        this.producerId = producerId;
    }

    public IntermediateResultPartitionID getPartitionId() {
        return partitionId;
    }

    public ExecutionAttemptID getProducerId() {
        return producerId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == ResultPartitionID.class) {
            ResultPartitionID o = (ResultPartitionID) obj;

            return o.getPartitionId().equals(partitionId) && o.getProducerId().equals(producerId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return partitionId.hashCode() ^ producerId.hashCode();
    }

    @Override
    public String toString() {
        return partitionId.toString() + "@" + producerId.toString();
    }
}
}
