package fleam.runtime.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class IOManager {
    private static final Logger LOG = LoggerFactory.getLogger(IOManager.class);

    private final WriterThread[] writers;

    private final ReaderThread[] readers;

    private final AtomicBoolean isShutdown = new AtomicBoolean();

    /** Shutdown hook to make sure that the directories are removed on exit */
    private final Thread shutdownHook;

    public IOManager(){

    }

    public void shutDown(){

    }


    public boolean isProperlyShutDown(){

    }




    public static final class ReaderThread extends Thread{
        private final RequestQueue<IOReadRequest> readRequestQueue;
        private volatile boolean isAlive;

        protected ReaderThread(){
            this.readRequestQueue = new RequestQueue<IOReadRequest>();
        }

        protected void shutDown(){

        }


        @Override
        public void run(){

        }
    }

    public static final class WriterThread extends Thread{
        private final RequestQueue<IOWriteRequest> writeRequestQueue;

        private volatile boolean isAlive;

        protected WriterThread(){
            this.writeRequestQueue = new RequestQueue<IOWriteRequest>();
        }

        protected void shutDown(){

        }

        @Override
        public void run(){

        }
    }


}

