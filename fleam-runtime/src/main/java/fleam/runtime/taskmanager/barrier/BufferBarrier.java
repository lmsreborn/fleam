package fleam.runtime.taskmanager.barrier;

import fleam.runtime.taskmanager.input.BufferOrEvent;
import fleam.runtime.taskmanager.input.InputGate;

public class BufferBarrier implements CheckpointBarrierHandler{
    private final InputGate inputGate;
    private final boolean[] blockedChannels;
    private final BufferSpiller bufferSpiller;

    private final int totalNumberOfInputChannels;


    @Override
    public BufferOrEvent getNextNonBlocked() throws Exception {
        while (true){

        }

    }
}
