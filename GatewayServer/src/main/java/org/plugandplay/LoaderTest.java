package org.plugandplay;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class LoaderTest {
        @Test
        public void testClassLoader() {
            DynamicJarLoader check = null;
            try {
                check = new DynamicJarLoader(Runnable.class.getName());
            } catch (ClassNotFoundException e) {
                System.out.println("caught ClassNotFoundException");
            }
            List<Class<?>> loadedClasses = check.load("/home/nofech/dev/java/Exercises/src/il/co/ilrd/Outer/Outer.jar");

            System.out.println(loadedClasses.size());

            for (Class<?> c : loadedClasses) {
                System.out.println(c.getName());
            }
        }
    }

