package org.factory;

import java.util.NoSuchElementException;
import java.util.function.Function;

public class FactoryTest {
    public static void main(String[] args) {
        Factory<String, String, Object> factory = new Factory<>();

        //Lambda expression
        factory.add("Custom", data -> "Custom object with data: " + data);
        System.out.println(factory.create("Custom", "Lambda"));

        try { //should catch exception
            factory.create("Customize", "Lambda");
        } catch (NoSuchElementException e) {
            System.out.println("IllegalArgumentException caught");
        }
        //Constructor reference
        factory.add("StringBuilder", StringBuilder::new);
        System.out.println(((StringBuilder) factory.create("StringBuilder", "Hello")).append(" World!"));

        // Reference to an instance method of an arbitrary object of aparticular type
        factory.add("ArbitraryInstance", String::length);
        System.out.println(factory.create("ArbitraryInstance", "Tzur the great!"));

        //Anonymous class
        factory.add("AnonymousClass", new Function<String, Object>() {
            @Override
            public Object apply(String data) {
                return "Anonymous Class with data: " + data;
            }
        });
        System.out.println(factory.create("AnonymousClass", "Anonymous Class Example"));

        //Static method reference
        factory.add("Integer", Integer::parseInt);
        System.out.println(factory.create("Integer", "123"));

        // complex lambda expression
        factory.add("ComplexObject", data -> {
            return new ComplexObject("Complex Object: " + data, data.length());
        });


        // nested factory test
        Factory<String, String, Factory<String, String, Object>> nestedFactory = new Factory<>();
        nestedFactory.add("Nested", unused -> factory);
        Factory<String, String, Object> retrievedFactory = nestedFactory.create("Nested", null);
        System.out.println("Nested Factory Test: " + retrievedFactory.create("Custom", "Nested Example"));

        // Complex object test
        ComplexObject complex = (ComplexObject) factory.create("ComplexObject", "Test Data");
        System.out.println("Complex Object Test: " + complex);


    }
}

// Complex object for testing
class ComplexObject {
    private final String description;
    private final int size;

    public ComplexObject(String description, int size) {
        this.description = description;
        this.size = size;
    }

    @Override
    public String toString() {
        return "ComplexObject{description = \"" + description + "\", size = " + size + "}";
    }

}
