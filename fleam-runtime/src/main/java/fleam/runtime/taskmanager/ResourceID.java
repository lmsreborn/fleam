package fleam.runtime.taskmanager;

import fleam.util.AbstractID;
import fleam.util.Preconditions;

public final class ResourceID implements java.io.Serializable{

    private static final long serialVersionUID = 8214410376605396001L;

    private final String resourceID;

    public ResourceID(String resourceID) {
        Preconditions.checkNotNull(resourceID, "ResourceID must not be null");
        this.resourceID = resourceID;
    }

    public String getResourceID(){
        return this.resourceID;
    }

    public static ResourceID generate() {
        return new ResourceID(new AbstractID().toString());
    }
}
