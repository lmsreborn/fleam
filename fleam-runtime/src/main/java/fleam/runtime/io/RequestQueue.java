package fleam.runtime.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestQueue<E> extends LinkedBlockingQueue<E> implements Closeable{
    private static final Logger LOG = LoggerFactory.getLogger(RequestQueue.class);

    private volatile boolean isClosed = false;

    @Override
    public void close(){
        this.isClosed = true;
    }

    public boolean isClosed(){
        return this.isClosed;
    }

}
