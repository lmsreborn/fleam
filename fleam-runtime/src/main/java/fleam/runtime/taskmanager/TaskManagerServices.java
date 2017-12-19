package fleam.runtime.taskmanager;

import fleam.runtime.io.IOManager;
import fleam.runtime.memory.MemoryManager;
import fleam.runtime.network.NetworkEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManagerServices {
    private static final Logger LOG = LoggerFactory.getLogger(TaskManagerServices.class);

    private final TaskManagerLocation taskManagerLocation;
    private final MemoryManager memoryManager;
    private final IOManager ioManager;
    private final NetworkEnvironment networkEnvironment;
    private final BroadcastVariableManager broadcastVariableManager;
    private final FileCache fileCache;
    private final TaskSlotTable taskSlotTable;
    private final JobManagerTable jobManagerTable;
    private final JobLeaderService jobLeaderService;
}
