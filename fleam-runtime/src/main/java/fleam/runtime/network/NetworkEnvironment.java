package fleam.runtime.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkEnvironment {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkEnvironment.class);

    private final Object lock = new Object();

    private final NetworkBufferPool networkBufferPool;

    private final ConnectionManager connectionManager;

    private final ResultPartitionManager resultPartitionManager;



}


