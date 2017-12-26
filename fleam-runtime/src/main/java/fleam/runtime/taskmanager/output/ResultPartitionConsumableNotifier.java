package fleam.runtime.taskmanager.output;

import fleam.util.JobID;

public interface ResultPartitionConsumableNotifier {
    void notifyPartitionConsumable(JobID jobId, ResultPartitionID partitionId, TaskActions taskActions);
}
