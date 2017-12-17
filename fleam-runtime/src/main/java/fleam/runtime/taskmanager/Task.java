package fleam.runtime.taskmanager;

import fleam.configuration.Configuration;
import fleam.runtime.blob.PermanentBlobKey;
import fleam.runtime.executiongraph.ExecutionAttemptID;
import fleam.runtime.jobgraph.JobVertexID;
import fleam.util.JobID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class Task implements Runnable{

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

    private final Configuration taskConfiguration;

    private final TaskManagerConfiguration taskManagerConfig;

    // -----------------------------------------------------------------------------
    //  Managers related to this Task (Mem, IO, NetWork, Store)
    // ----------------------------------------------------------------------------

    /** The memory manager to be used by this task */
    private final MemoryManager memoryManager;

    /** The I/O manager to be used by this task */
    private final IOManager ioManager;

    /** The gateway to the network stack, which handles inputs and produced results */
    private final NetworkEnvironment network;

    /** The BroadcastVariableManager to be used by this task */
    private final BroadcastVariableManager broadcastVariableManager;

    /** The BLOB cache, from which the task can request BLOB files */
    private final BlobCacheService blobService;

    /** The library cache, from which the task can request its class loader */
    private final LibraryCacheManager libraryCache;

    /** The cache for user-defined files that the invokable requires */
    private final FileCache fileCache;

    /** jars used by this task */
    private final Collection<PermanentBlobKey> requiredJarFiles;

    private final Collection<URL> requiredClasspaths;

    /**
     * This class loader should be set as the context class loader of the threads in
     * {@link #asyncCallDispatcher} because user code may dynamically load classes in all callbacks.
     */
    private ClassLoader userCodeClassLoader;

    // ----------------------------------------------------------------------------
    //  Task Execution
    // ----------------------------------------------------------------------------

    /** The thread that executes the task */
    private final Thread executingThread;

    /** Executor to run future callbacks */
    private final Executor executor;


    // ------------------------------------------------------------------------
    //  Fields that control the task execution. All these fields are volatile
    //  (which means that they introduce memory barriers), to establish
    //  proper happens-before semantics on parallel modification
    // ------------------------------------------------------------------------

    /** atomic flag that makes sure the invokable is canceled exactly once upon error */
    private final AtomicBoolean invokableHasBeenCanceled;

    /** The invokable of this task, if initialized */
    private volatile AbstractInvokable invokable;

    /** The current execution state of the task */
    private volatile ExecutionState executionState = ExecutionState.CREATED;

    /** The observed exception, in case the task execution failed */
    private volatile Throwable failureCause;

    /** Serial executor for asynchronous calls (checkpoints, etc), lazily initialized */
    private volatile ExecutorService asyncCallDispatcher;

    /**
     * The handles to the states that the task was initialized with. Will be set
     * to null after the initialization, to be memory friendly.
     */
    private volatile TaskStateSnapshot taskStateHandles;

    /** Initialized from the Flink configuration. May also be set at the ExecutionConfig */
    private long taskCancellationInterval;

    /** Initialized from the Flink configuration. May also be set at the ExecutionConfig */
    private long taskCancellationTimeout;


    // ----------------------------------------------------------------------------
    //  Task Data from and to
    // ----------------------------------------------------------------------------

    /** The resource data from which the task gets */
    private final SingleInputGate[] inputGates;

    /** The result data to which the task targets */
    private final ResultPartition[] producedPartitions;

    // ----------------------------------------------------------------------------
    //  Task Data display
    // ----------------------------------------------------------------------------
    /** Parent group for all metrics of this task */
    private final TaskMetricGroup metrics;

    // ---------------------------------------------------------------------------


    /**
     * Constructor
     */

    public Task(){

    }


    @Override
    public void run() {

    }
}
