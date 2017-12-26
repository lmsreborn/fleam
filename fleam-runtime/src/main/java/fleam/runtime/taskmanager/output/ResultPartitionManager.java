package fleam.runtime.taskmanager.output;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import fleam.runtime.executiongraph.ExecutionAttemptID;
import fleam.runtime.jobgraph.IntermediateResultPartitionID;
import fleam.runtime.taskmanager.input.BufferAvailabilityListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The result partition manager keeps track of all currently produced/consumed partitions of a
 * task manager.
 */

public class ResultPartitionManager {
    private static final Logger LOG = LoggerFactory.getLogger(ResultPartitionManager.class);

    public final Table<ExecutionAttemptID, IntermediateResultPartitionID, ResultPartition>
            registeredPartitions = HashBasedTable.create();


    public ResultSubpartitionView createSubpartitionView(
            ResultPartitionID partitionID,
            int subpartitionIndex,
            BufferAvailabilityListener subPartitionListener) throws IOException {
        synchronized (registeredPartitions) {
            ResultPartition resultPartition = registeredPartitions.get(partitionID.getProducerId(),
                    partitionID.getPartitionId());

            if (resultPartition == null) {
                throw new PartitionNotFoundException(partitionID);
            }

            return resultPartition.createSubpartitionView(subpartitionIndex, subPartitionListener);
        }

    }
}
