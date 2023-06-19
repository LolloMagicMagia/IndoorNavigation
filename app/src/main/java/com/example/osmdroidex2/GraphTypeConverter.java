package com.example.osmdroidex2;
import androidx.room.TypeConverter;
import com.example.osmdroidex2.Graph;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GraphTypeConverter {

    @TypeConverter
    public static String graphToString(Graph graph) {
        if(graph!=null){
        ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(graph);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            return null;
            }
        }else{
            return null;
        }
    }

    @TypeConverter
    public static Graph stringToGraph(String graphString) {
        if(graphString != null){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(graphString, Graph.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    @TypeConverter
    public static List<Graph.Node> nodeListFromString(String nodeListString) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Graph.Node>>() {}.getType();
        return gson.fromJson(nodeListString, type);
    }

    @TypeConverter
    public static String nodeListToString(List<Graph.Node> nodeList) {
        Gson gson = new Gson();
        return gson.toJson(nodeList);
    }

    @TypeConverter
    public static List<Graph.Edge> edgeListFromString(String edgeListString) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Graph.Edge>>() {}.getType();
        return gson.fromJson(edgeListString, type);
    }

    @TypeConverter
    public static String edgeListToString(List<Graph.Edge> edgeList) {
        Gson gson = new Gson();
        return gson.toJson(edgeList);
    }
}
