package org.plugandplay;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

import org.observer.Callback;

class DirWatcherTest {

    @Test
    void start() throws IOException, InterruptedException {
        DirWatcher watcher = new DirWatcher("/home/nofech/Downloads");

        watcher.registerCallback(new Callback<>((d)->{
            for (WatchEvent<?> e : d) {
                System.out.println(((Path)e.context()).toUri().getPath());
            }
        }, ()->{
            System.out.println("Stopped Service");
        }));

        watcher.start();

        Thread.sleep(5000);

        watcher.stopService();
    }
}
