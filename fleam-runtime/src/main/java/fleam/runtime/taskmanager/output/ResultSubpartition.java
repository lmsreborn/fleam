package fleam.runtime.taskmanager.output;

import fleam.runtime.taskmanager.input.BufferAvailabilityListener;

import java.io.IOException;

public abstract class ResultSubpartition {

    abstract public ResultSubpartitionView createReadView(BufferAvailabilityListener availabilityListener) throws IOException;

    abstract boolean isReleased();
}
