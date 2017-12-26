package fleam.runtime.network;

import fleam.runtime.network.netty.NettyConnectionManager;
import fleam.runtime.network.netty.PartitionRequestProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkEnvironment {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkEnvironment.class);

    private final Object lock = new Object();

    private final NetworkBufferPool networkBufferPool;

    private final NettyConnectionManager connectionManager;

    private final ResultPartitionManager resultPartitionManager;



    public void start(){
        PartitionRequestProtocol partitionRequestProtocol = new PartitionRequestProtocol();


    }
}


