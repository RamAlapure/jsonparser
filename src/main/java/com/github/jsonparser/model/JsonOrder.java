package com.github.jsonparser.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The utility class to handle json order. Keeping Order:
 * 1. JSON primitive
 * 2. JSON Object
 * 3. JSON Array
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 17/02/2020
 */
public class JsonOrder {

    private static Type type = new TypeToken<Map<String, Object>>() {
    }.getType();

    private static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Double.class, (JsonSerializer<Double>) JsonOrder::serialize);
        gson = gsonBuilder.create();
    }

    public static JsonElement orderJson(JsonElement ele) {
        Map<String, Object> jsonPre = new LinkedHashMap<>();
        Map<String, Object> jsonArr = new LinkedHashMap<>();
        Map<String, Object> jsonObj = new LinkedHashMap<>();

        //converting JsonElement to Map
        Map<String, Object> origMap = gson.fromJson(ele, type);

        //Iterating the Map object to to get type of Object
        for (Map.Entry<String, Object> entry : origMap.entrySet()) {
            try {
                //adding check if value of key in json is null
                if (entry.getValue() == null || entry.getValue().getClass().getSimpleName().equals("ArrayList")) {
                    //if Object is of type ArrayList push it to jsonArr Map
                    jsonArr.put(entry.getKey(), entry.getValue());
                } else if (entry.getValue().getClass().getSimpleName().equals("LinkedTreeMap")) {
                    jsonObj.put(entry.getKey(), entry.getValue());
                } else {
                    //if Object is of type Primitive push it to jsonPre.
                    jsonPre.put(entry.getKey(), entry.getValue());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        //appending jsonArr map to jsonPre map in order to maintain order.
        jsonPre.putAll(jsonObj);
        jsonPre.putAll(jsonArr);

        //reconstructing the JSON from Map Objects and returning
        return gson.toJsonTree(jsonPre, LinkedHashMap.class);
    }

    private static JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == src.longValue())
            return new JsonPrimitive(src.longValue());
        return new JsonPrimitive(src);
    }
}
