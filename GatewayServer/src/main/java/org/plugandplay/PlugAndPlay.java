package org.plugandplay;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.command.Command;
import org.factory.Factory;
import org.observer.Callback;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class PlugAndPlay {
    private final String folderPath;
    private final Factory<String, Map<String, String>, Command> factory;
    private final DirWatcher watcher;
    private final DynamicJarLoader loader;

    public PlugAndPlay(String folderPath, Factory<String, Map<String, String>, Command> factory) throws ClassNotFoundException {
        this.folderPath = folderPath;
        this.factory = factory;
        loader = new DynamicJarLoader(Command.class.getName());

        try {
            watcher = new DirWatcher(folderPath); //
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void start() {
        watcher.registerCallback(new Callback<>(this::handleEvent,
                                                ()-> System.out.println("Service stopped")));
        watcher.start();
    }

    public void handleEvent(List<WatchEvent<?>> events) {
        Set<Path> newClasses = new HashSet<>();

        for (WatchEvent<?> event : events) {
            if (event.kind() != OVERFLOW) {
                newClasses.add((Path)event.context());
            }
        }

        for (Path clazz : newClasses){
            String fullPath = folderPath + "/" + clazz;
            if (!JarValidator.validate(fullPath)) {
                continue;
            }
            List<Class<?>> loadedClasses = loader.load(fullPath);
            for (Class<?> c : loadedClasses) {
                factory.add(c.getName(), (args) -> {
                    try {
                        return (Command) c.getDeclaredConstructor(Map.class).newInstance(args);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    public void stop() {
        try {
            watcher.stopService();
        } catch (IOException | InterruptedException ignore) {}
    }
}
