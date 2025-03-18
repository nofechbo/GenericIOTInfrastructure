package org.plugandplay;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

import static java.nio.file.StandardWatchEventKinds.*;

import org.observer.Callback;
import org.observer.Dispatcher;

public class DirWatcher {
    private final Dispatcher<List<WatchEvent<?>>> dispatcher;
    private final WatchService watcher;
    private Thread watcherThread;
    private volatile boolean isThreadWorking = true;

    public DirWatcher(String jarFolder) throws IOException {
        jarFolder = Objects.requireNonNull(jarFolder, "jar folder must not be null");
        dispatcher = new Dispatcher<>();
        watcher = FileSystems.getDefault().newWatchService();

        File tempJarFile = new File(jarFolder);
        if (!tempJarFile.isDirectory()) {
            throw new IllegalArgumentException("must provide a folder");
        }
        Path jar = Paths.get(jarFolder);

        jar.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
    }

    public void registerCallback(Callback<List<WatchEvent<?>>> call) {
        dispatcher.subscribe(call);
    }

    public void stopService() throws IOException, InterruptedException {
        isThreadWorking = false;
        watcher.close();
        dispatcher.stopService();
        watcherThread.join();
    }

    public void start() {
        watcherThread = new Thread(new WatcherThread());
        watcherThread.start();
    }

    private class WatcherThread implements Runnable {
        @Override
        public void run(){
            while (isThreadWorking) {
                WatchKey key = null;
                try {
                    key = watcher.take();
                } catch (InterruptedException | ClosedWatchServiceException e) {
                    continue;
                }

                dispatcher.publish(key.pollEvents());

                if (!key.reset()) {
                    throw new RuntimeException("failure resetting key");
                }
            }
        }
    }
}
