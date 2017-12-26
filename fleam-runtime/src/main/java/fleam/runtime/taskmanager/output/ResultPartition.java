package fleam.runtime.taskmanager.output;

import fleam.runtime.taskmanager.input.BufferAvailabilityListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ResultPartition {

    private static final Logger LOG = LoggerFactory.getLogger(ResultPartition.class);

    private final String owningTaskName;

    private final ResultPartitionID partitionId;

    private final ResultPartitionType partitionType;

    private final ResultSubpartition[] subpartitions;

    private final ResultPartitionManager partitionManager;

    private final ResultPartitionConsumableNotifier partitionConsumableNotifier;


    public ResultSubpartitionView createSubpartitionView(
            int index, BufferAvailabilityListener subPartitonListener) throws IOException {

        ResultSubpartitionView readView = subpartitions[index].createReadView(subPartitonListener);

        LOG.debug("Created {}", readView);

        return readView;
    }
}
