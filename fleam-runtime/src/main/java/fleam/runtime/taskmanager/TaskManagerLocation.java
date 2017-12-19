package fleam.runtime.taskmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class TaskManagerLocation {
    private final Logger LOG = LoggerFactory.getLogger(TaskManagerServices.class);

    private final ResourceID resourceID;
    private final InetAddress inetAddress;
    private final String hostName;
    private final int dataPort;

    public TaskManagerLocation(ResourceID resourceID, InetAddress inetAddress, int dataPort){
        this.resourceID = resourceID;
        this.inetAddress = inetAddress;
        this.dataPort = dataPort;
        this.hostName = this.inetAddress.getCanonicalHostName();
    }

    public String toString(){
        return String.format("%s @ %s (dataPort=%d)", resourceID, hostName, dataPort);
    }
}
