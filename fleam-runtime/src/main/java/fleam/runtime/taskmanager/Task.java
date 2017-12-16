package fleam.runtime.taskmanager;

import fleam.configuration.Configuration;
import fleam.runtime.executiongraph.ExecutionAttemptID;
import fleam.runtime.jobgraph.JobVertexID;
import fleam.util.JobID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Task {

    public static final Logger LOG = LoggerFactory.getLogger(Task.class);

    public static final ThreadGroup TASK_THREAD_GROUP = new ThreadGroup("Task Threads");

    // ------------------------------------------------------------------------
    //  ID to identify kinds of resource
    // ------------------------------------------------------------------------

    private final JobID jobID;

    private final JobVertexID vertxID;

    private final ExecutionAttemptID executionAttemptID;

    private final AllocationID allocationId;


    // ------------------------------------------------------------------------
    //  configuration
    // ------------------------------------------------------------------------

    private final Configuration jobConfiguration;
}
