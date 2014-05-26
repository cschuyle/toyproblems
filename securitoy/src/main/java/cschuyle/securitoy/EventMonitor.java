package cschuyle.securitoy;

import com.codahale.metrics.Timer;

import java.io.IOException;
import java.nio.file.*;

import static cschuyle.securitoy.EventCategorizer.EventCategory.ALARM;
import static cschuyle.securitoy.EventCategorizer.EventCategory.IMG;
import static java.nio.file.StandardWatchEventKinds.*;

// File watch docs can be found here --> http://docs.oracle.com/javase/tutorial/essential/io/notification.html
// Most of the code in this file is shamelessly ripped from that link.

/**
 * Can be used to watch any number (limited by the number of Threads) of directories for new/modified files, by calling monitorFolder().
 */
public class EventMonitor {

    private final EventCategorizer eventCategorizer;
    private final StatisticsRepository stats;
    private final WatchService watcher;

    public EventMonitor(EventCategorizer eventCategorizer, StatisticsRepository stats) throws IOException {
        this.eventCategorizer = eventCategorizer;
        this.stats = stats;
        this.watcher = FileSystems.getDefault().newWatchService();
    }

    /**
     *
     * @param dirname The name of the folder to monitor for new/modified files.  As files are detected,
     *                they will be processed by the eventProcessor.
     * @return A Thread which will start monitoring the requested folder when you call start().
     * @throws Exception
     */
    public Thread monitorFolder(String dirname) throws IOException {
        return new Thread(new MonitoredFolder(dirname));
    }

    private class MonitoredFolder implements Runnable {

        private final Path dir;
        private final String dirname;

        public MonitoredFolder(String dirname) throws IOException {
            this.dirname = dirname;
            this.dir = FileSystems.getDefault().getPath(dirname);
            this.dir.register(watcher,
                              ENTRY_CREATE,
                              ENTRY_MODIFY);
            // TODO: Whatever files are in the directory at the beginning, do I need to process them?  Will take more thought.
        }

        @Override
        public void run() {
            for (;;) {
                try {
                    if( ! processEvents(watcher.take())) {
                        System.out.println("Hmmm, your directory, '" + dirname + "', seems to have evaporated.  Goodbye.");
                        break;
                    }
                } catch (InterruptedException x) {
                    break;
                }
            }
            System.out.println("No more file change events in folder '"+ dirname +"' for you.");
        }

        /** Process any events that are available on the watched resource (the key).
         *
         * @param key
         * @return false when the key can no longer receive events (e.g. when the directory is deleted)
         */
        private boolean processEvents(WatchKey key) {
            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == OVERFLOW) {
                    System.out.println("Overflow.  Carry on.");
                    continue;
                }

                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                Path filename = ev.context();

                Timer.Context timer = stats.time();
                try {
                    Path child = dir.resolve(filename);
                    processFile(child);
                } catch (Exception e) {
                    System.out.println("Unexpected exception in event loop" + e);
                    e.printStackTrace();
                } finally {
                    timer.stop();
                }
            }
            return key.reset();
        }

        private void processFile(Path filename) throws IOException {
            EventCategorizer.EventCategory eventCategory = eventCategorizer.processFile(filename);
            System.out.println("PROCESSED " + filename + " and categorized as " + eventCategory);
            if(eventCategory == ALARM) {
                stats.markEvent(StatisticsRepository.ALARM);
            } else if(eventCategory == IMG) {
                stats.markEvent(StatisticsRepository.IMG);
            } else {
                stats.markEvent();
            }
        }
    }
}
