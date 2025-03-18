package org.plugandplay;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DynamicJarLoader {
    private final Class<?> implementedInterface;

    public DynamicJarLoader(String implementedInterface) throws ClassNotFoundException {
        this.implementedInterface = Class.forName(implementedInterface);
    }

    public List<Class<?>> load(String classPath) {
        List<Class<?>> loadedClasses = new ArrayList<>();
        File tempJarFile = new File(classPath);

        try(JarFile jarFile = new JarFile(classPath);
            URLClassLoader jarLoader = new URLClassLoader(new URL[]{
                    new URL("jar", "", tempJarFile.toURI().toURL() + "!/")});) {

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .replace("/", ".")
                            .replace(".class", "");
                    try {
                        Class<?> clazz = jarLoader.loadClass(className);
                        if (validateIsCommand(clazz)) {
                            loadedClasses.add(clazz);
                        }
                    } catch (ClassNotFoundException ignore) {
                        System.err.println("Failed to load class: " + className);
                    }
                }
            }

            return loadedClasses;

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private boolean validateIsCommand(Class<?> clazz) {
        return implementedInterface.isAssignableFrom(clazz) && !clazz.isInterface();
    }

}

