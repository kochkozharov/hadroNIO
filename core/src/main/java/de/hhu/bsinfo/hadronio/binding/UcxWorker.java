package de.hhu.bsinfo.hadronio.binding;

import java.io.Closeable;

public interface UcxWorker extends Closeable {

    boolean progress();

    void waitForEvents();

    void interrupt();

    boolean arm();

    int getEventFileDescriptor();

    default boolean drain() {
        boolean currentProgress = progress();
        boolean eventsPolled = currentProgress;

        while (currentProgress) {
            currentProgress = progress();
        }

        return eventsPolled;
    }
}
