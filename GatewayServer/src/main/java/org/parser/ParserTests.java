package org.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class ParserTests {
        @Test
        void testSingleKeyValuePair() {
            StringParser parser = new StringParser();
            String input = "regCompany&companyName@Apple";
            Map<String, String> result = parser.parse(input);

            assertEquals(2, result.size());
            assertEquals("regCompany", result.get(null));
            assertEquals("Apple", result.get("companyName"));
        }

        @Test
        void testMultipleKeyValuePairs() {
            StringParser parser = new StringParser();
            String input = "regCompany&companyName@Apple#companyID@1234";
            Map<String, String> result = parser.parse(input);

            assertEquals(3, result.size());
            assertEquals("regCompany", result.get(null));
            assertEquals("Apple", result.get("companyName"));
            assertEquals("1234", result.get("companyID"));
        }

        @Test
        void testEmptyValueValidInput() {
            StringParser parser = new StringParser();
            String input = "regCompany&companyName@#companyID@1234";
            Map<String, String> result = parser.parse(input);

            assertEquals(3, result.size());
            assertEquals("regCompany", result.get(null));
            assertEquals("", result.get("companyName"));
            assertEquals("1234", result.get("companyID"));
        }

        @Test
        void testOnlyCommand() {
            StringParser parser = new StringParser();
            String input = "regCompany";
            Map<String, String> result = parser.parse(input);

            assertNull(result);
        }

        @Test
        void testEmptyKey() {
            StringParser parser = new StringParser();
            String input = "regCompany&@value#companyID@1234";
            Map<String, String> result = parser.parse(input);

            assertEquals(3, result.size());
            assertEquals("regCompany", result.get(null));
            assertEquals("value", result.get(""));
            assertEquals("1234", result.get("companyID"));
        }

        @Test
        void testTrailingDelimiter() {
            StringParser parser = new StringParser();
            String input = "regCompany&key@value#";
            Map<String, String> result = parser.parse(input);

            assertEquals(2, result.size());
            assertEquals("regCompany", result.get(null));
            assertEquals("value", result.get("key"));
        }
}

