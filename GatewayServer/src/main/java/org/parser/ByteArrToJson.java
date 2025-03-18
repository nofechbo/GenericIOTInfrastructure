package org.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;

public class ByteArrToJson {


    public static JsonObject convertByteArrToJson(byte[] buffer) {
        if (buffer == null || buffer.length == 0) {
            return null; // Return null or an empty JsonObject
        }
        String jsonString = new String(buffer, StandardCharsets.UTF_8);
        return JsonParser.parseString(jsonString).getAsJsonObject();
    }

}


