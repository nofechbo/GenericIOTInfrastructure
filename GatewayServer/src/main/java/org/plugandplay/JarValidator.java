package org.plugandplay;

public class JarValidator {

    public static boolean validate(String filePath) {
        return filePath.endsWith(".jar");
    }

}
