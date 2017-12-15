package fleam.runtime.execution;

/**
 * *     CREATED  -> SCHEDULED -> DEPLOYING -> RUNNING -> FINISHED
 * |            |            |          |
 * |            |            |   +------+
 * |            |            V   V
 * |            |         CANCELLING -----+----> CANCELED
 * |            |                         |
 * |            +-------------------------+
 * |
 * |                                   ... -> FAILED
 * V
 * RECONCILING  -> RUNNING | FINISHED | CANCELED | FAILED
 */
public enum ExecutionState {

    CREATED,

    SCHEDULED,

    DEPLOYING,

    RUNNING,

    FINISHED,

    CANCELLING,

    CANCELED,

    FAILED,

    RECONCILING

}
