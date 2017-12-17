package fleam.runtime.blob;

import fleam.util.AbstractID;
import fleam.util.Preconditions;

/**
 * A blob key ( byte[20]) identifies a BLOB
 *
 *
 */
abstract class BlobKey implements java.io.Serializable, Comparable<BlobKey>{

    private static final long serialVersionUID = -4041872425543199315L;

    private static final int SIZE = 20;

    enum BlobType{
        PERMANENT_BLOB,
        TRANSIENT_BLOB
    }


    // ------------------------------------------------------------------------
    //  components
    // ------------------------------------------------------------------------

    private final byte[] key;

    private final BlobType type;

    private final AbstractID random;

    // -----------------------------------------------------

    protected BlobKey(BlobType type){
        this.type = Preconditions.checkNotNill(type);
        this.key = new byte[SIZE];
        this.random = new AbstractID();
    }
}
