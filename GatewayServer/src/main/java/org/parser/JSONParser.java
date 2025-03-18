package org.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class JSONParser implements RPSParser<Map<String, String>, JsonObject> {
    /*
        example:
        { command: "regCompany",
          args: {
             name: "Apple",
             address: "343 indian road crescent"
             }
        }
        */

    @Override
    public Map<String, String> parse(JsonObject json) {
        Map<String, String> resultMap = new HashMap<>();

        if (json == null || !json.has("command") || !json.has("args")) {
            return null; // Return null if format is incorrect
        }

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            JsonElement valueElement = entry.getValue();

            if (valueElement.isJsonObject()) {
                JsonObject nestedObject = valueElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> nestedEntry : nestedObject.entrySet()) {
                    resultMap.put(nestedEntry.getKey(), nestedEntry.getValue().getAsString());
                }
            } else {
                resultMap.put(key, valueElement.getAsString());
            }
        }

        return resultMap;
    }

    public static void main(String[] args) {
        JsonObject json = new JsonObject();
        JsonObject nestedJson = new JsonObject();

        json.addProperty("command", "regCompany");
        nestedJson.addProperty("name", "Apple");
        nestedJson.addProperty("address", "343 indian road crescent");
        json.add("args", nestedJson);

        JSONParser parser = new JSONParser();
        Map<String, String> result = parser.parse(json);

        System.out.println(result);
    }
}
