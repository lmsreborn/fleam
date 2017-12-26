package fleam.runtime.taskmanager.input;

import fleam.runtime.network.TaskEventDispatcher;
import fleam.runtime.network.buffer.Buffer;
import fleam.runtime.taskmanager.output.PartitionNotFoundException;
import fleam.runtime.taskmanager.output.ResultPartitionManager;
import fleam.runtime.taskmanager.output.ResultSubpartitionView;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LocalInputChannel extends InputChannel implements BufferAvailabilityListener {

    private final Object requestLock = new Object();

    private final ResultPartitionManager partitionManager;

    private final TaskEventDispatcher eventDispatcher;

    private volatile ResultSubpartitionView resultSubpartitionView;

    private final AtomicInteger numBytesIn;

    private final AtomicLong numBuffersAvailable = new AtomicLong();

    public LocalInputChannel(SingleInputGate inputGate, ResultPartitionManager partitionManager,
                             TaskEventDispatcher taskEventDispatcher,) {
        super(inputGate);
        this.partitionManager = partitionManager;
        this.eventDispatcher = taskEventDispatcher;
    }

    @Override
    public void requestSubpartition(int subpartitionIndex) throws IOException {
        boolean retriggerRequest = false;

        synchronized (requestLock) {
            if (resultSubpartitionView == null) {
                try {
                    ResultSubpartitionView subpartitionView = partitionManager.createSubpartitionView(
                            resultPartitionID, subpartitionIndex, this);

                    if (subpartitionView == null) {
                        throw new IOException("Error requesting subpartition.");
                    }

                    // make the subpartition view visible
                    this.resultSubpartitionView = subpartitionView;

                } catch (PartitionNotFoundException notFound) {
                    if (increaseBackoff()) {
                        retriggerRequest = true;
                    } else {
                        throw notFound;
                    }
                }
            }
        }

        //TODO: retrigger request subPartition
    }


    @Override
    public void notifyBuffersAvailable(long numBuffers) {
        // if this request made the channel non-empty, notify the input gate
        if (numBuffers > 0 && numBuffersAvailable.getAndAdd(numBuffers) == 0) {
            notifyChannelNonEmpty();
        }
    }

    @Override
    public BufferAndAvailability getNextBuffer() throws IOException {
        ResultSubpartitionView resultSubpartitionView = this.resultSubpartitionView;
        Buffer nextBuffer = resultSubpartitionView.getNextBuffer();

        long remaining = numBuffersAvailable.decrementAndGet();

        if (remaining >= 0) {
            numBytesIn.addAndGet(nextBuffer.getSizeUnsafe());
            return new BufferAndAvailability(nextBuffer, remaining > 0);
        } else {
            throw new IOException("result subpartition is released");
        }
    }


}
