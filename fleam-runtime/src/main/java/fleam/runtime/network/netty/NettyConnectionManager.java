package fleam.runtime.network.netty;

import fleam.runtime.network.netty.NettyServer;

public class NettyConnectionManager {
    private final NettyServer server;

    private final NettyClient client;

    private final NettyBufferPool bufferPool;

    private final PartitionRequestClientFactory partitionRequestClientFactory;


    public NettyConnectionManager(){
        this.server = new NettyServer();
        this.client = new NettyClient();
        this.bufferPool = new NettyBufferPool();
        this.partitionRequestClientFactory = new PartitionRequestClientFactory();
    }


    public void start(ResultPartitionProvider partitionProvider, TaskEventDispatcher taskEventDispatcher){
        PartitionRequestProtocol partitionRequestProtocol =
                new PartitionRequestProtocol(partitionProvider, taskEventDispatcher);
        client.init();
        server.init();
    }
}
