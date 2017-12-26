package fleam.runtime.taskmanager.output;

public enum ResultPartitionType {
    BLOCKING(false, false, false, false),

    PIPELINED(true, true, false, false),

    /**
     * Pipelined partitions with a bounded (local) buffer pool.
     *
     * For streaming jobs, a fixed limit on the buffer pool size should help avoid that too much
     * data is being buffered and checkpoint barriers are delayed. In contrast to limiting the
     * overall network buffer pool size, this, however, still allows to be flexible with regards
     * to the total number of partitions by selecting an appropriately big network buffer pool size.
     *
     * For batch jobs, it will be best to keep this unlimited ({@link #PIPELINED}) since there are
     * no checkpoint barriers.
     */
    PIPELINED_BOUNDED(true, true, true, false),

    /**
     * Pipelined partitions with a bounded (local) buffer pool for floating buffers in input gate, and a number
     * of exclusive buffers per input channel. The producer transfers data based on consumer's available credits.
     */
    PIPELINED_CREDIT_BASED(true, true, true, true);

    /** Can the partition be consumed while being produced? */
    private final boolean isPipelined;

    /** Does the partition produce back pressure when not consumed? */
    private final boolean hasBackPressure;

    /** Does this partition use a limited number of (network) buffers? */
    private final boolean isBounded;

    /** Does this partition only send data when consumer has available buffers? */
    private final boolean isCreditBased;

    /**
     * Specifies the behaviour of an intermediate result partition at runtime.
     */
    ResultPartitionType(boolean isPipelined, boolean hasBackPressure, boolean isBounded, boolean isCreditBased) {
        this.isPipelined = isPipelined;
        this.hasBackPressure = hasBackPressure;
        this.isBounded = isBounded;
        this.isCreditBased = isCreditBased;
    }

    public boolean hasBackPressure() {
        return hasBackPressure;
    }

    public boolean isBlocking() {
        return !isPipelined;
    }

    public boolean isPipelined() {
        return isPipelined;
    }

    /**
     * Whether this partition uses a limited number of (network) buffers or not.
     *
     * @return <tt>true</tt> if the number of buffers should be bound to some limit
     */
    public boolean isBounded() {
        return isBounded;
    }

    /**
     * Whether this partition uses the credit-based mode to transfer data or not.
     *
     * @return <tt>true</tt> if the data is transferred based on consumer's credit
     */
    public boolean isCreditBased() {
        return isCreditBased;
    }
}
