package fleam.runtime.taskmanager;

import fleam.util.AbstractID;

public class AllocationID extends AbstractID{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new random AllocationID.
     */
    public AllocationID() {
        super();
    }

    /**
     * Constructs a new AllocationID with the given parts.
     *
     * @param lowerPart the lower bytes of the ID
     * @param upperPart the higher bytes of the ID
     */
    public AllocationID(long lowerPart, long upperPart) {
        super(lowerPart, upperPart);
    }
}

}
