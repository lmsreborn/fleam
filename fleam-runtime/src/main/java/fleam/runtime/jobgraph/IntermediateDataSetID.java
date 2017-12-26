package fleam.runtime.jobgraph;

import fleam.util.AbstractID;

import java.util.UUID;

public class IntermediateDataSetID extends AbstractID {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an new random intermediate data set ID.
     */
    public IntermediateDataSetID() {
        super();
    }

    /**
     * Creates a new intermediate data set ID with the bytes of the given ID.
     *
     * @param from The ID to create this ID from.
     */
    public IntermediateDataSetID(AbstractID from) {
        super(from);
    }

    /**
     * Creates a new intermediate data set ID with the bytes of the given UUID.
     *
     * @param from The UUID to create this ID from.
     */
    public IntermediateDataSetID(UUID from) {
        super(from.getLeastSignificantBits(), from.getMostSignificantBits());
    }
}}
