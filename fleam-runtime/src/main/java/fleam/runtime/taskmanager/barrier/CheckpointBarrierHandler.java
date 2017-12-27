package fleam.runtime.taskmanager.barrier;

import fleam.runtime.taskmanager.input.BufferOrEvent;

public interface CheckpointBarrierHandler {

    BufferOrEvent getNextNonBlocked()
}
