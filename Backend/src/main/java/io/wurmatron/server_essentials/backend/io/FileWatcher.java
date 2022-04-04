package io.wurmatron.server_essentials.backend.io;

import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class FileWatcher {

    private final File trackedDir;
    private final WatchKey watchKey;

    private NonBlockingHashMap<String, EventAction> files;

    /**
     * @param service
     * @param trackedDirectory
     * @throws IOException
     */
    public FileWatcher(WatchService service, File trackedDirectory) throws IOException {
        this.trackedDir = trackedDirectory;
        watchKey = trackedDirectory.toPath().register(service, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
        files = new NonBlockingHashMap<>();
        ServerEssentialsBackend.scheduledService.scheduleAtFixedRate(handleFileChangeEvent(), 0, ServerEssentialsBackend.backendConfiguration != null ? ServerEssentialsBackend.backendConfiguration.General.fileResyncInterval : 30, TimeUnit.SECONDS);
    }

    /**
     * @param eventRunnable function to run when an event has occurred
     * @param name          name used to register a file to be tracked
     * @param ignoreCase    ignore the case of the file
     * @param backupMode    style of how the backup file is handled
     */
    public void track(TrackedFile eventRunnable, String name, boolean ignoreCase, BackupMode backupMode) {
        files.put(name, new EventAction(ignoreCase, backupMode, eventRunnable));
    }

    /**
     * Track a file for changes and act accordingly
     *
     * @param eventRunnable function to run when an event has occurred
     * @param name          name used to register a file to be tracked
     */
    public void track(TrackedFile eventRunnable, String name) {
        track(eventRunnable, name, true, BackupMode.BACKUP_FOLDER);
    }

    /**
     * Remove an entry from the list of tracked files
     *
     * @param name name used to register when it was tracked
     */
    public void untrack(String name) {
        files.remove(name);
    }

    /**
     * Runs every x seconds to each for file update and send out updates via events
     */
    private Runnable handleFileChangeEvent() {
        return () -> {
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                if (!event.kind().equals(StandardWatchEventKinds.OVERFLOW)) {
                    WatchEvent<Path> ev = cast(event);
                    Path fileName = ev.context();
                    boolean bak = fileName.endsWith(".bak");
                    if (bak)
                        fileName = Paths.get(fileName.toString().substring(0, fileName.getFileName().toString().length() - 4));
                    // TODO Implement ignoreCase
                    if (files.containsKey(fileName.toString())) {
                        EventAction trackedRunner = files.get(fileName.toString());
                        // TODO Implement Backup mode
                        trackedRunner.trackedFile.event(event, fileName, bak);
                    }
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Code that is set to run when a tracked file or directory had a requested event occur
     */
    @FunctionalInterface
    public interface TrackedFile {

        /**
         * @param kind     type of event that has occurred
         * @param filename name of the file that has fired the event
         */
        abstract void event(WatchEvent<?> kind, Path filename, boolean bak);
    }

    /**
     * IGNORE = Do nothing and ignore the update requests
     * DELETE_ON_COMPLETE = Upon finishing reloading without errors the backup will be deleted
     * BACKUP_FOLDER = Move backup files to a seperated nba
     */
    public enum BackupMode {
        IGNORE, DELETE_ON_COMPLETE, BACKUP_FOLDER
    }

    // Wrapper
    private class EventAction {

        protected boolean ignoreCase;
        protected BackupMode backupMode;
        protected TrackedFile trackedFile;

        public EventAction(boolean ignoreCase, BackupMode backupMode, TrackedFile trackedFile) {
            this.ignoreCase = ignoreCase;
            this.backupMode = backupMode;
            this.trackedFile = trackedFile;
        }
    }
}
