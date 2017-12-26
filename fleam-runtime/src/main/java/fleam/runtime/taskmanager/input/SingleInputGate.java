package fleam.runtime.taskmanager.input;

import fleam.runtime.jobgraph.IntermediateDataSetID;
import fleam.runtime.jobgraph.IntermediateResultPartitionID;
import fleam.runtime.network.NetworkBufferPool;
import fleam.runtime.network.buffer.Buffer;
import fleam.runtime.network.buffer.BufferPool;
import fleam.runtime.taskmanager.input.InputChannel.BufferAndAvailability;
import fleam.runtime.taskmanager.output.ResultPartitionType;
import fleam.util.JobID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Map;

public class SingleInputGate implements InputGate {

    private static final Logger LOG = LoggerFactory.getLogger(SingleInputGate.class);

    private final Object requestLock = new Object();

    private final IntermediateDataSetID intermidateResultID;

    private final BufferPool bufferPool;

    private boolean hasRequestedPartitions;

    /** recycle directly to networkBufferPool if the channel is released */
    private final NetworkBufferPool networkBufferPool;

    private final String owingTaskName;

    private final JobID jobID;

    private final int numOfInputChannels;

    /** consumed subpartition index in the target partition */
    private final int consumedSubpartitionIndex;

    private final Map<IntermediateResultPartitionID,InputChannel> inputChannels;

    private final ResultPartitionType consumedPartitionType;

    private final ArrayDeque<InputChannel> inputChannelsWithData = new ArrayDeque<>();



    @Override
    public void notifyChannelNonEmpty(InputChannel channel) {
        synchronized (inputChannelsWithData){
            int numAvailableChannels = inputChannelsWithData.size();
            if (numAvailableChannels == 0) {
                inputChannelsWithData.notifyAll();
            }
            inputChannelsWithData.add(channel);
        }
    }

    @Override
    public BufferOrEvent getNextBufferOrEvent() throws IOException {
        requestPartitions();

        InputChannel currentChannel;
        boolean moreAvailable;
        synchronized (inputChannelsWithData){
            currentChannel = inputChannelsWithData.remove();
            moreAvailable = inputChannelsWithData.size() > 0;
        }
        final BufferAndAvailability nextBuffer = currentChannel.getNextBuffer();

        // this moreAvailable means the channel from which this buffer gets has remaining buffers
        // so we need to add it into the inputChannelsWithData again;
        if (nextBuffer.moreAvailable()) {
            notifyChannelNonEmpty(currentChannel);
        }
        final Buffer buffer = nextBuffer.buffer();
        if (buffer.isBuffer()) {
            // this moreAvailabe means more channels has data, not only this one
            return new BufferOrEvent(buffer, currentChannel.getChannelIndex(), moreAvailable);
        } else{ // TODO: why has this else???

            //TODO: deal with the event

        }
    }

    @Override
    public void requestPartitions() throws IOException{
        synchronized (requestLock){
            if(!hasRequestedPartitions){
                for (InputChannel inputChannel : inputChannels.values()) {
                    inputChannel.requestSubpartition(consumedSubpartitionIndex);
                }
            }
            hasRequestedPartitions = true;
        }
    }



}
