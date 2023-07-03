package com.example.osmdroidex2;

import android.util.Log;

import androidx.room.TypeConverter;

import java.util.HashMap;
import java.util.Map;

public class MapTypeConverter {
    @TypeConverter
    public static String mapToString(Map<Character, Graph> piani) {
        StringBuilder stringBuilder = new StringBuilder();

        if (piani != null && !piani.isEmpty()) {
            for (Character key : piani.keySet()) {
                Graph graph = piani.get(key);
                String graphString = GraphTypeConverter.graphToString(graph);

                stringBuilder.append(key);
                stringBuilder.append("::");
                stringBuilder.append(graphString);
                stringBuilder.append(",,");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1); // Rimuove l'ultima virgola
        }

        return stringBuilder.toString();
    }

    @TypeConverter
    public static Map<Character, Graph> stringToMap(String value) {
        Map<Character, Graph> piani = new HashMap<>();

        if (value != null && !value.isEmpty()) {
            String[] entries = value.split(",,");
            for (String entry : entries) {
                String[] keyValue = entry.split("::");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    char charKey = key.charAt(0);  // Converti la stringa in un carattere

                    String graphString = keyValue[1];
                    Graph graph = GraphTypeConverter.stringToGraph(graphString);

                    if (graph != null) {
                        piani.put(charKey, graph);
                    } else {
                        Log.e("GraphTypeConverter", "Failed to convert graph for key: " + key);
                    }
                }
            }
        } else {
            Log.e("GraphTypeConverter", "Empty or null value provided");
        }

        return piani;
    }
    }



